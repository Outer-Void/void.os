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
import com.outervoid.axiom.settings.FontOptions
import com.outervoid.axiom.settings.IconPackOptions
import com.outervoid.axiom.ui.components.CornerBrackets
import com.outervoid.axiom.ui.components.SettingsDropdown
import com.outervoid.axiom.ui.components.SettingsRow
import com.outervoid.axiom.ui.components.SettingsSection
import com.outervoid.axiom.ui.components.SettingsToggle
import com.outervoid.axiom.ui.theme.Amber
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
                    .background(Amber)
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
                    title = "Icon pack",
                    subtitle = "Prefer VOID.SYS when available; fallback uses system icons.",
                    selected = IconPackOptions.byPackage(settings.selectedIconPackPackage),
                    options = IconPackOptions.All,
                    optionLabel = { it.displayName },
                    onSelected = { haptic(); onIconPackSelected(it.packageName) }
                )
                SettingsRow(
                    title = "Show app labels",
                    trailing = {
                        SettingsToggle(checked = settings.showAppLabels, onCheckedChange = {
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
                        SettingsToggle(checked = settings.compactMode, onCheckedChange = {
                            toggle { onCompactModeChanged(it) }
                        })
                    }
                )
            }

            SettingsSection("Interface") {
                SettingsRow("Ambient boot sequence", trailing = {
                    SettingsToggle(checked = settings.ambientBootEnabled, onCheckedChange = { toggle { onAmbientBootChanged(it) } })
                })
                SettingsRow("Show SYSTEM ONLINE indicator", trailing = {
                    SettingsToggle(checked = settings.showSystemOnline, onCheckedChange = { toggle { onShowSystemOnlineChanged(it) } })
                })
                SettingsRow("Amber accent enabled", trailing = {
                    SettingsToggle(checked = settings.amberAccentEnabled, onCheckedChange = { toggle { onAmberAccentChanged(it) } })
                })
                SettingsRow("Hide status bar", trailing = {
                    SettingsToggle(checked = settings.hideStatusBar, onCheckedChange = { toggle { onHideStatusBarChanged(it) } })
                })
                SettingsRow("Hide navigation bar", trailing = {
                    SettingsToggle(checked = settings.hideNavigationBar, onCheckedChange = { toggle { onHideNavigationBarChanged(it) } })
                })
            }

            SettingsSection("Controls") {
                SettingsRow("Swipe up opens search", trailing = {
                    SettingsToggle(checked = settings.searchOnSwipeUp, onCheckedChange = { toggle { onSearchOnSwipeUpChanged(it) } })
                })
                SettingsRow("System panel enabled", subtitle = "Enable access to the AXIOM system control panel.", trailing = {
                    SettingsToggle(checked = settings.systemPanelEnabled, onCheckedChange = { toggle { onSystemPanelChanged(it) } })
                })
                SettingsRow("Haptic feedback", trailing = {
                    SettingsToggle(checked = settings.hapticFeedbackEnabled, onCheckedChange = { toggle { onHapticFeedbackChanged(it) } })
                })
            }

            SettingsSection("System") {
                SettingsRow("Reload apps", value = "RUN", onClick = { haptic(); onReloadApps() })
                SettingsRow("Reset settings", value = "RESET", onClick = { haptic(); resetConfirmVisible = true })
                SettingsRow("About AXIOM", subtitle = "A5 Settings Foundation. System-focused Android launcher by Outer-Void.")
            }

            SettingsSection("VOID.SYS") {
                SettingsRow(
                    title = "VOID.SYS status",
                    value = if (voidSysInstalled) "ACTIVE / INSTALLED" else "NOT INSTALLED"
                )
                SettingsRow("Package", value = IconPackOptions.VoidSysPackage)
                SettingsRow("Icon system", subtitle = "Intended icon system for AXIOM.")
                if (!voidSysInstalled) {
                    SettingsRow("Fallback", subtitle = "VOID.SYS not installed. Using system icons.")
                }
            }
            Spacer(Modifier.height(24.dp))
        }
        CornerBrackets()
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
                    "Restore AXIOM defaults: JetBrains Mono, VOID.SYS, 4-column grid, labels, and ambient boot.",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            containerColor = Surface
        )
    }
}
