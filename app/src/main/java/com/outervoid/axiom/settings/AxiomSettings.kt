package com.outervoid.axiom.settings

data class AxiomSettings(
    val selectedFontKey: String = DEFAULT_FONT_KEY,
    val selectedIconPackPackage: String = DEFAULT_ICON_PACK_PACKAGE,
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
    val hapticFeedbackEnabled: Boolean = true
) {
    companion object {
        const val DEFAULT_FONT_KEY = "jetbrains_mono"
        const val DEFAULT_ICON_PACK_PACKAGE = "com.outervoid.voidsys"
        val Defaults = AxiomSettings()
    }
}
