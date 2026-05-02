# Changelog

## Debug Build Alignment — Native VOID.SYS + Bundled Shooter

- Settings Modules section: AXIOM SHOOTER value → "Included", subtitle → "Hidden arcade Easter egg".
- Settings Modules section: VOID.SYS standalone subtitle → "Standalone: Installed / Not Installed".
- Bundled PauseScreen marked deprecated — GameScreen owns the inline pause overlay; only one pause system active.
- void.game PauseScreen marked deprecated in sync.
- Build confirmed clean for debug APK.

## AXIOM v1 Customization Controls

- Added persistent accent color, icon shape, clock format, favorites, home panel, hidden app, lock-screen wallpaper, and folder settings.
- Added Home favorites with configurable counts, lock behavior, and app info/remove actions.
- Added 1-5 Home panels with real widget-provider discovery through Android `AppWidgetManager`.
- Added hidden-app filtering for App Drawer/Search.
- Added default-launcher, About, Contact, clock/date, wallpaper, and expanded phone settings actions.
- Added basic left/right dock folder app selection and launch panels.
- Kept VOID.SYS native icons, bundled fonts, and Axiom Shooter internal launch/security gates intact.

## Terminal Panel Scaffold

- Added developer toggle placeholder.
- Added documentation.
- Feature scheduled for v1.1.

## Final Integration Tightening

- Split AXIOM Shooter missing/locked/unavailable dialogs.
- Hardened System Panel setting enforcement.
- Updated full device-test instructions.


## Licensing Backend Planning

- Added entitlement/server verification architecture plan.

## Integration Hardening Pass

- Enforced System Panel setting.
- Added reactive module status.
- Removed unmanaged game launch path.
- Added device-test matrix.
- Polished AXIOM dialogs/screens.

## A4 Stabilization Pass

- Updated stale docs.
- Hardened VOID.SYS icon lookup keys.
- Added real flashlight toggle state.
- Confirmed A2 System Panel routing.
- Confirmed proprietary licensing.

## A3 Visual Transformation Baseline

- Rebuilt visual components around the AXIOM / VOID.SYS matte black system.
- Tightened Ambient boot sequence with Canvas line, ghost watermark, lower-left status, and pulsing amber dot.
- Refined Home and Search styling with corner framing, short amber rules, rounded icon cells, and muted labels.
- Reworked DockBar into a bordered matte control strip with Canvas-only controls and focal indicator.
- Added visual system documentation for palette, typography, dock, icon cells, and accent discipline.

## A5 Settings Foundation

- Added persistent AXIOM settings backed by DataStore Preferences.
- Added app-wide font selection, icon pack selection, layout controls, interface toggles, and haptic preferences.
- Added AXIOM-styled settings screen and reusable settings controls.
- Wired settings into Home, Search, Dock, system UI visibility, theme typography, and icon provider fallback behavior.
- Documented settings architecture, defaults, font catalog, and VOID.SYS icon pack logic.
