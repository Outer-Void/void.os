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

- Font: `monospace`
- Icon pack: `com.outervoid.voidsys`
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

The active settings catalog only exposes bundled or Android-backed font choices. Because AXIOM currently does not bundle font files, active choices are limited to Android system families that visibly change UI on device:

- Default / Sans
- Serif
- Monospace
- Casual
- Cursive

Future expanded font packs may add JetBrains Mono, Roboto Mono, Space Mono, Fira Code, Inter, Orbitron, Share Tech Mono, and other bundled open-license fonts after the files are packaged in `res/font`.

## Icon Pack Logic

VOID.SYS is the native default icon system:

- Default package: `com.outervoid.voidsys`
- Native bundled icon assets are included in AXIOM.
- If standalone VOID.SYS is installed, AXIOM reports the standalone package as installed.
- If standalone VOID.SYS is missing, AXIOM native icons remain available.
- `System Default` bypasses VOID.SYS and uses installed app icons.

## Known Limitations

Generic third-party icon pack parsing is not implemented in the active settings UI. Appfilter parsing and user-selected package scanning remain future work.

## System Panel Access

System Panel access controls whether the AXIOM internal control panel is available from the dock.

## Future Icon Pack Plan

Future phases can add icon pack discovery, appfilter XML parsing, per-package drawable lookup, cache invalidation, and preview states without changing the public settings model.
