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
- Swipe-up search: enabled
- System panel: disabled by default; when enabled, dock access opens the AXIOM internal control panel
- Haptics: enabled

## Fonts

The settings catalog includes Core AXIOM mono fonts, Nerd Font variants, popular sans, popular serif, and display/system choices. Fonts are referenced by system family name. AXIOM does not bundle proprietary font files; unavailable fonts fall back through Android typeface resolution and keep the app usable with monospace styling.

## Icon Pack Logic

VOID.SYS is the intended and default icon system:

- Default package: `com.outervoid.voidsys`
- If VOID.SYS is installed, AXIOM reports it as `ACTIVE / INSTALLED`.
- If it is missing, AXIOM keeps VOID.SYS selected but falls back to system app icons.
- `System Default` bypasses VOID.SYS and uses installed app icons.

## Known Limitations

Generic third-party icon pack parsing is not implemented in A5. The `Installed Icon Pack` option exists as architecture for a later phase, but appfilter parsing and user-selected package scanning remain future work.

## System Panel Access

System Panel access controls whether the AXIOM internal control panel is available from the dock.

## Future Icon Pack Plan

Future phases can add icon pack discovery, appfilter XML parsing, per-package drawable lookup, cache invalidation, and preview states without changing the public settings model.
