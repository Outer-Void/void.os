package com.outervoid.axiom.widgets

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.outervoid.axiom.settings.SettingsKeys
import kotlinx.coroutines.flow.first

internal val PendingWidgetKey = stringPreferencesKey("widget_operation_v1")
internal val WidgetGarbageKey = stringSetPreferencesKey("widget_cleanup_v1")

internal fun widgetSnapshot(prefs: Preferences): WidgetSnapshot {
    val raw = prefs.asMap()[SettingsKeys.PanelWidgets]
    val records = (raw as? Set<*>)?.filterIsInstance<String>() ?: emptyList()
    val panels = linkedMapOf<Int, Int>()
    var malformed = raw != null && (raw !is Set<*> || records.size != raw.size)
    for (record in records.sorted()) {
        val parts = record.split(':')
        val panel = parts.getOrNull(0)?.toIntOrNull()
        val id = parts.getOrNull(1)?.toIntOrNull()
        if (parts.size != 2 || panel !in 1..4 || id == null || id <= 0 || panel in panels || id in panels.values) {
            malformed = true
        } else panels[panel!!] = id
    }
    val pendingRaw = prefs.asMap()[PendingWidgetKey]
    val pending = WidgetOperation.decode(pendingRaw as? String)
    if (pendingRaw != null && pending == null) malformed = true
    if (pending != null && pending.candidate in panels.values) malformed = true
    val garbageRaw = prefs.asMap()[WidgetGarbageKey]
    val garbage = (garbageRaw as? Set<*>)?.mapNotNull { (it as? String)?.toIntOrNull()?.takeIf { id -> id > 0 } }?.toSet() ?: emptySet()
    if (garbageRaw != null && (garbageRaw !is Set<*> || garbage.size != garbageRaw.size)) malformed = true
    return WidgetSnapshot(panels, pending, garbage, malformed)
}

internal class PreferencesWidgetStore(private val dataStore: DataStore<Preferences>) : WidgetStore {
    override suspend fun read(): WidgetSnapshot = widgetSnapshot(dataStore.data.first())

    override suspend fun update(transform: (WidgetSnapshot) -> WidgetSnapshot): WidgetSnapshot {
        val committed = dataStore.edit { prefs ->
            val current = widgetSnapshot(prefs)
            check(!current.malformed) { "Widget records are damaged. Reset widgets in the widget picker to recover safely." }
            val next = transform(current)
            prefs[SettingsKeys.PanelWidgets] = next.panels.map { "${it.key}:${it.value}" }.toSet()
            if (next.pending == null) prefs.remove(PendingWidgetKey) else prefs[PendingWidgetKey] = next.pending.encode()
            prefs[WidgetGarbageKey] = next.garbage.map(Int::toString).toSet()
        }
        return widgetSnapshot(committed)
    }
}
