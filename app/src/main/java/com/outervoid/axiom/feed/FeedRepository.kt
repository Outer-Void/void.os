package com.outervoid.axiom.feed

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

private val Context.axiomFeedStore: DataStore<Preferences> by preferencesDataStore("axiom_feed")

class FeedRepository(private val context: Context) {
    val state: Flow<FeedState> = context.axiomFeedStore.data
        .catch { error ->
            if (error is IOException) emit(androidx.datastore.preferences.core.emptyPreferences()) else throw error
        }
        .map { prefs ->
            FeedState(
                enabled = prefs[Keys.Enabled] ?: true,
                minimalMode = prefs[Keys.MinimalMode] ?: false,
                modules = parseModules(prefs[Keys.Modules]),
                priorities = parseList(prefs[Keys.Priorities], listOf("Stabilize AXIOM", "Test device flows")),
                tasks = parseList(prefs[Keys.Tasks], listOf("Review widgets", "Verify icon style")),
                nextAction = prefs[Keys.NextAction] ?: "Run device test pass",
                notes = parseList(prefs[Keys.Notes], listOf("AXIOM Feed is a user-built control layer.")),
                appShortcutKeys = parseList(prefs[Keys.AppShortcuts], emptyList()).toSet(),
                quickActions = parseList(prefs[Keys.QuickActions], listOf("Wifi", "Bluetooth", "PhoneSettings", "SystemPanelShortcut"))
            )
        }

    suspend fun setEnabled(enabled: Boolean) = update { it[Keys.Enabled] = enabled }

    suspend fun setMinimalMode(enabled: Boolean) {
        context.axiomFeedStore.edit { prefs ->
            prefs[Keys.MinimalMode] = enabled
            prefs[Keys.Modules] = encodeModules(if (enabled) FeedModuleRegistry.minimalModules() else FeedModuleRegistry.defaultModules())
        }
    }

    suspend fun resetLayout() = update { it[Keys.Modules] = encodeModules(FeedModuleRegistry.defaultModules()) }

    suspend fun updateModules(modules: List<FeedModuleConfig>) =
        update { it[Keys.Modules] = encodeModules(normalizePositions(modules)) }

    suspend fun addModule(type: FeedModuleType, current: List<FeedModuleConfig>) {
        if (current.any { it.type == type }) {
            updateModules(current.map { if (it.type == type) it.copy(enabled = true) else it })
            return
        }
        updateModules(current + FeedModuleConfig(id = "${type.name}-${System.currentTimeMillis()}", type = type, position = current.size))
    }

    suspend fun removeModule(id: String, current: List<FeedModuleConfig>) =
        updateModules(current.filterNot { it.id == id })

    suspend fun toggleModule(id: String, current: List<FeedModuleConfig>) =
        updateModules(current.map { if (it.id == id) it.copy(enabled = !it.enabled) else it })

    suspend fun toggleCollapsed(id: String, current: List<FeedModuleConfig>) =
        updateModules(current.map { if (it.id == id) it.copy(collapsed = !it.collapsed) else it })

    suspend fun moveModule(id: String, delta: Int, current: List<FeedModuleConfig>) {
        val sorted = normalizePositions(current).toMutableList()
        val from = sorted.indexOfFirst { it.id == id }
        if (from == -1) return
        val to = (from + delta).coerceIn(0, sorted.lastIndex)
        if (from == to) return
        val item = sorted.removeAt(from)
        sorted.add(to, item)
        updateModules(sorted)
    }

    suspend fun setPriorities(values: List<String>) = update { it[Keys.Priorities] = encodeList(values.take(3)) }
    suspend fun setTasks(values: List<String>) = update { it[Keys.Tasks] = encodeList(values.take(12)) }
    suspend fun setNextAction(value: String) = update { it[Keys.NextAction] = value }
    suspend fun setNotes(values: List<String>) = update { it[Keys.Notes] = encodeList(values.take(20)) }

    private suspend fun update(block: suspend (androidx.datastore.preferences.core.MutablePreferences) -> Unit) {
        context.axiomFeedStore.edit { block(it) }
    }

    private fun normalizePositions(modules: List<FeedModuleConfig>): List<FeedModuleConfig> =
        modules.sortedBy { it.position }.mapIndexed { index, module -> module.copy(position = index) }

    private fun parseModules(raw: String?): List<FeedModuleConfig> {
        if (raw.isNullOrBlank()) return FeedModuleRegistry.defaultModules()
        return runCatching {
            val array = JSONArray(raw)
            List(array.length()) { index ->
                val item = array.getJSONObject(index)
                FeedModuleConfig(
                    id = item.getString("id"),
                    type = FeedModuleType.valueOf(item.getString("type")),
                    enabled = item.optBoolean("enabled", true),
                    collapsed = item.optBoolean("collapsed", false),
                    position = item.optInt("position", index),
                    size = runCatching { FeedModuleSize.valueOf(item.optString("size", FeedModuleSize.MEDIUM.name)) }.getOrDefault(FeedModuleSize.MEDIUM),
                    titleOverride = item.optString("titleOverride").takeIf { it.isNotBlank() },
                    settings = emptyMap()
                )
            }
        }.getOrElse { FeedModuleRegistry.defaultModules() }
    }

    private fun encodeModules(modules: List<FeedModuleConfig>): String {
        val array = JSONArray()
        normalizePositions(modules).forEach { module ->
            array.put(
                JSONObject()
                    .put("id", module.id)
                    .put("type", module.type.name)
                    .put("enabled", module.enabled)
                    .put("collapsed", module.collapsed)
                    .put("position", module.position)
                    .put("size", module.size.name)
                    .put("titleOverride", module.titleOverride ?: "")
            )
        }
        return array.toString()
    }

    private fun parseList(raw: String?, fallback: List<String>): List<String> {
        if (raw.isNullOrBlank()) return fallback
        return runCatching {
            val array = JSONArray(raw)
            List(array.length()) { index -> array.getString(index) }.filter { it.isNotBlank() }
        }.getOrDefault(fallback)
    }

    private fun encodeList(values: List<String>): String {
        val array = JSONArray()
        values.filter { it.isNotBlank() }.forEach(array::put)
        return array.toString()
    }

    private object Keys {
        val Enabled = booleanPreferencesKey("enabled")
        val MinimalMode = booleanPreferencesKey("minimal_mode")
        val Modules = stringPreferencesKey("modules")
        val Priorities = stringPreferencesKey("priorities")
        val Tasks = stringPreferencesKey("tasks")
        val NextAction = stringPreferencesKey("next_action")
        val Notes = stringPreferencesKey("notes")
        val AppShortcuts = stringPreferencesKey("app_shortcuts")
        val QuickActions = stringPreferencesKey("quick_actions")
    }
}
