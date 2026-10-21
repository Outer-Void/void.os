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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
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
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    var isDefault by remember { mutableStateOf(permManager.isDefaultLauncher()) }
    var hasNotifListener by remember { mutableStateOf(permManager.hasNotificationListenerAccess()) }

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isDefault = permManager.isDefaultLauncher()
                hasNotifListener = permManager.hasNotificationListenerAccess()
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

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
                value = if (isDefault) "ACTIVE" else "SET",
                subtitle = if (isDefault) "AXIOM is set as your default launcher."
                else "Tap to choose AXIOM as default launcher.",
                onClick = if (!isDefault) onChooseDefaultLauncher else null
            )
        }

        SettingsSection("Notifications") {
            SettingsRow(
                title = "Notification Listener",
                value = if (hasNotifListener) "ACTIVE" else "SETUP",
                subtitle = if (hasNotifListener) "AXIOM can read active media sessions for Home."
                else "Optional for Home media titles. Grants access to notifications; enable only if you want this feature.",
                onClick = if (hasNotifListener) null else ({ permManager.openNotificationListenerSettings() })
            )
        }

        SettingsSection("Feed data") {
            SettingsRow(
                title = "Calendar, weather, contacts and usage metrics",
                value = "PLANNED",
                subtitle = "These data integrations are not implemented. AXIOM does not request permissions for them."
            )
        }

        SettingsSection("Widgets") {
            SettingsRow(
                title = "Widget Binding",
                value = "AVAILABLE",
                subtitle = "Long-press any home panel (panel 2+), choose a widget, then approve the Android widget permission dialog if shown.",
                onClick = onOpenWidgetSettings
            )
        }

        SettingsSection("Wallpaper") {
            SettingsRow(
                title = "Lock Screen Wallpaper",
                value = "DEVICE DEPENDENT",
                subtitle = "Some OEMs restrict lock-screen wallpaper via app. AXIOM reports any restriction clearly and offers the system picker as fallback."
            )
        }

        SettingsSection("Phone & Messages") {
            SettingsRow(
                title = "Phone / Dialer",
                value = "SYSTEM",
                subtitle = "AXIOM opens your default dialer via ACTION_DIAL — no extra permission needed."
            )
            SettingsRow(
                title = "Messages",
                value = "SYSTEM",
                subtitle = "AXIOM opens your default SMS app via ACTION_MAIN + CATEGORY_APP_MESSAGING — no extra permission needed."
            )
        }

        SettingsSection("VOID.SYS") {
            SettingsRow(
                title = "Native Icons",
                value = "INCLUDED",
                subtitle = "Bundled with AXIOM — no separate install required."
            )
            SettingsRow(
                title = "Standalone Package",
                value = if (voidSysInstalled) "INSTALLED" else "OPTIONAL",
                subtitle = if (voidSysInstalled) "Installed for other launchers."
                else "Not required for AXIOM. Optional for Nova, Lawnchair, and others."
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}
