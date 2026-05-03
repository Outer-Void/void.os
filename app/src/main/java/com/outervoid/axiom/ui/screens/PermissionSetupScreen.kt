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
    onOpenWidgetSettings: () -> Unit,
    onRequestCalendar: () -> Unit = {},
    onRequestContacts: () -> Unit = {},
    onRequestLocation: () -> Unit = {},
    onRequestNotifications: () -> Unit = {}
) {
    val context = LocalContext.current
    val accent = settings.accentColor()
    val permManager = remember(context) { PermissionManager(context) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    var isDefault by remember { mutableStateOf(permManager.isDefaultLauncher()) }
    var hasNotifListener by remember { mutableStateOf(permManager.hasNotificationListenerAccess()) }
    var hasCalendar by remember { mutableStateOf(permManager.hasCalendarPermission()) }
    var hasContacts by remember { mutableStateOf(permManager.hasContactsPermission()) }
    var hasLocation by remember { mutableStateOf(permManager.hasLocationPermission()) }
    var hasUsageStats by remember { mutableStateOf(permManager.hasUsageStatsAccess()) }
    var hasNotifs by remember { mutableStateOf(permManager.hasNotificationsPermission()) }

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isDefault = permManager.isDefaultLauncher()
                hasNotifListener = permManager.hasNotificationListenerAccess()
                hasCalendar = permManager.hasCalendarPermission()
                hasContacts = permManager.hasContactsPermission()
                hasLocation = permManager.hasLocationPermission()
                hasUsageStats = permManager.hasUsageStatsAccess()
                hasNotifs = permManager.hasNotificationsPermission()
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
                title = "Post Notifications",
                value = if (hasNotifs) "GRANTED" else "REQUEST",
                subtitle = if (hasNotifs) "AXIOM can post system notifications."
                else "Required for notification alerts from AXIOM.",
                onClick = if (!hasNotifs) onRequestNotifications else null
            )
            SettingsRow(
                title = "Notification Listener",
                value = if (hasNotifListener) "ACTIVE" else "SETUP",
                subtitle = if (hasNotifListener) "AXIOM can read notification summaries for the Feed."
                else "Required for Notifications Summary feed module. Opens Android Notification Access settings.",
                onClick = if (!hasNotifListener) {{ permManager.openNotificationListenerSettings() }} else null
            )
        }

        SettingsSection("Calendar") {
            SettingsRow(
                title = "Calendar Access",
                value = if (hasCalendar) "GRANTED" else "REQUEST",
                subtitle = if (hasCalendar) "AXIOM Feed can show today's calendar events."
                else "Required for Calendar feed module.",
                onClick = if (!hasCalendar) onRequestCalendar else null
            )
        }

        SettingsSection("Contacts") {
            SettingsRow(
                title = "Contacts Access",
                value = if (hasContacts) "GRANTED" else "REQUEST",
                subtitle = if (hasContacts) "AXIOM Feed can show contact shortcuts."
                else "Required for Contact Shortcuts feed module.",
                onClick = if (!hasContacts) onRequestContacts else null
            )
        }

        SettingsSection("Location") {
            SettingsRow(
                title = "Location Access",
                value = if (hasLocation) "GRANTED" else "REQUEST",
                subtitle = if (hasLocation) "AXIOM Feed weather uses device location."
                else "Optional — required for automatic weather location. You can also enter location manually.",
                onClick = if (!hasLocation) onRequestLocation else null
            )
        }

        SettingsSection("Usage Stats") {
            SettingsRow(
                title = "Usage Access",
                value = if (hasUsageStats) "ACTIVE" else "SETUP",
                subtitle = if (hasUsageStats) "Screen time, top apps, and metrics modules are active."
                else "Required for Screen Time, Top Apps, Unlock Count, and metrics modules. Opens Usage Access settings.",
                onClick = if (!hasUsageStats) {{ permManager.openUsageAccessSettings() }} else null
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
