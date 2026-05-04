# AXIOM v1.0 Functional Audit

Generated: 2026-05-04

---

## Placeholders Identified and Resolution

| Location | Issue | Resolution |
|---|---|---|
| SettingsScreen.kt | Terminal Panel row shows "PLANNED / Planned for v1.1" | Activated — navigates to TerminalPanelScreen |
| FeedModuleType.kt | TerminalShortcut status = Planned | Changed to Functional |
| FeedModuleType.kt | LogViewer, ModuleDebugInfo, SystemInfoDump, PerformanceMonitor, FeatureFlagsPanel status = Planned | Moved to Future Modules (v2.0) section in library — hidden from active add list |
| FeedModuleType.kt | RoutineBlock, GoalTracker, StateIndicator, DriftIndicator, FocusConsistency, SavedArticles, BookmarksPanel, DevBoardFeed, HabitTracker, FinanceSnapshot status = Planned | Moved to Future Modules (v2.0) section in library |
| FeedModuleLibraryScreen.kt | All modules shown in same list regardless of status | Now shows Active and SetupRequired modules in top section, Planned modules in "Future Modules" section |
| MainActivity.kt favoriteApps | When favoriteAppKeys is empty, auto-fills from allApps.take(N) | Fixed — returns emptyList() when no favorites selected, so empty slots show |
| StartScreen.kt (bundled) | Old simple START/HIGH SCORE/EXIT menu | Rebuilt with CAMPAIGN/SURVIVAL/TRAINING/SETTINGS/EXIT TO AXIOM |
| SettingsScreen.kt | Wallpaper UI only exposed lock-screen set | Added explicit Lock, Home, and Home + Lock wallpaper actions |
| AxiomWallpaperManager.kt | Single-target wallpaper setter only | Added home-screen and combined home/lock direct setters using packaged wallpaper art |
| FeedModuleType.kt | Terminal Panel feed module was still marked Planned | Changed to Functional and routes to TerminalPanelScreen |

---

## Setup-Required Features (Fully Functional After User Grants)

| Feature | Permission Required | Status |
|---|---|---|
| Post notifications | POST_NOTIFICATIONS (Android 13+) | Runtime request implemented |
| Calendar module | READ_CALENDAR | Runtime request implemented |
| Contacts shortcuts | READ_CONTACTS | Runtime request implemented |
| Location / weather | ACCESS_FINE_LOCATION + ACCESS_COARSE_LOCATION | Runtime request implemented |
| Usage stats metrics | PACKAGE_USAGE_STATS (special) | Settings intent flow implemented |
| Notification listener | BIND_NOTIFICATION_LISTENER_SERVICE (special) | Settings intent flow implemented |
| Flashlight | CAMERA | Handled in SystemActionController |
| Widget hosting | AppWidgetHost bind | Runtime request flow via Activity |
| Wallpaper | SET_WALLPAPER | Lock, Home, and Home + Lock direct set handled; system picker fallback handled |

---

## Permission-Backed Features — Status

| Feature | Status |
|---|---|
| Default Launcher | Check + route to settings implemented |
| Notification Listener | Check + route to settings implemented |
| Calendar | Check + runtime request implemented |
| Contacts | Check + runtime request implemented |
| Location | Check + runtime request implemented |
| Usage Access | Check + route to settings implemented |
| Widget Binding | Flow implemented via bindWidgetLauncher |
| Wallpaper | SET_WALLPAPER install permission plus OEM-restricted fallback to Android wallpaper setter |

---

## Features Moved to v2.0 (Hidden from Active UI)

The following are NOT shown in the active module library add list. They appear in a separate "Future Modules (v2.0)" section in FeedModuleLibraryScreen for transparency. No fake functionality is rendered.

- RoutineBlock
- GoalTracker
- StateIndicator
- DriftIndicator
- FocusConsistency
- SavedArticles
- BookmarksPanel
- DevBoardFeed
- HabitTracker
- FinanceSnapshot
- LogViewer
- ModuleDebugInfo
- SystemInfoDump
- PerformanceMonitor
- FeatureFlagsPanel

---

## Terminal Panel — v1.0 Status

- Enabled via Settings → Developer → Terminal Panel toggle
- Opened from AXIOM Feed Terminal Shortcut or Terminal Panel module
- Shell session via ProcessBuilder /system/bin/sh
- Output displayed in scrollable list
- Command input at bottom
- Clear session button
- Termux bridge offered if Termux detected (com.termux)
- Graceful message if shell session cannot start

---

## Axiom Shooter — v1.0 Features

- CAMPAIGN: 3 missions, progress persisted
- SURVIVAL: Endless mode
- TRAINING: No enemies, practice mode
- SETTINGS: Audio toggle, sensitivity, HUD
- SHIP: AXIOM-01 stats display
- EXIT TO AXIOM: Correct return

---

## Security

- Proprietary license, entitlement scaffold maintained
- Signature gate maintained
- Debug bypass only in debug builds
- Release fail-closed documented in V1_RELEASE_CHECKLIST.md
