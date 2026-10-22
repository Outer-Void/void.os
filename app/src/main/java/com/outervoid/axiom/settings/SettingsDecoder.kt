package com.outervoid.axiom.settings

import androidx.datastore.preferences.core.Preferences
import com.outervoid.axiom.ui.iconshape.IconShape

internal fun decodeAxiomSettings(preferences: Preferences): AxiomSettings {
    return AxiomSettings(
        selectedFontKey = preferences.read(SettingsKeys.SelectedFontKey) ?: AxiomSettings.DEFAULT_FONT_KEY,
        selectedIconPackPackage = preferences.read(SettingsKeys.SelectedIconPackPackage)
            ?: AxiomSettings.DEFAULT_ICON_PACK_PACKAGE,
        iconShape = enumValue(preferences.read(SettingsKeys.IconShape), IconShape.ROUNDED_SQUARE),
        favoritesCount = (preferences.read(SettingsKeys.FavoritesCount) ?: 10).coerceIn(5, 25),
        accentColorKey = preferences.read(SettingsKeys.AccentColorKey) ?: AccentColorOptions.DefaultKey,
        favoritesLocked = preferences.read(SettingsKeys.FavoritesLocked) ?: false,
        homePanelCount = (preferences.read(SettingsKeys.HomePanelCount) ?: 1).coerceIn(1, 5),
        hiddenApps = preferences.read(SettingsKeys.HiddenApps) ?: emptySet(),
        clockFormat = enumValue(preferences.read(SettingsKeys.ClockFormat), ClockFormat.SYSTEM),
        lockScreenWallpaperEnabled = preferences.read(SettingsKeys.LockScreenWallpaperEnabled) ?: false,
        leftFolderName = preferences.read(SettingsKeys.LeftFolderName) ?: "CUSTOM FOLDER",
        rightFolderName = preferences.read(SettingsKeys.RightFolderName) ?: "CUSTOM FOLDER",
        leftFolderAppPackages = preferences.read(SettingsKeys.LeftFolderAppPackages) ?: emptySet(),
        rightFolderAppPackages = preferences.read(SettingsKeys.RightFolderAppPackages) ?: emptySet(),
        folderIconStyle = preferences.read(SettingsKeys.FolderIconStyle) ?: "FOLDER",
        favoriteAppKeys = preferences.read(SettingsKeys.FavoriteAppKeys) ?: emptySet(),
        showAppLabels = preferences.read(SettingsKeys.ShowAppLabels) ?: true,
        gridColumns = (preferences.read(SettingsKeys.GridColumns) ?: 4).coerceIn(3, 6),
        iconScale = sanitizeIconScale(preferences.read(SettingsKeys.IconScale)),
        showSystemOnline = preferences.read(SettingsKeys.ShowSystemOnline) ?: true,
        ambientBootEnabled = preferences.read(SettingsKeys.AmbientBootEnabled) ?: true,
        amberAccentEnabled = preferences.read(SettingsKeys.AmberAccentEnabled) ?: true,
        compactMode = preferences.read(SettingsKeys.CompactMode) ?: false,
        hideStatusBar = preferences.read(SettingsKeys.HideStatusBar) ?: false,
        hideNavigationBar = preferences.read(SettingsKeys.HideNavigationBar) ?: false,
        searchOnSwipeUp = preferences.read(SettingsKeys.SearchOnSwipeUp) ?: true,
        systemPanelEnabled = preferences.read(SettingsKeys.SystemPanelEnabled) ?: false,
        terminalPanelEnabled = preferences.read(SettingsKeys.TerminalPanelEnabled) ?: false,
        hapticFeedbackEnabled = preferences.read(SettingsKeys.HapticFeedbackEnabled) ?: true,
        panelWidgets = com.outervoid.axiom.widgets.widgetSnapshot(preferences).panels.map { "${it.key}:${it.value}" }.toSet()
    )
}

private inline fun <reified T> Preferences.read(key: Preferences.Key<T>): T? =
    try {
        asMap()[key] as? T
    } catch (_: ClassCastException) {
        null
    }

private inline fun <reified T : Enum<T>> enumValue(value: String?, fallback: T): T =
    value?.let { runCatching { enumValueOf<T>(it) }.getOrNull() } ?: fallback

internal fun sanitizeIconScale(value: Float?): Float =
    value?.takeIf { it.isFinite() }?.coerceIn(0.55f, 0.95f) ?: 0.72f
