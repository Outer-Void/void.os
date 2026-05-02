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

## Fonts

The active settings catalog exposes bundled open-license font choices. Each selectable option maps to a real file under `app/src/main/res/font`, and `tools/fetch_fonts.sh` refreshes those assets from Google Fonts and official Nerd Fonts releases.

Bundled categories:

- Core AXIOM mono fonts.
- Nerd Font variants.
- Popular sans fonts.
- Popular serif fonts.
- Display/system fonts.

Do not add active font options unless the font file is bundled or generated into `res/font`.

## Icon Pack Logic

VOID.SYS is the native default icon system:

- Default package: `com.outervoid.voidsys`
- Native bundled icon assets are included in AXIOM.
- If standalone VOID.SYS is installed, AXIOM reports the standalone package as installed.
- If standalone VOID.SYS is missing, AXIOM native icons remain available.
- `System Default` bypasses VOID.SYS and uses installed app icons.

## v1 Customization Controls

- Accent color options are persisted by key and applied to headers, selected controls, dock icons, SYSTEM ONLINE, clock color, and Axiom Shooter transition pulse.
- Icon shape is applied to Home favorites and App Drawer icon tiles using Compose shape masks.
- Favorites are real Home items. Empty favorites auto-fill from installed apps until the user pins explicit favorites; locked favorites block edit/remove actions.
- Home panels support one Favorites panel plus up to four widget panels. Widget discovery uses Android `AppWidgetManager`; actual binding may require Android user/system permission.
- Hidden apps are stored as package/activity keys and filtered from App Drawer/Search only.
- Lock-screen wallpaper uses Android `WallpaperManager.FLAG_LOCK` where supported. Restricted devices show a direct limitation message.
- Folder customization stores left/right folder app sets and an icon style key. Folder panels launch selected apps.

## Known Limitations

Generic third-party icon pack parsing is not implemented in the active settings UI. Appfilter parsing and user-selected package scanning remain future work.

Widget hosting is implemented as far as Android allows for third-party launchers in this pass: the picker reads real installed widget providers, but some widgets require the platform bind-permission flow before an `AppWidgetHostView` can be attached.

## System Panel Access

System Panel access controls whether the AXIOM internal control panel is available from the dock.

## Planned Features

- Terminal Panel remains planned for v1.1.
- Drag/drop folder and favorite ordering remain planned.

## Future Icon Pack Plan

Future phases can add icon pack discovery, appfilter XML parsing, per-package drawable lookup, cache invalidation, and preview states without changing the public settings model.
