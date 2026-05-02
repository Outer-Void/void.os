package com.outervoid.axiom.settings

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object SettingsKeys {
    val SelectedFontKey = stringPreferencesKey("selected_font_key")
    val SelectedIconPackPackage = stringPreferencesKey("selected_icon_pack_package")
    val IconShape = stringPreferencesKey("icon_shape")
    val FavoritesCount = intPreferencesKey("favorites_count")
    val AccentColorKey = stringPreferencesKey("accent_color_key")
    val FavoritesLocked = booleanPreferencesKey("favorites_locked")
    val HomePanelCount = intPreferencesKey("home_panel_count")
    val HiddenApps = stringSetPreferencesKey("hidden_apps")
    val ClockFormat = stringPreferencesKey("clock_format")
    val LockScreenWallpaperEnabled = booleanPreferencesKey("lock_screen_wallpaper_enabled")
    val LeftFolderName = stringPreferencesKey("left_folder_name")
    val RightFolderName = stringPreferencesKey("right_folder_name")
    val LeftFolderAppPackages = stringSetPreferencesKey("left_folder_app_packages")
    val RightFolderAppPackages = stringSetPreferencesKey("right_folder_app_packages")
    val FolderIconStyle = stringPreferencesKey("folder_icon_style")
    val FavoriteAppKeys = stringSetPreferencesKey("favorite_app_keys")
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
    val TerminalPanelEnabled = booleanPreferencesKey("terminal_panel_enabled")
    val HapticFeedbackEnabled = booleanPreferencesKey("haptic_feedback_enabled")
}
