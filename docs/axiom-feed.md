# AXIOM Feed

AXIOM Feed is a user-designed modular right-swipe control panel. It is not Google Discover, a news feed, an ad surface, a recommendations panel, or infinite scroll.

## Navigation

- Swipe right from the Home favorites panel to open AXIOM Feed when enabled.
- Swipe left from AXIOM Feed to return Home.
- Back from AXIOM Feed returns Home.

## Default Modules

Default layout:

- System Status
- Weather
- Calendar
- Focus Panel
- Quick Actions

Weather and Calendar start in setup-required states because AXIOM does not bundle weather API keys or read calendar data without explicit Android access.

## Module Configuration

Settings > AXIOM FEED > Configure Feed opens the active layout editor.

Supported controls:

- Enable / disable
- Reorder with Up / Down buttons
- Collapse / expand
- Remove
- Re-add from Module Library
- Reset layout
- Minimal Feed Mode

Minimal Feed Mode keeps only Minimal Clock, System Dot Indicator, and Focus Only Panel.

## Functional v1 Modules

Implemented with real local state or device state:

- System Status
- Clock Panel
- Focus Panel
- Task List
- Session Timer
- Next Action
- Quick Actions
- App Shortcuts
- Notes
- Minimal Clock
- System Dot Indicator
- Focus Only Panel
- Black Screen Module
- Observe Mode
- System Panel Shortcut

The following local data is persisted with DataStore:

- Focus priorities
- Task list text/check state
- Next action
- Notes
- Feed layout/configuration

## Setup Required Modules

Modules that require Android permissions or user configuration are visibly marked setup-required and do not fake data:

- Weather
- Calendar
- Notifications Summary
- Connectivity Panel
- Device Health
- Screen Time Today
- Top Apps
- Unlock Count
- Session Duration
- Notification Load
- Contact Shortcuts
- Media Controls
- RSS Feed
- GitHub Activity
- Manual News Sources
- Reminders
- Location / Travel
- Health Snapshot

Usage metrics require Android Usage Access. Notification modules require Notification Listener access. Calendar requires calendar access or external calendar intent handling. Weather requires a manual location/provider setup and no API key is stored in the repo.

## Future Module Boundaries

Future modules are kept out of the active v1.0 add list and are shown separately for roadmap visibility:

- Routine Block
- Goal Tracker
- State Indicator
- Drift Indicator
- Focus Consistency
- Saved Articles
- Bookmarks Panel
- DevBoard Feed
- Habit Tracker
- Finance Snapshot
- Log Viewer
- Module Debug Info
- System Info Dump
- Performance Monitor
- Feature Flags Panel

Terminal Shortcut and Terminal Panel are active v1.0 modules and open the embedded AXIOM Terminal panel.
