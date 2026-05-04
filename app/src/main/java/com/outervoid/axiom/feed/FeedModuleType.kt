package com.outervoid.axiom.feed

enum class FeedModuleCategory(val label: String) {
    CoreSystem("Core System"),
    FocusControl("Focus / Control"),
    QuickAction("Quick Actions"),
    BehaviorMetrics("Behavior / Metrics"),
    Content("Content"),
    PersonalLife("Personal / Life"),
    VoidMinimal("VOID / Minimal"),
    Developer("Developer")
}

enum class FeedModuleStatus {
    Functional,
    SetupRequired,
    Planned
}

enum class FeedModuleSize {
    SMALL,
    MEDIUM,
    LARGE
}

enum class FeedModuleType(
    val label: String,
    val category: FeedModuleCategory,
    val status: FeedModuleStatus
) {
    SystemStatus("System Status", FeedModuleCategory.CoreSystem, FeedModuleStatus.Functional),
    Weather("Weather", FeedModuleCategory.CoreSystem, FeedModuleStatus.SetupRequired),
    Calendar("Calendar", FeedModuleCategory.CoreSystem, FeedModuleStatus.SetupRequired),
    ClockPanel("Clock Panel", FeedModuleCategory.CoreSystem, FeedModuleStatus.Functional),
    NotificationsSummary("Notifications Summary", FeedModuleCategory.CoreSystem, FeedModuleStatus.SetupRequired),
    ConnectivityPanel("Connectivity Panel", FeedModuleCategory.CoreSystem, FeedModuleStatus.SetupRequired),
    DeviceHealth("Device Health", FeedModuleCategory.CoreSystem, FeedModuleStatus.SetupRequired),

    FocusPanel("Focus Panel", FeedModuleCategory.FocusControl, FeedModuleStatus.Functional),
    TaskList("Task List", FeedModuleCategory.FocusControl, FeedModuleStatus.Functional),
    SessionTimer("Session Timer", FeedModuleCategory.FocusControl, FeedModuleStatus.Functional),
    NextAction("Next Action", FeedModuleCategory.FocusControl, FeedModuleStatus.Functional),
    RoutineBlock("Routine Block", FeedModuleCategory.FocusControl, FeedModuleStatus.Planned),
    GoalTracker("Goal Tracker", FeedModuleCategory.FocusControl, FeedModuleStatus.Planned),
    StateIndicator("State Indicator", FeedModuleCategory.FocusControl, FeedModuleStatus.Planned),

    QuickToggles("Quick Toggles", FeedModuleCategory.QuickAction, FeedModuleStatus.Functional),
    QuickActions("Quick Actions", FeedModuleCategory.QuickAction, FeedModuleStatus.Functional),
    AppShortcuts("App Shortcuts", FeedModuleCategory.QuickAction, FeedModuleStatus.Functional),
    ContactShortcuts("Contact Shortcuts", FeedModuleCategory.QuickAction, FeedModuleStatus.SetupRequired),
    MediaControls("Media Controls", FeedModuleCategory.QuickAction, FeedModuleStatus.SetupRequired),
    SystemPanelShortcut("System Panel Shortcut", FeedModuleCategory.QuickAction, FeedModuleStatus.Functional),
    TerminalShortcut("Terminal Shortcut", FeedModuleCategory.QuickAction, FeedModuleStatus.Functional),

    ScreenTimeToday("Screen Time Today", FeedModuleCategory.BehaviorMetrics, FeedModuleStatus.SetupRequired),
    TopApps("Top Apps", FeedModuleCategory.BehaviorMetrics, FeedModuleStatus.SetupRequired),
    UnlockCount("Unlock Count", FeedModuleCategory.BehaviorMetrics, FeedModuleStatus.SetupRequired),
    SessionDuration("Session Duration", FeedModuleCategory.BehaviorMetrics, FeedModuleStatus.SetupRequired),
    DriftIndicator("Drift Indicator", FeedModuleCategory.BehaviorMetrics, FeedModuleStatus.Planned),
    FocusConsistency("Focus Consistency", FeedModuleCategory.BehaviorMetrics, FeedModuleStatus.Planned),
    NotificationLoad("Notification Load", FeedModuleCategory.BehaviorMetrics, FeedModuleStatus.SetupRequired),

    RssFeed("RSS Feed", FeedModuleCategory.Content, FeedModuleStatus.SetupRequired),
    SavedArticles("Saved Articles", FeedModuleCategory.Content, FeedModuleStatus.Planned),
    BookmarksPanel("Bookmarks Panel", FeedModuleCategory.Content, FeedModuleStatus.Planned),
    NotesFeed("Notes Feed", FeedModuleCategory.Content, FeedModuleStatus.Functional),
    DevBoardFeed("DevBoard Feed", FeedModuleCategory.Content, FeedModuleStatus.Planned),
    GitHubActivity("GitHub Activity", FeedModuleCategory.Content, FeedModuleStatus.SetupRequired),
    ManualNewsSources("Manual News Sources", FeedModuleCategory.Content, FeedModuleStatus.SetupRequired),

    Reminders("Reminders", FeedModuleCategory.PersonalLife, FeedModuleStatus.SetupRequired),
    Notes("Notes", FeedModuleCategory.PersonalLife, FeedModuleStatus.Functional),
    HabitTracker("Habit Tracker", FeedModuleCategory.PersonalLife, FeedModuleStatus.Planned),
    FinanceSnapshot("Finance Snapshot", FeedModuleCategory.PersonalLife, FeedModuleStatus.Planned),
    LocationTravel("Location / Travel", FeedModuleCategory.PersonalLife, FeedModuleStatus.SetupRequired),
    HealthSnapshot("Health Snapshot", FeedModuleCategory.PersonalLife, FeedModuleStatus.SetupRequired),

    MinimalClock("Minimal Clock", FeedModuleCategory.VoidMinimal, FeedModuleStatus.Functional),
    SystemDotIndicator("System Dot Indicator", FeedModuleCategory.VoidMinimal, FeedModuleStatus.Functional),
    FocusOnlyPanel("Focus Only Panel", FeedModuleCategory.VoidMinimal, FeedModuleStatus.Functional),
    BlackScreenModule("Black Screen Module", FeedModuleCategory.VoidMinimal, FeedModuleStatus.Functional),
    ObserveMode("Observe Mode", FeedModuleCategory.VoidMinimal, FeedModuleStatus.Functional),

    TerminalPanel("Terminal Panel", FeedModuleCategory.Developer, FeedModuleStatus.Functional),
    LogViewer("Log Viewer", FeedModuleCategory.Developer, FeedModuleStatus.Planned),
    ModuleDebugInfo("Module Debug Info", FeedModuleCategory.Developer, FeedModuleStatus.Planned),
    SystemInfoDump("System Info Dump", FeedModuleCategory.Developer, FeedModuleStatus.Planned),
    PerformanceMonitor("Performance Monitor", FeedModuleCategory.Developer, FeedModuleStatus.Planned),
    FeatureFlagsPanel("Feature Flags Panel", FeedModuleCategory.Developer, FeedModuleStatus.Planned)
}
