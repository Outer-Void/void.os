package com.outervoid.axiom.feed

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.Composable
import com.outervoid.axiom.data.AppInfo
import com.outervoid.axiom.settings.AxiomSettings
import com.outervoid.axiom.system.SystemSettingsNavigator
import com.outervoid.axiom.ui.feed.modules.AppShortcutsModule
import com.outervoid.axiom.ui.feed.modules.CalendarModule
import com.outervoid.axiom.ui.feed.modules.ClockPanelModule
import com.outervoid.axiom.ui.feed.modules.FocusPanelModule
import com.outervoid.axiom.ui.feed.modules.MetricsModule
import com.outervoid.axiom.ui.feed.modules.MinimalModules
import com.outervoid.axiom.ui.feed.modules.NextActionModule
import com.outervoid.axiom.ui.feed.modules.NotesModule
import com.outervoid.axiom.ui.feed.modules.PlannedModule
import com.outervoid.axiom.ui.feed.modules.QuickActionsModule
import com.outervoid.axiom.ui.feed.modules.SessionTimerModule
import com.outervoid.axiom.ui.feed.modules.SetupRequiredModule
import com.outervoid.axiom.ui.feed.modules.SystemStatusModule
import com.outervoid.axiom.ui.feed.modules.TaskListModule
import com.outervoid.axiom.ui.feed.modules.WeatherModule

@Composable
fun FeedModuleRenderer(
    config: FeedModuleConfig,
    state: FeedState,
    settings: AxiomSettings,
    apps: List<AppInfo>,
    onLaunchApp: (AppInfo) -> Unit,
    onOpenSystemPanel: () -> Unit,
    onOpenAppDrawer: () -> Unit,
    onOpenTerminal: () -> Unit,
    onUpdatePriorities: (List<String>) -> Unit,
    onUpdateTasks: (List<String>) -> Unit,
    onUpdateNextAction: (String) -> Unit,
    onUpdateNotes: (List<String>) -> Unit,
    context: Context
) {
    when (config.type) {
        FeedModuleType.SystemStatus -> SystemStatusModule(config, settings)
        FeedModuleType.Weather -> PlannedModule(config)
        FeedModuleType.Calendar -> CalendarModule(config, context)
        FeedModuleType.ClockPanel -> ClockPanelModule(config)
        FeedModuleType.FocusPanel -> FocusPanelModule(config, state.priorities, onUpdatePriorities)
        FeedModuleType.TaskList -> TaskListModule(config, state.tasks, onUpdateTasks)
        FeedModuleType.SessionTimer -> SessionTimerModule(config)
        FeedModuleType.NextAction -> NextActionModule(config, state.nextAction, onUpdateNextAction)
        FeedModuleType.QuickActions,
        FeedModuleType.QuickToggles -> QuickActionsModule(config, state.quickActions, onOpenSystemPanel, onOpenAppDrawer) { action ->
            when (action) {
                "Wifi" -> SystemSettingsNavigator.openSpecificSettings(context, Settings.ACTION_WIFI_SETTINGS)
                "Bluetooth" -> SystemSettingsNavigator.openSpecificSettings(context, Settings.ACTION_BLUETOOTH_SETTINGS)
                "PhoneSettings" -> SystemSettingsNavigator.openPhoneSettings(context)
            }
        }
        FeedModuleType.AppShortcuts -> AppShortcutsModule(config, settings, apps, state.appShortcutKeys, onLaunchApp, onOpenAppDrawer)
        FeedModuleType.SystemPanelShortcut -> QuickActionsModule(config, listOf("SystemPanelShortcut"), onOpenSystemPanel, onOpenAppDrawer) {}
        FeedModuleType.TerminalPanel -> QuickActionsModule(config, listOf("TerminalShortcut"), onOpenSystemPanel, onOpenAppDrawer) { action ->
            if (action == "TerminalShortcut") onOpenTerminal()
        }
        FeedModuleType.TerminalShortcut -> QuickActionsModule(config, listOf("TerminalShortcut"), onOpenSystemPanel, onOpenAppDrawer) { action ->
            if (action == "TerminalShortcut") onOpenTerminal()
        }
        FeedModuleType.Notes,
        FeedModuleType.NotesFeed -> NotesModule(config, state.notes, onUpdateNotes)
        FeedModuleType.MinimalClock,
        FeedModuleType.SystemDotIndicator,
        FeedModuleType.FocusOnlyPanel,
        FeedModuleType.BlackScreenModule,
        FeedModuleType.ObserveMode -> MinimalModules(config, state)
        FeedModuleType.ScreenTimeToday,
        FeedModuleType.TopApps,
        FeedModuleType.UnlockCount,
        FeedModuleType.SessionDuration,
        FeedModuleType.NotificationLoad -> PlannedModule(config)
        else -> when (config.type.status) {
            FeedModuleStatus.SetupRequired -> PlannedModule(config)
            FeedModuleStatus.Planned -> PlannedModule(config)
            FeedModuleStatus.Functional -> PlannedModule(config)
        }
    }
}
