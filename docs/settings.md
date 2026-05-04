# AXIOM Settings

AXIOM Launcher settings are stored with AndroidX DataStore Preferences. The app exposes one settings model, `AxiomSettings`, through `SettingsRepository` and `LauncherViewModel`, then applies it across theme, launcher grids, search, dock controls, system UI visibility, and icon loading.

## Architecture

- Model: `app/src/main/java/com/outervoid/axiom/settings/AxiomSettings.kt`
- Keys: `SettingsKeys.kt`
- Persistence: `SettingsRepository.kt`
- Font catalog: `FontOption.kt`
- Icon pack catalog: `IconPackOption.kt`
- UI: `ui/screens/SettingsScreen.kt`
- Controls: `ui/components/SettingsRow.kt`, `SettingsSection.kt`, `SettingsToggle.kt`, `SettingsDropdown.kt`

## Defaults

- Font: `jetbrains_mono`
- Icon pack: `com.outervoid.voidsys`
- Icon shape: `ROUNDED_SQUARE`
- Accent color: `AMBER`
- Favorites count: `10`
- Favorites locked: disabled
- Home panels: `1`
- Hidden apps: none
- Clock format: `SYSTEM`
- Lock-screen wallpaper: disabled
- Left/right dock folders: named `CUSTOM FOLDER` with no selected apps
- App labels: enabled
- Grid columns: `4`
- Icon scale: `0.72`
- SYSTEM ONLINE: enabled
- Ambient boot: enabled
- Amber accent: enabled
- Compact mode: disabled
- Status/navigation hiding: disabled
- Swipe-up App Drawer: enabled
- System panel: disabled by default; when enabled, dock access opens the AXIOM internal control panel
- Haptics: enabled
- AXIOM Feed: enabled
- Minimal Feed Mode: disabled

## Fonts

The active settings catalog exposes bundled open-license font choices. Each selectable option maps to a real file under `app/src/main/res/font`, and `tools/fetch_fonts.sh` refreshes those assets from Google Fonts and official Nerd Fonts releases.

Bundled categories:

- Core AXIOM mono fonts.
- Nerd Font variants.
- Popular sans fonts.
- Popular serif fonts.
- Display/system fonts.

Do not add active font options unless the font file is bundled or generated into `res/font`.

## Icon Style Logic

VOID.SYS is the native default icon system:

- Default package: `com.outervoid.voidsys`
- Native bundled icon assets are included in AXIOM.
- Default mode is `VOID.SYS Native`: bundled/native icons first, AXIOM symbolic fallback when no match exists. Current bundled coverage is 578 PNG drawable icons, 590 source SVG icons, and 1,908 lookup mappings.
- `AXIOM Fallback` uses generated matte symbolic fallback icons for every app.
- `System Icons` is the only mode that uses colorful installed app launcher icons.
- If standalone VOID.SYS is installed, AXIOM reports the standalone package as installed.
- If standalone VOID.SYS is missing, AXIOM native icons remain available.

## v1 Customization Controls

- Accent color options are persisted by key and applied to headers, selected controls, dock icons, SYSTEM ONLINE, clock color, and Axiom Shooter transition pulse.
- Icon shape is applied to Home favorites and App Drawer icon tiles using Compose shape masks.
- Favorites are real Home items. Empty favorites auto-fill from installed apps until the user pins explicit favorites; locked favorites block edit/remove actions.
- Home panels support one Favorites panel plus up to four widget panels. Widget discovery uses Android `AppWidgetManager`, binding uses the platform approval flow when needed, configuration activities are handled, and replacement deletes the old widget host ID.
- Hidden apps are stored as package/activity keys and filtered from App Drawer/Search only.
- Lock-screen wallpaper uses the bundled `axiom_wallpaper.png` resource generated from `docs/assets/axiom-launcher-wallpaper.png` and Android `WallpaperManager.FLAG_LOCK` where supported. Restricted devices get a generated-image handoff to Android's wallpaper setter when available; otherwise AXIOM shows `WALLPAPER UNAVAILABLE`.
- Folder customization stores left/right folder app sets and an icon style key. Folder panels launch selected apps.
- AXIOM Feed settings expose Enable AXIOM Feed, Configure Feed, Reset Feed Layout, and Minimal Feed Mode. Feed module configuration is stored separately in `FeedRepository` using DataStore Preferences.

## At a Glance

Home At a Glance replaces the static clock/date block with a swipeable control surface:

- Clock/Date page: opens Clock and Calendar through the existing Home actions.
- Now Playing page: reads active Android media sessions only after the user grants AXIOM notification access.
- Setup state: shows `MUSIC ACCESS REQUIRED` and opens Android notification listener settings.
- Gesture guard: swipes that begin on At a Glance are reserved for the panel so they do not trigger AXIOM Feed or Home widget panels.

## AXIOM Feed Settings

Settings > AXIOM FEED controls the optional right-swipe control layer:

- `Enable AXIOM Feed`: controls whether right-swipe from Home opens the feed.
- `Configure Feed`: opens active layout controls for reorder, enable/disable, collapse/expand, remove, and add module.
- `Reset Feed Layout`: restores System Status, Weather, Calendar, Focus Panel, and Quick Actions.
- `Minimal Feed Mode`: replaces active modules with Minimal Clock, System Dot Indicator, and Focus Only Panel.

AXIOM Feed is not Google Discover, a news feed, or an infinite-scroll recommendation surface. Permission-bound modules show setup-required states until Android access is granted.

## Known Limitations

Generic third-party icon pack parsing is not implemented in the active settings UI. Appfilter parsing and user-selected package scanning remain future work.

Widget hosting is implemented as far as Android allows for third-party launchers in this pass: the picker reads real installed widget providers, but some widgets require the platform bind-permission flow before an `AppWidgetHostView` can be attached. Some OEM builds block direct bind requests; AXIOM reports that state instead of crashing.

## System Panel Access

System Panel access controls whether the AXIOM internal control panel is available from dock tap. Dock long-press opens Android Phone Settings.

## v1.0 Boundaries

- Terminal Panel is active in v1.0 as an embedded `/system/bin/sh` session with Termux bridge fallback.
- Drag/drop folder and favorite ordering remain planned.

## Future Icon Pack Plan

Future phases can add icon pack discovery, appfilter XML parsing, per-package drawable lookup, cache invalidation, and preview states without changing the public settings model.
