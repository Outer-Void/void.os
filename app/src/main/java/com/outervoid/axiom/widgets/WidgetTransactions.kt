package com.outervoid.axiom.widgets

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID

enum class WidgetStage { Prepared, Binding, Configuring }

data class WidgetOperation(
    val id: String,
    val panel: Int,
    val candidate: Int,
    val provider: String,
    val stage: WidgetStage = WidgetStage.Prepared
) {
    fun encode(): String = listOf("1", id, panel, candidate, provider, stage.name).joinToString("|")

    companion object {
        fun decode(raw: String?): WidgetOperation? {
            val p = raw?.split('|') ?: return null
            if (p.size != 6 || p[0] != "1") return null
            val panel = p[2].toIntOrNull()?.takeIf { it in 1..4 } ?: return null
            val candidate = p[3].toIntOrNull()?.takeIf { it > 0 } ?: return null
            val stage = WidgetStage.entries.firstOrNull { it.name == p[5] } ?: return null
            if (!p[1].matches(Regex("[a-zA-Z0-9-]{1,80}")) || !p[4].contains('/') || p[4].contains('|')) return null
            return WidgetOperation(p[1], panel, candidate, p[4], stage)
        }
    }
}

data class WidgetSnapshot(
    val panels: Map<Int, Int> = emptyMap(),
    val pending: WidgetOperation? = null,
    val garbage: Set<Int> = emptySet(),
    val malformed: Boolean = false
)

interface WidgetStore {
    suspend fun read(): WidgetSnapshot
    /** Returns only after the entire snapshot has been durably written. No default-on-IO-error reads. */
    suspend fun update(transform: (WidgetSnapshot) -> WidgetSnapshot): WidgetSnapshot
}

interface WidgetIds {
    fun allocate(): Int
    fun owned(): Set<Int>
    fun provider(id: Int): String?
    fun delete(id: Int)
}

/** Serializes operations; the durable panel pointer is the commit record. */
class WidgetTransactions(private val store: WidgetStore, private val ids: WidgetIds) {
    private val mutex = Mutex()
    private suspend fun <T> serialized(block: suspend () -> T): T = withContext(Dispatchers.IO) {
        mutex.withLock { block() }
    }

    suspend fun begin(panel: Int, provider: String): WidgetOperation = serialized {
        require(panel in 1..4 && provider.contains('/') && !provider.contains('|'))
        val initial = store.read()
        check(!initial.malformed) { "Reset damaged widget records in the widget picker." }
        check(initial.pending == null) { "Finish or cancel the current widget setup first." }
        val candidate = ids.allocate()
        val op = WidgetOperation(UUID.randomUUID().toString(), panel, candidate, provider)
        // A write failure leaves the old assignment intact; recovery sweeps the newly allocated orphan.
        store.update { state ->
            check(state.pending == null) { "Another widget setup is active." }
            state.copy(pending = op)
        }
        op
    }

    suspend fun advance(id: String, expected: WidgetStage, next: WidgetStage): WidgetOperation? = serialized {
        var result: WidgetOperation? = null
        store.update { state ->
            val op = state.pending
            if (op?.id != id || op.stage != expected) state else {
                require((expected == WidgetStage.Prepared && next == WidgetStage.Binding) ||
                    (expected == WidgetStage.Binding && next == WidgetStage.Configuring))
                result = op.copy(stage = next)
                state.copy(pending = result)
            }
        }
        result
    }

    suspend fun commit(id: String, stage: WidgetStage): Boolean = serialized {
        val state = store.read()
        val op = state.pending?.takeIf { it.id == id && it.stage == stage } ?: return@serialized false
        check(op.candidate in ids.owned() && ids.provider(op.candidate) == op.provider) { "Widget provider is no longer available." }
        check(state.panels.values.none { it == op.candidate }) { "Widget is already assigned." }
        store.update { current ->
            check(current.pending == op && current.panels == state.panels) { "Widget assignment changed; retry setup." }
            val old = current.panels[op.panel]
            current.copy(panels = current.panels + (op.panel to op.candidate), pending = null,
                garbage = current.garbage + listOfNotNull(old))
        }
        cleanupLocked(sweep = false)
        true
    }

    suspend fun cancel(id: String): Boolean = serialized {
        var cancelled = false
        store.update { state ->
            val op = state.pending
            if (op?.id != id) state else {
                cancelled = true
                state.copy(pending = null, garbage = state.garbage + op.candidate)
            }
        }
        cleanupLocked(sweep = false)
        cancelled
    }

    suspend fun remove(panel: Int) = serialized {
        store.update { state ->
            check(state.pending?.panel != panel) { "Cancel widget setup before removing this panel." }
            state.copy(panels = state.panels - panel, garbage = state.garbage + listOfNotNull(state.panels[panel]))
        }
        cleanupLocked(sweep = false)
    }

    suspend fun recover() = serialized { cleanupLocked(sweep = true) }

    private suspend fun cleanupLocked(sweep: Boolean) {
        val state = store.read()
        check(!state.malformed) { "Reset damaged widget records in the widget picker." }
        val retained = state.panels.values.toSet() + listOfNotNull(state.pending?.candidate)
        val owned = ids.owned()
        // Never infer abandoned ownership from malformed records; preserve IDs for explicit repair.
        val garbage = state.garbage + if (sweep && !state.malformed) owned - retained else emptySet()
        store.update { it.copy(garbage = garbage - retained) }
        for (id in garbage - retained) {
            if (id in ids.owned()) ids.delete(id)
            // Crash between delete and acknowledgement is safe: next recovery sees a non-owned ID.
            store.update { it.copy(garbage = it.garbage - id) }
        }
    }
}
