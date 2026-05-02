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

## Known Limitations

Generic third-party icon pack parsing is not implemented in the active settings UI. Appfilter parsing and user-selected package scanning remain future work.

## System Panel Access

System Panel access controls whether the AXIOM internal control panel is available from the dock.

## Planned Features

- Terminal Panel remains planned for v1.1.
- Dock custom folders are scaffolded as left/right placeholder panels. Full folder app selection and editing remain planned.

## Future Icon Pack Plan

Future phases can add icon pack discovery, appfilter XML parsing, per-package drawable lookup, cache invalidation, and preview states without changing the public settings model.
