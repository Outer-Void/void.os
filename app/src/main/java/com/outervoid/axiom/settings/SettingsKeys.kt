package com.outervoid.axiom.settings

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object SettingsKeys {
    val SelectedFontKey = stringPreferencesKey("selected_font_key")
    val SelectedIconPackPackage = stringPreferencesKey("selected_icon_pack_package")
    val ShowAppLabels = booleanPreferencesKey("show_app_labels")
    val GridColumns = intPreferencesKey("grid_columns")
    val IconScale = floatPreferencesKey("icon_scale")
    val ShowSystemOnline = booleanPreferencesKey("show_system_online")
    val AmbientBootEnabled = booleanPreferencesKey("ambient_boot_enabled")
    val AmberAccentEnabled = booleanPreferencesKey("amber_accent_enabled")
    val CompactMode = booleanPreferencesKey("compact_mode")
    val HideStatusBar = booleanPreferencesKey("hide_status_bar")
    val HideNavigationBar = booleanPreferencesKey("hide_navigation_bar")
    val SearchOnSwipeUp = booleanPreferencesKey("search_on_swipe_up")
    val SystemPanelEnabled = booleanPreferencesKey("system_panel_enabled")
    val HapticFeedbackEnabled = booleanPreferencesKey("haptic_feedback_enabled")
}
