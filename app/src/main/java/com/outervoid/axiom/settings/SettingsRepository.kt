package com.outervoid.axiom.settings

import android.content.Context
import android.content.pm.PackageManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.MutablePreferences
import com.outervoid.axiom.ui.iconshape.IconShape
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import com.outervoid.axiom.widgets.PreferencesWidgetStore
import com.outervoid.axiom.widgets.WidgetStore
import com.outervoid.axiom.widgets.WidgetSnapshot
import com.outervoid.axiom.widgets.PendingWidgetKey
import com.outervoid.axiom.widgets.WidgetGarbageKey
import com.outervoid.axiom.widgets.widgetSnapshot
import java.io.IOException

private val Context.axiomSettingsStore: DataStore<Preferences> by preferencesDataStore("axiom_settings")

class SettingsRepository(private val context: Context) {
    val widgetStore: WidgetStore = PreferencesWidgetStore(context.axiomSettingsStore)

    val widgetSnapshots: Flow<WidgetSnapshot> = context.axiomSettingsStore.data.map(::widgetSnapshot)

    val settings: Flow<AxiomSettings> = context.axiomSettingsStore.data
        .catch { error ->
            if (error is IOException) emit(androidx.datastore.preferences.core.emptyPreferences()) else throw error
        }
        .map(::decodeAxiomSettings)

    suspend fun updateFont(key: String) = update { it[SettingsKeys.SelectedFontKey] = key }

    suspend fun updateIconPack(packageName: String) =
        update { it[SettingsKeys.SelectedIconPackPackage] = packageName }

    suspend fun updateIconShape(shape: IconShape) = update { it[SettingsKeys.IconShape] = shape.name }
    suspend fun updateFavoritesCount(count: Int) = update { it[SettingsKeys.FavoritesCount] = count.coerceIn(5, 25) }
    suspend fun updateAccentColor(key: String) = update { it[SettingsKeys.AccentColorKey] = key }
    suspend fun updateFavoritesLocked(locked: Boolean) = update { it[SettingsKeys.FavoritesLocked] = locked }
    suspend fun updateHomePanelCount(count: Int) {
        val limit = count.coerceIn(1, 5)
        context.axiomSettingsStore.edit { prefs ->
            val state = widgetSnapshot(prefs)
            check(!state.malformed) { "Reset damaged widget records before changing panels." }
            val removed = state.panels.filterKeys { it >= limit }.values.toSet()
            val pending = state.pending?.takeIf { it.panel >= limit }
            prefs[SettingsKeys.HomePanelCount] = limit
            prefs[SettingsKeys.PanelWidgets] = state.panels.filterKeys { it < limit }.map { "${it.key}:${it.value}" }.toSet()
            if (pending != null) prefs.remove(PendingWidgetKey)
            prefs[WidgetGarbageKey] = (state.garbage + removed + listOfNotNull(pending?.candidate)).map(Int::toString).toSet()
        }
    }
    suspend fun updateHiddenApps(keys: Set<String>) = update { it[SettingsKeys.HiddenApps] = keys }
    suspend fun updateClockFormat(format: ClockFormat) = update { it[SettingsKeys.ClockFormat] = format.name }
    suspend fun updateLockScreenWallpaperEnabled(enabled: Boolean) =
        update { it[SettingsKeys.LockScreenWallpaperEnabled] = enabled }
    suspend fun updateLeftFolderName(name: String) = update { it[SettingsKeys.LeftFolderName] = name.ifBlank { "CUSTOM FOLDER" } }
    suspend fun updateRightFolderName(name: String) = update { it[SettingsKeys.RightFolderName] = name.ifBlank { "CUSTOM FOLDER" } }
    suspend fun updateLeftFolderApps(keys: Set<String>) = update { it[SettingsKeys.LeftFolderAppPackages] = keys }
    suspend fun updateRightFolderApps(keys: Set<String>) = update { it[SettingsKeys.RightFolderAppPackages] = keys }
    suspend fun updateFolderIconStyle(style: String) = update { it[SettingsKeys.FolderIconStyle] = style }
    suspend fun updateFavoriteAppKeys(keys: Set<String>) = update { it[SettingsKeys.FavoriteAppKeys] = keys }

    suspend fun updateShowAppLabels(enabled: Boolean) = update { it[SettingsKeys.ShowAppLabels] = enabled }

    suspend fun updateGridColumns(columns: Int) =
        update { it[SettingsKeys.GridColumns] = columns.coerceIn(3, 6) }

    suspend fun updateIconScale(scale: Float) =
        update { it[SettingsKeys.IconScale] = sanitizeIconScale(scale) }

    suspend fun updateShowSystemOnline(enabled: Boolean) =
        update { it[SettingsKeys.ShowSystemOnline] = enabled }

    suspend fun updateAmbientBoot(enabled: Boolean) = update { it[SettingsKeys.AmbientBootEnabled] = enabled }

    suspend fun updateAmberAccent(enabled: Boolean) = update { it[SettingsKeys.AmberAccentEnabled] = enabled }

    suspend fun updateCompactMode(enabled: Boolean) = update { it[SettingsKeys.CompactMode] = enabled }

    suspend fun updateHideStatusBar(enabled: Boolean) = update { it[SettingsKeys.HideStatusBar] = enabled }

    suspend fun updateHideNavigationBar(enabled: Boolean) =
        update { it[SettingsKeys.HideNavigationBar] = enabled }

    suspend fun updateSearchOnSwipeUp(enabled: Boolean) =
        update { it[SettingsKeys.SearchOnSwipeUp] = enabled }

    suspend fun updateSystemPanel(enabled: Boolean) = update { it[SettingsKeys.SystemPanelEnabled] = enabled }

    suspend fun updateTerminalPanel(enabled: Boolean) = update { it[SettingsKeys.TerminalPanelEnabled] = enabled }

    suspend fun updateHapticFeedback(enabled: Boolean) =
        update { it[SettingsKeys.HapticFeedbackEnabled] = enabled }

    suspend fun resetWidgets() {
        context.axiomSettingsStore.edit { prefs ->
            prefs.remove(SettingsKeys.PanelWidgets)
            prefs.remove(PendingWidgetKey)
            prefs.remove(WidgetGarbageKey)
        }
    }

    suspend fun reset() {
        context.axiomSettingsStore.edit { it.clear() }
    }

    fun isVoidSysInstalled(): Boolean = try {
        context.packageManager.getPackageInfo(IconPackOptions.VoidSysPackage, 0)
        true
    } catch (_: PackageManager.NameNotFoundException) {
        false
    }

    private suspend fun update(block: (MutablePreferencesEditor) -> Unit) {
        context.axiomSettingsStore.edit { preferences -> block(MutablePreferencesEditor(preferences)) }
    }
}

class MutablePreferencesEditor internal constructor(
    private val preferences: MutablePreferences
) {
    operator fun <T> set(key: Preferences.Key<T>, value: T) {
        preferences[key] = value
    }
}
