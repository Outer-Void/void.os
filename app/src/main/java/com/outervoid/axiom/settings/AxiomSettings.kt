package com.outervoid.axiom.settings

import com.outervoid.axiom.ui.iconshape.IconShape

data class AxiomSettings(
    val selectedFontKey: String = DEFAULT_FONT_KEY,
    val selectedIconPackPackage: String = DEFAULT_ICON_PACK_PACKAGE,
    val iconShape: IconShape = IconShape.ROUNDED_SQUARE,
    val favoritesCount: Int = 10,
    val accentColorKey: String = AccentColorOptions.DefaultKey,
    val favoritesLocked: Boolean = false,
    val homePanelCount: Int = 1,
    val hiddenApps: Set<String> = emptySet(),
    val clockFormat: ClockFormat = ClockFormat.SYSTEM,
    val lockScreenWallpaperEnabled: Boolean = false,
    val leftFolderName: String = "CUSTOM FOLDER",
    val rightFolderName: String = "CUSTOM FOLDER",
    val leftFolderAppPackages: Set<String> = emptySet(),
    val rightFolderAppPackages: Set<String> = emptySet(),
    val folderIconStyle: String = "FOLDER",
    val favoriteAppKeys: Set<String> = emptySet(),
    val showAppLabels: Boolean = true,
    val gridColumns: Int = 4,
    val iconScale: Float = 0.72f,
    val showSystemOnline: Boolean = true,
    val ambientBootEnabled: Boolean = true,
    val amberAccentEnabled: Boolean = true,
    val compactMode: Boolean = false,
    val hideStatusBar: Boolean = false,
    val hideNavigationBar: Boolean = false,
    val searchOnSwipeUp: Boolean = true,
    val systemPanelEnabled: Boolean = false,
    val terminalPanelEnabled: Boolean = false,
    val hapticFeedbackEnabled: Boolean = true,
    val panelWidgets: Set<String> = emptySet()
) {
    fun widgetForPanel(panelIndex: Int): Int? =
        panelWidgets.firstOrNull { it.startsWith("$panelIndex:") }
            ?.substringAfter(":")?.toIntOrNull()

    companion object {
        const val DEFAULT_FONT_KEY = "jetbrains_mono"
        const val DEFAULT_ICON_PACK_PACKAGE = "com.outervoid.voidsys"
        val Defaults = AxiomSettings()
    }
}
