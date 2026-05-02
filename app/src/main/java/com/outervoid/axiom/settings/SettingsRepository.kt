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
import java.io.IOException

private val Context.axiomSettingsStore: DataStore<Preferences> by preferencesDataStore("axiom_settings")

class SettingsRepository(private val context: Context) {
    val settings: Flow<AxiomSettings> = context.axiomSettingsStore.data
        .catch { error ->
            if (error is IOException) emit(androidx.datastore.preferences.core.emptyPreferences()) else throw error
        }
        .map { preferences ->
            AxiomSettings(
                selectedFontKey = preferences[SettingsKeys.SelectedFontKey] ?: AxiomSettings.DEFAULT_FONT_KEY,
                selectedIconPackPackage = preferences[SettingsKeys.SelectedIconPackPackage]
                    ?: AxiomSettings.DEFAULT_ICON_PACK_PACKAGE,
                iconShape = enumValue(preferences[SettingsKeys.IconShape], IconShape.ROUNDED_SQUARE),
                favoritesCount = (preferences[SettingsKeys.FavoritesCount] ?: 10).coerceIn(5, 25),
                accentColorKey = preferences[SettingsKeys.AccentColorKey] ?: AccentColorOptions.DefaultKey,
                favoritesLocked = preferences[SettingsKeys.FavoritesLocked] ?: false,
                homePanelCount = (preferences[SettingsKeys.HomePanelCount] ?: 1).coerceIn(1, 5),
                hiddenApps = preferences[SettingsKeys.HiddenApps] ?: emptySet(),
                clockFormat = enumValue(preferences[SettingsKeys.ClockFormat], ClockFormat.SYSTEM),
                lockScreenWallpaperEnabled = preferences[SettingsKeys.LockScreenWallpaperEnabled] ?: false,
                leftFolderName = preferences[SettingsKeys.LeftFolderName] ?: "CUSTOM FOLDER",
                rightFolderName = preferences[SettingsKeys.RightFolderName] ?: "CUSTOM FOLDER",
                leftFolderAppPackages = preferences[SettingsKeys.LeftFolderAppPackages] ?: emptySet(),
                rightFolderAppPackages = preferences[SettingsKeys.RightFolderAppPackages] ?: emptySet(),
                folderIconStyle = preferences[SettingsKeys.FolderIconStyle] ?: "FOLDER",
                favoriteAppKeys = preferences[SettingsKeys.FavoriteAppKeys] ?: emptySet(),
                showAppLabels = preferences[SettingsKeys.ShowAppLabels] ?: true,
                gridColumns = (preferences[SettingsKeys.GridColumns] ?: 4).coerceIn(3, 6),
                iconScale = (preferences[SettingsKeys.IconScale] ?: 0.72f).coerceIn(0.55f, 0.95f),
                showSystemOnline = preferences[SettingsKeys.ShowSystemOnline] ?: true,
                ambientBootEnabled = preferences[SettingsKeys.AmbientBootEnabled] ?: true,
                amberAccentEnabled = preferences[SettingsKeys.AmberAccentEnabled] ?: true,
                compactMode = preferences[SettingsKeys.CompactMode] ?: false,
                hideStatusBar = preferences[SettingsKeys.HideStatusBar] ?: false,
                hideNavigationBar = preferences[SettingsKeys.HideNavigationBar] ?: false,
                searchOnSwipeUp = preferences[SettingsKeys.SearchOnSwipeUp] ?: true,
                systemPanelEnabled = preferences[SettingsKeys.SystemPanelEnabled] ?: false,
                terminalPanelEnabled = preferences[SettingsKeys.TerminalPanelEnabled] ?: false,
                hapticFeedbackEnabled = preferences[SettingsKeys.HapticFeedbackEnabled] ?: true,
                panelWidgets = preferences[SettingsKeys.PanelWidgets] ?: emptySet()
            )
        }

    suspend fun updateFont(key: String) = update { it[SettingsKeys.SelectedFontKey] = key }

    suspend fun updateIconPack(packageName: String) =
        update { it[SettingsKeys.SelectedIconPackPackage] = packageName }

    suspend fun updateIconShape(shape: IconShape) = update { it[SettingsKeys.IconShape] = shape.name }
    suspend fun updateFavoritesCount(count: Int) = update { it[SettingsKeys.FavoritesCount] = count.coerceIn(5, 25) }
    suspend fun updateAccentColor(key: String) = update { it[SettingsKeys.AccentColorKey] = key }
    suspend fun updateFavoritesLocked(locked: Boolean) = update { it[SettingsKeys.FavoritesLocked] = locked }
    suspend fun updateHomePanelCount(count: Int) = update { it[SettingsKeys.HomePanelCount] = count.coerceIn(1, 5) }
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
        update { it[SettingsKeys.IconScale] = scale.coerceIn(0.55f, 0.95f) }

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

    suspend fun updatePanelWidget(panelIndex: Int, widgetId: Int?) {
        context.axiomSettingsStore.edit { prefs ->
            val current = (prefs[SettingsKeys.PanelWidgets] ?: emptySet()).toMutableSet()
            current.removeAll { it.startsWith("$panelIndex:") }
            if (widgetId != null) current.add("$panelIndex:$widgetId")
            prefs[SettingsKeys.PanelWidgets] = current
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

    private inline fun <reified T : Enum<T>> enumValue(value: String?, fallback: T): T =
        value?.let { runCatching { enumValueOf<T>(it) }.getOrNull() } ?: fallback
}

class MutablePreferencesEditor internal constructor(
    private val preferences: MutablePreferences
) {
    operator fun <T> set(key: Preferences.Key<T>, value: T) {
        preferences[key] = value
    }
}
