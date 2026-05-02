package com.outervoid.axiom.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.settings.AxiomSettings
import com.outervoid.axiom.settings.AccentColorOptions
import com.outervoid.axiom.settings.ClockFormat
import com.outervoid.axiom.settings.FontOptions
import com.outervoid.axiom.settings.IconPackOptions
import com.outervoid.axiom.settings.accentColor
import com.outervoid.axiom.ui.iconshape.IconShape
import com.outervoid.axiom.ui.components.SettingsDropdown
import com.outervoid.axiom.ui.components.SettingsRow
import com.outervoid.axiom.ui.components.SettingsSection
import com.outervoid.axiom.ui.components.SettingsToggle
import com.outervoid.axiom.ui.theme.Background
import com.outervoid.axiom.ui.theme.Danger
import com.outervoid.axiom.ui.theme.Surface
import com.outervoid.axiom.ui.theme.TextPrimary
import com.outervoid.axiom.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    settings: AxiomSettings,
    voidSysInstalled: Boolean,
    onFontSelected: (String) -> Unit,
    onIconPackSelected: (String) -> Unit,
    onIconShapeSelected: (IconShape) -> Unit,
    onAccentColorSelected: (String) -> Unit,
    onClockFormatSelected: (ClockFormat) -> Unit,
    onLockScreenWallpaper: () -> Unit,
    onFavoritesCountSelected: (Int) -> Unit,
    onFavoritesLockedChanged: (Boolean) -> Unit,
    onHomePanelCountSelected: (Int) -> Unit,
    onOpenHiddenApps: () -> Unit,
    onOpenLeftFolderApps: () -> Unit,
    onOpenRightFolderApps: () -> Unit,
    onFolderIconStyleSelected: (String) -> Unit,
    onChooseDefaultLauncher: () -> Unit,
    onOpenPhoneSettings: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenContact: () -> Unit,
    onOpenPermissions: () -> Unit,
    onShowAppLabelsChanged: (Boolean) -> Unit,
    onGridColumnsChanged: (Int) -> Unit,
    onIconScaleChanged: (Float) -> Unit,
    onShowSystemOnlineChanged: (Boolean) -> Unit,
    onAmbientBootChanged: (Boolean) -> Unit,
    onAmberAccentChanged: (Boolean) -> Unit,
    onCompactModeChanged: (Boolean) -> Unit,
    onHideStatusBarChanged: (Boolean) -> Unit,
    onHideNavigationBarChanged: (Boolean) -> Unit,
    onSearchOnSwipeUpChanged: (Boolean) -> Unit,
    onSystemPanelChanged: (Boolean) -> Unit,
    onHapticFeedbackChanged: (Boolean) -> Unit,
    onReloadApps: () -> Unit,
    onResetSettings: () -> Unit
) {
    var resetConfirmVisible by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current
    val accent = settings.accentColor()

    fun haptic() {
        if (settings.hapticFeedbackEnabled) haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    fun toggle(action: () -> Unit) {
        haptic()
        action()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 18.dp, vertical = 22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text("AXIOM SETTINGS", color = TextPrimary, style = MaterialTheme.typography.titleLarge)
            Text("SYSTEM CONFIGURATION", color = TextSecondary, style = MaterialTheme.typography.labelLarge)
            Spacer(
                Modifier
                    .padding(top = 14.dp, bottom = 8.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(accent)
            )

            SettingsSection("Appearance") {
                SettingsDropdown(
                    title = "Font",
                    subtitle = "Active: ${FontOptions.byKey(settings.selectedFontKey).displayName}",
                    selected = FontOptions.byKey(settings.selectedFontKey),
                    options = FontOptions.All,
                    optionLabel = { it.displayName },
                    onSelected = { haptic(); onFontSelected(it.key) }
                )
                SettingsDropdown(
                    title = "Icon Shape",
                    selected = settings.iconShape,
                    options = IconShape.entries,
                    optionLabel = { it.name.replace('_', ' ') },
                    onSelected = { haptic(); onIconShapeSelected(it) }
                )
                SettingsDropdown(
                    title = "Accent Color",
                    selected = AccentColorOptions.byKey(settings.accentColorKey),
                    options = AccentColorOptions.All,
                    optionLabel = { it.displayName },
                    onSelected = { haptic(); onAccentColorSelected(it.key) }
                )
                SettingsDropdown(
                    title = "Clock Format",
                    selected = settings.clockFormat,
                    options = ClockFormat.entries,
                    optionLabel = {
                        when (it) {
                            ClockFormat.SYSTEM -> "System"
                            ClockFormat.TWELVE_HOUR -> "12 hour"
                            ClockFormat.TWENTY_FOUR_HOUR -> "24 hour"
                        }
                    },
                    onSelected = { haptic(); onClockFormatSelected(it) }
                )
                SettingsRow("Lock Screen Wallpaper", value = "SET", onClick = { haptic(); onLockScreenWallpaper() })
                SettingsDropdown(
                    title = "Icon pack",
                    subtitle = "Native VOID.SYS icons are included with AXIOM.",
                    selected = IconPackOptions.byPackage(settings.selectedIconPackPackage),
                    options = IconPackOptions.All,
                    optionLabel = { it.displayName },
                    onSelected = { haptic(); onIconPackSelected(it.packageName) }
                )
                SettingsRow(
                    title = "Show app labels",
                    trailing = {
                        SettingsToggle(checked = settings.showAppLabels, accentColor = accent, onCheckedChange = {
                            toggle { onShowAppLabelsChanged(it) }
                        })
                    }
                )
                SettingsDropdown(
                    title = "Grid columns",
                    selected = settings.gridColumns,
                    options = listOf(3, 4, 5, 6),
                    optionLabel = { it.toString() },
                    onSelected = { haptic(); onGridColumnsChanged(it) }
                )
                SettingsDropdown(
                    title = "Icon scale",
                    selected = settings.iconScale,
                    options = listOf(0.55f, 0.64f, 0.72f, 0.82f, 0.95f),
                    optionLabel = { "${(it * 100).toInt()}%" },
                    onSelected = { haptic(); onIconScaleChanged(it) }
                )
                SettingsRow(
                    title = "Compact mode",
                    trailing = {
                        SettingsToggle(checked = settings.compactMode, accentColor = accent, onCheckedChange = {
                            toggle { onCompactModeChanged(it) }
                        })
                    }
                )
            }

            SettingsSection("Home Screen") {
                SettingsDropdown(
                    title = "Favorites Count",
                    selected = settings.favoritesCount,
                    options = listOf(5, 10, 15, 20, 25),
                    optionLabel = { it.toString() },
                    onSelected = { haptic(); onFavoritesCountSelected(it) }
                )
                SettingsRow("Lock Favorites", trailing = {
                    SettingsToggle(
                        checked = settings.favoritesLocked,
                        onCheckedChange = { toggle { onFavoritesLockedChanged(it) } },
                        accentColor = accent
                    )
                })
                SettingsDropdown(
                    title = "Home Panels",
                    selected = settings.homePanelCount,
                    options = listOf(1, 2, 3, 4, 5),
                    optionLabel = { count ->
                        if (count == 1) "1 = Favorites" else "$count = Favorites / " + List(count - 1) { "Widgets" }.joinToString(" / ")
                    },
                    onSelected = { haptic(); onHomePanelCountSelected(it) }
                )
            }

            SettingsSection("App Drawer") {
                SettingsRow("Hidden Apps", value = "OPEN", subtitle = "Hide from app drawer only.", onClick = { haptic(); onOpenHiddenApps() })
            }

            SettingsSection("Folders") {
                SettingsRow("Left Folder", value = "APPS", subtitle = settings.leftFolderName, onClick = { haptic(); onOpenLeftFolderApps() })
                SettingsRow("Right Folder", value = "APPS", subtitle = settings.rightFolderName, onClick = { haptic(); onOpenRightFolderApps() })
                SettingsDropdown(
                    title = "Folder Customization",
                    selected = settings.folderIconStyle,
                    options = listOf("FOLDER", "GRID", "RING", "DOTS"),
                    optionLabel = { it },
                    onSelected = { haptic(); onFolderIconStyleSelected(it) }
                )
            }

            SettingsSection("Interface") {
                SettingsRow("Ambient boot sequence", trailing = {
                    SettingsToggle(checked = settings.ambientBootEnabled, accentColor = accent, onCheckedChange = { toggle { onAmbientBootChanged(it) } })
                })
                SettingsRow("Show SYSTEM ONLINE indicator", trailing = {
                    SettingsToggle(checked = settings.showSystemOnline, accentColor = accent, onCheckedChange = { toggle { onShowSystemOnlineChanged(it) } })
                })
                SettingsRow("Amber accent enabled", trailing = {
                    SettingsToggle(checked = settings.amberAccentEnabled, accentColor = accent, onCheckedChange = { toggle { onAmberAccentChanged(it) } })
                })
                SettingsRow("Hide status bar", trailing = {
                    SettingsToggle(checked = settings.hideStatusBar, accentColor = accent, onCheckedChange = { toggle { onHideStatusBarChanged(it) } })
                })
                SettingsRow("Hide navigation bar", trailing = {
                    SettingsToggle(checked = settings.hideNavigationBar, accentColor = accent, onCheckedChange = { toggle { onHideNavigationBarChanged(it) } })
                })
            }

            SettingsSection("Controls") {
                SettingsRow("Swipe up opens App Drawer", trailing = {
                    SettingsToggle(checked = settings.searchOnSwipeUp, onCheckedChange = { toggle { onSearchOnSwipeUpChanged(it) } }, accentColor = accent)
                })
                SettingsRow("System panel enabled", subtitle = "Enable access to the AXIOM system control panel.", trailing = {
                    SettingsToggle(checked = settings.systemPanelEnabled, onCheckedChange = { toggle { onSystemPanelChanged(it) } }, accentColor = accent)
                })
                SettingsRow("Haptic feedback", trailing = {
                    SettingsToggle(checked = settings.hapticFeedbackEnabled, onCheckedChange = { toggle { onHapticFeedbackChanged(it) } }, accentColor = accent)
                })
            }

            SettingsSection("System") {
                SettingsRow("Choose Default Launcher", value = "OPEN", onClick = { haptic(); onChooseDefaultLauncher() })
                SettingsRow("Open Phone Settings", value = "OPEN", onClick = { haptic(); onOpenPhoneSettings() })
                SettingsRow("Reload apps", value = "RUN", onClick = { haptic(); onReloadApps() })
                SettingsRow("Reset settings", value = "RESET", onClick = { haptic(); resetConfirmVisible = true })
            }

            SettingsSection("Modules") {
                SettingsRow(
                    title = "AXIOM SHOOTER",
                    value = "Included",
                    subtitle = "Hidden arcade Easter egg"
                )
                SettingsRow(
                    title = "VOID.SYS",
                    value = "Native: Included",
                    subtitle = "Standalone: ${if (voidSysInstalled) "Installed" else "Optional"}"
                )
            }

            SettingsSection("Developer") {
                SettingsRow(
                    title = "Terminal Panel",
                    value = "PLANNED",
                    subtitle = "Planned for v1.1"
                )
                SettingsRow("Custom dock folders", value = "ACTIVE", subtitle = "Left and right dock folders use selected app sets.")
            }

            SettingsSection("Setup") {
                SettingsRow("Permissions & Setup", value = "OPEN", subtitle = "Default launcher, widget binding, wallpaper.", onClick = { haptic(); onOpenPermissions() })
            }

            SettingsSection("About") {
                SettingsRow("About AXIOM", value = "OPEN", onClick = { haptic(); onOpenAbout() })
                SettingsRow("Contact", value = "OPEN", onClick = { haptic(); onOpenContact() })
            }

            SettingsSection("VOID.SYS") {
                SettingsRow(
                    title = "VOID.SYS status",
                    value = "Native: Included"
                )
                SettingsRow("Standalone package", value = if (voidSysInstalled) "Installed" else "Optional")
                SettingsRow("Package", value = IconPackOptions.VoidSysPackage)
                SettingsRow("Icon system", subtitle = "Default AXIOM icon system.")
                SettingsRow("Standalone product", value = "$1.99")
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (resetConfirmVisible) {
        AlertDialog(
            onDismissRequest = { resetConfirmVisible = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        resetConfirmVisible = false
                        haptic()
                        onResetSettings()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Danger)
                ) {
                    Text("RESET", color = Danger, style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { resetConfirmVisible = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = TextSecondary)
                ) {
                    Text("CANCEL", color = TextSecondary, style = MaterialTheme.typography.labelLarge)
                }
            },
            title = { Text("RESET SETTINGS", color = TextPrimary, style = MaterialTheme.typography.titleLarge) },
            text = {
                Text(
                    "Restore AXIOM defaults: bundled font, VOID.SYS, 4-column grid, labels, and ambient boot.",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            containerColor = Surface
        )
    }
}
