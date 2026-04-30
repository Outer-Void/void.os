package com.outervoid.axiom.settings

import android.content.Context
import android.content.pm.PackageManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
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
                hapticFeedbackEnabled = preferences[SettingsKeys.HapticFeedbackEnabled] ?: true
            )
        }

    suspend fun updateFont(key: String) = update { it[SettingsKeys.SelectedFontKey] = key }

    suspend fun updateIconPack(packageName: String) =
        update { it[SettingsKeys.SelectedIconPackPackage] = packageName }

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

    suspend fun updateHapticFeedback(enabled: Boolean) =
        update { it[SettingsKeys.HapticFeedbackEnabled] = enabled }

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
    private val preferences: androidx.datastore.preferences.core.MutablePreferences
) {
    operator fun <T> set(key: Preferences.Key<T>, value: T) {
        preferences[key] = value
    }
}
