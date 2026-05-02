package com.outervoid.axiom.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.security.PermissionManager
import com.outervoid.axiom.settings.AxiomSettings
import com.outervoid.axiom.settings.accentColor
import com.outervoid.axiom.ui.components.SettingsRow
import com.outervoid.axiom.ui.components.SettingsSection
import com.outervoid.axiom.ui.theme.Background
import com.outervoid.axiom.ui.theme.TextSecondary

@Composable
fun PermissionSetupScreen(
    settings: AxiomSettings,
    voidSysInstalled: Boolean,
    onChooseDefaultLauncher: () -> Unit,
    onOpenWidgetSettings: () -> Unit
) {
    val context = LocalContext.current
    val accent = settings.accentColor()
    val permManager = remember(context) { PermissionManager(context) }
    val isDefault = permManager.isDefaultLauncher()
    val hasWidgetSetupPath = permManager.hasWidgetSetupPath()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 18.dp, vertical = 22.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("PERMISSIONS & SETUP", color = accent, style = MaterialTheme.typography.titleLarge)
        Text("AXIOM INITIAL CONFIGURATION", color = TextSecondary, style = MaterialTheme.typography.labelLarge)
        Spacer(
            Modifier
                .padding(top = 12.dp, bottom = 18.dp)
                .width(104.dp)
                .height(1.dp)
                .background(accent)
        )

        SettingsSection("Launcher") {
            SettingsRow(
                title = "Default Launcher",
                value = if (isDefault) "ACTIVE" else "NOT SET",
                subtitle = if (isDefault) "AXIOM is your default launcher."
                else "Tap to set AXIOM as default launcher.",
                onClick = if (!isDefault) onChooseDefaultLauncher else null
            )
        }

        SettingsSection("Widgets") {
            SettingsRow(
                title = "Widget Binding",
                value = if (hasWidgetSetupPath) "AVAILABLE" else "REQUIRED",
                subtitle = "Long-press a widget panel, choose a widget, then approve the Android widget permission dialog if shown.",
                onClick = onOpenWidgetSettings
            )
        }

        SettingsSection("Lock Screen Wallpaper") {
            SettingsRow(
                title = "Wallpaper",
                value = "DEVICE DEPENDENT",
                subtitle = "Some devices restrict lock-screen wallpaper via app. AXIOM reports any restriction clearly."
            )
        }

        SettingsSection("VOID.SYS") {
            SettingsRow(
                title = "Native Icons",
                value = "INCLUDED",
                subtitle = "Bundled with AXIOM. No separate install required."
            )
            SettingsRow(
                title = "Standalone Package",
                value = if (voidSysInstalled) "INSTALLED" else "OPTIONAL",
                subtitle = if (voidSysInstalled) "Installed for other launchers."
                else "Not required for AXIOM. Optional for Nova, Lawnchair, etc."
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}
