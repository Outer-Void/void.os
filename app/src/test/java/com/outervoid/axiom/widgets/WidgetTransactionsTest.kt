package com.outervoid.axiom.widgets

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.io.IOException

class WidgetTransactionsTest {
    private class Store(var state: WidgetSnapshot = WidgetSnapshot()) : WidgetStore {
        var fail = false
        override suspend fun read() = state
        override suspend fun update(transform: (WidgetSnapshot) -> WidgetSnapshot): WidgetSnapshot {
            if (fail) throw IOException("disk full")
            return transform(state).also { state = it }
        }
    }
    private class Ids : WidgetIds {
        var next = 10
        val all = mutableSetOf(1)
        val providers = mutableMapOf(1 to "old/.Widget")
        val deleted = mutableListOf<Int>()
        var failDelete = false
        override fun allocate(): Int = next++.also { all.add(it) }
        override fun owned() = all.toSet()
        override fun provider(id: Int) = providers[id]
        override fun delete(id: Int) {
            if (failDelete) throw IllegalStateException("host unavailable")
            all.remove(id); deleted.add(id)
        }
    }
    private val store = Store(WidgetSnapshot(panels = mapOf(1 to 1)))
    private val ids = Ids()
    private val engine = WidgetTransactions(store, ids)
    private suspend fun ready(): WidgetOperation {
        val op = engine.begin(1, "new/.Widget")
        ids.providers[op.candidate] = op.provider
        return engine.advance(op.id, WidgetStage.Prepared, WidgetStage.Binding)!!
    }

    @Test fun replacementDeletesOldOnlyAfterDurableCommit() = runBlocking {
        val op = ready()
        assertEquals(1, store.state.panels[1]); assertTrue(ids.deleted.isEmpty())
        assertTrue(engine.commit(op.id, op.stage))
        assertEquals(op.candidate, store.state.panels[1]); assertEquals(listOf(1), ids.deleted)
        assertNull(store.state.pending)
    }
    @Test fun firstInstallationAndRemoval() = runBlocking {
        store.state = WidgetSnapshot()
        ids.all.clear()
        val op = ready()
        assertTrue(engine.commit(op.id, op.stage))
        engine.remove(1)
        assertTrue(store.state.panels.isEmpty()); assertEquals(listOf(op.candidate), ids.deleted)
        engine.remove(1)
        assertEquals(1, ids.deleted.size)
    }
    @Test fun cancellationIsIdempotentAndPreservesOldWidget() = runBlocking {
        val op = ready()
        assertTrue(engine.cancel(op.id)); assertFalse(engine.cancel(op.id))
        assertFalse(engine.commit(op.id, op.stage))
        assertEquals(mapOf(1 to 1), store.state.panels); assertEquals(listOf(op.candidate), ids.deleted)
    }
    @Test fun persistenceFailureRetainsBothIdsAndAllowsRetry() = runBlocking {
        val op = ready()
        store.fail = true
        try { engine.commit(op.id, op.stage); fail("Expected disk failure") } catch (_: IOException) { }
        assertEquals(1, store.state.panels[1]); assertTrue(ids.deleted.isEmpty())
        store.fail = false
        assertTrue(engine.commit(op.id, op.stage))
    }
    @Test fun failedBeginLeavesRecoverableOrphanAndOldWidget() = runBlocking {
        store.fail = true
        try { engine.begin(1, "new/.Widget"); fail("Expected disk failure") } catch (_: IOException) { }
        store.fail = false
        engine.recover()
        assertEquals(setOf(1), ids.all); assertEquals(1, store.state.panels[1])
    }
    @Test fun duplicateAndWrongStageCallbacksCannotReplaceTwice() = runBlocking {
        val op = ready()
        assertFalse(engine.commit(op.id, WidgetStage.Configuring))
        assertTrue(engine.commit(op.id, WidgetStage.Binding))
        assertFalse(engine.commit(op.id, WidgetStage.Binding))
        assertEquals(listOf(1), ids.deleted)
    }
    @Test fun rapidRepeatedSelectionDoesNotAllocateSecondCandidate() = runBlocking {
        ready()
        try { engine.begin(2, "other/.Widget"); fail("Expected busy operation") } catch (_: IllegalStateException) { }
        assertEquals(11, ids.next)
    }
    @Test fun reconstructedCoordinatorRestoresConfigurationAndProtectsCandidate() = runBlocking {
        val op = ready()
        engine.advance(op.id, WidgetStage.Binding, WidgetStage.Configuring)
        val restored = WidgetTransactions(store, ids)
        restored.recover()
        assertEquals(setOf(1, op.candidate), ids.all)
        assertEquals(WidgetStage.Configuring, store.read().pending!!.stage)
        assertTrue(restored.commit(op.id, WidgetStage.Configuring))
    }
    @Test fun cleanupAfterCommitCanRetryWithoutRollingBackNewWidget() = runBlocking {
        val op = ready()
        ids.failDelete = true
        try { engine.commit(op.id, op.stage); fail("Expected host failure") } catch (_: IllegalStateException) { }
        assertEquals(op.candidate, store.state.panels[1]); assertTrue(1 in store.state.garbage)
        ids.failDelete = false
        WidgetTransactions(store, ids).recover()
        assertEquals(setOf(op.candidate), ids.all); assertTrue(store.state.garbage.isEmpty())
    }
    @Test fun staleOperationCannotCancelNewSetup() = runBlocking {
        val old = ready(); engine.cancel(old.id)
        val current = ready()
        assertFalse(engine.cancel(old.id)); assertFalse(engine.commit(old.id, old.stage))
        assertEquals(current.id, store.state.pending!!.id)
    }
    @Test fun providerFailurePreventsCommitAndSupportsExplicitCancellation() = runBlocking {
        val op = ready(); ids.providers[op.candidate] = "wrong/.Widget"
        try { engine.commit(op.id, op.stage); fail("Expected provider failure") } catch (_: IllegalStateException) { }
        assertEquals(1, store.state.panels[1]); assertTrue(ids.deleted.isEmpty())
        engine.cancel(op.id)
        assertEquals(setOf(1), ids.all)
    }
    @Test fun configurationFailureCancelsOnlyCandidate() = runBlocking {
        val op = ready(); engine.advance(op.id, WidgetStage.Binding, WidgetStage.Configuring)
        assertFalse(engine.commit(op.id, WidgetStage.Binding))
        engine.cancel(op.id)
        assertEquals(1, store.state.panels[1]); assertEquals(listOf(op.candidate), ids.deleted)
    }
    @Test fun cleanupNeverDeletesReferencedOrForeignIds() = runBlocking {
        store.state = store.state.copy(garbage = setOf(1, 999))
        engine.recover()
        assertTrue(ids.deleted.isEmpty()); assertEquals(setOf(1), ids.all)
    }
    @Test fun malformedStateBlocksDestructiveRecovery() = runBlocking {
        store.state = store.state.copy(malformed = true)
        try { engine.recover(); fail("Expected repair required") } catch (_: IllegalStateException) { }
        assertTrue(ids.deleted.isEmpty())
    }
    @Test fun operationCodecRejectsMalformedRecords() {
        val op = WidgetOperation("valid-id", 1, 12, "app/.Widget", WidgetStage.Configuring)
        assertEquals(op, WidgetOperation.decode(op.encode()))
        for (raw in listOf("broken", "1|x|0|2|app/.Widget|Binding", "1|x|1|-1|app/.Widget|Binding", "1|x|1|2|app/.Widget|Unknown")) {
            assertNull(WidgetOperation.decode(raw))
        }
    }
}
