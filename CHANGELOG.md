# Changelog

## v2.0.0-dev Cockpit Start

- Started AXIOM Launcher v2.0 cockpit development line and set debug version to `2.0.0-dev`.
- Added Home dashboard system metrics strip for battery, storage, network status, and process RAM.
- Added explicit v2 packaging architecture docs for AXIOM, VOID.SYS, and AXIOM Shooter product roles.
- Updated bundled AXIOM Shooter menu with Leaderboards and the `VOID. FOCUS. DESTROY.` tagline.
- Preserved native VOID.SYS and native AXIOM Shooter inside AXIOM.

## Wallpaper Artwork + Shooter Launch Hardening

- Added the recreated AXIOM lock-screen wallpaper artwork at `docs/assets/axiom-launcher-wallpaper.png` and packaged it as `res/drawable-nodpi/axiom_wallpaper.png`.
- Updated wallpaper generation to use the full AXIOM wallpaper artwork directly instead of drawing a small logo over a solid black bitmap.
- Hardened bundled Axiom Shooter launch with an explicit internal component/class intent, retry fallback, and a visible Settings > Modules launch row.
- Packaged LibGDX `libgdx.so` native runtime libraries for `armeabi-v7a`, `arm64-v8a`, `x86`, and `x86_64` so the bundled Shooter activity can load on-device.

## Feed Gesture, Wallpaper Fallback, and Mockup Visual Sync

- Changed AXIOM Feed navigation to avoid widget-panel conflicts: swipe right from the Home favorites panel opens Feed, while left swipe remains reserved for Home widget panels.
- Updated AXIOM Feed return gesture: swipe left from Feed returns Home; Back still returns Home.
- Replaced the static Home clock/date block with an At a Glance panel that swipes between Clock/Date/System Online and Now Playing.
- Added notification-listener setup for At a Glance media sessions; AXIOM shows `MUSIC ACCESS REQUIRED` until Android grants access, then reads active media sessions instead of faking track data.
- Added `SET_WALLPAPER` support and a generated-wallpaper system handoff fallback when direct lock-screen wallpaper install is blocked by the phone.
- Deepened AXIOM matte surfaces, app icon tile beveling, dock tile depth, and controlled accent behavior toward the supplied AXIOM mockup.
- Synced the rebuilt VOID.SYS mockup-aligned native assets into AXIOM: 578 bundled PNG icons across Android densities, backed by 590 source SVG icons and 1,908 icon-map lookups.

## Shooter Launch + VOID.SYS 500+ Sync

- Hardened Axiom Shooter launch to use the live Activity context, explicit internal component/package routing, foreground reuse flags, fullscreen game theme, keep-screen-on flag, and diagnostic logging.
- Fixed release-token validation in the bundled `VoidGameActivity` so internal AXIOM source package checks compare against the AXIOM application id instead of the game activity package.
- Synced expanded VOID.SYS native assets into AXIOM: 578 bundled PNG icons across Android densities and 1,908 icon-map lookups.
- Added Android-safe synced resource names for digit-leading apps such as `app_7_eleven`.

## AXIOM Feed Module System

- Added `LauncherState.AxiomFeed`, Home gesture navigation, Back return to Home, and AXIOM-styled `AxiomFeedScreen`.
- Added feed architecture under `com.outervoid.axiom.feed`: module types, categories, config, registry, renderer, and DataStore-backed repository.
- Added Feed Configuration and Module Library screens with enable/disable, collapse/expand, remove, re-add, and Up/Down reorder controls.
- Added Settings > AXIOM FEED rows: Enable AXIOM Feed, Configure Feed, Reset Feed Layout, and Minimal Feed Mode.
- Implemented functional local modules: System Status, Clock Panel, Focus Panel, Task List, Session Timer, Next Action, Quick Actions, App Shortcuts, Notes, Minimal Clock, System Dot Indicator, Focus Only Panel, Black Screen Module, and Observe Mode.
- Added permission/setup states for weather, calendar, notification, usage metrics, contacts, media, location, health, GitHub, RSS, and manual source modules without faking data.
- Marked v1.1/planned modules visibly instead of pretending incomplete integrations are live.

## Visual System Alignment

- Synced the regenerated VOID.SYS native asset set into AXIOM: 430 bundled PNG icons across Android densities and 1,477 icon-map lookups.
- Added `AxiomFallbackIconProvider` so unmatched apps render controlled matte AXIOM fallback symbols instead of raw colorful Android icons.
- Added icon style modes: `VOID.SYS Native`, `AXIOM Fallback`, and `System Icons`.
- Tightened accent discipline: app icon borders use neutral system borders, active system tiles use subdued accent borders, and toggles use reduced accent saturation.
- Updated App Drawer header to `SEARCH APPS` and preserved the dark matte layout language across Home, App Drawer, System Panel, dock, folders, and settings.

## Device-Test Repair Pass

- Replaced the stale wallpaper source with the user-provided AXIOM logo at `res/drawable-nodpi/axiom_logo.png`.
- Hardened AXIOM lock-screen wallpaper generation: dynamic wallpaper sizing, centered logo, matte black background, amber signal line, and safe logging.
- Restricted wallpaper failures now show `WALLPAPER UNAVAILABLE` instead of silently failing.
- Hardened widget binding: replacement deletes old widget IDs, bind denial shows `WIDGET PERMISSION REQUIRED`, OEM bind launch failure shows `WIDGET UNAVAILABLE`, and widget configuration activities are handled.
- Corrected Settings routing so `Choose Default Launcher` uses home/default-app settings and `Open Phone Settings` opens Android settings directly.
- Corrected dock system behavior: tap respects the AXIOM System Panel toggle; long-press opens Android Phone Settings.
- Updated Messages fallback to use `smsto:` before known package launches.
- Updated About licensing text and removed stale folder-placeholder language.
- Confirmed debug build compiles with bundled `com.outervoid.voidgame.VoidGameActivity` intact.

## Device-Test Routing, Widgets, Wallpaper, 7-Slot Dock, About/Contact, Permissions

- Fixed `LauncherState.Contact` routing: `ContactScreen` now exists as a separate composable with Email, Outer-Void GitHub, and Justadudeinspace GitHub rows. No longer renders `AboutScreen` for the Contact route.
- Fixed `AboutScreen`: removed merged contact content (`onEmail` param, Email row). About is now product-only (version, product, VOID.SYS, Shooter, License, GitHub links).
- Fixed `WidgetPickerScreen`: added `panelIndex: Int` and `onSelectProvider: (AppWidgetProviderInfo) -> Unit` — selecting a widget now triggers the bind/allocate flow.
- Fixed `HomeScreen.onOpenWidgetPicker` type: changed from `() -> Unit` to `(Int) -> Unit` so the panel index is passed through to the picker.
- Fixed `DockBar`: rewritten from 5 slots — Phone, LeftFolder, Settings (long-press = Shooter), AppDrawer (focal), System, RightFolder, Messages. Button size 40/36dp, spacing 5/4dp, paddings 8/6dp.
- Fixed `EntitlementManager`: initial state set to `DebugBypass` immediately in debug builds — no more brief `LockedScreen` flash during development.
- Fixed `AxiomWallpaperManager.createWallpaper()`: replaced hardcoded 1080×2400 with dynamic screen-size detection via `WindowManager` (API 30+ path: `currentWindowMetrics.bounds`; legacy path: `getRealMetrics`).
- Added `SystemSettingsNavigator`: centralised Android Settings intent dispatch with fallback chain.
- Added `PermissionManager`: checks default-launcher status and BIND_APPWIDGET permission at runtime.
- Added `PermissionSetupScreen`: shows Default Launcher, Widget Binding, Wallpaper note, and VOID.SYS status. Reachable via Settings > Setup > Permissions & Setup.
- Added `LauncherState.PermissionSetup` enum value; BackHandler covers new state.
- Added widget persistence: `SettingsKeys.PanelWidgets`, `AxiomSettings.panelWidgets` + `widgetForPanel()`, `SettingsRepository.updatePanelWidget()`, `LauncherViewModel.setWidgetForPanel()`.
- Added `BoundWidgetPanel` composable inside `HomeScreen`: renders a bound `AppWidgetHostView` or "WIDGET UNAVAILABLE" fallback; long-press replaces widget.
- Added `AxiomWidgetHost` lifecycle management in `MainActivity` (startListening/stopListening via DisposableEffect).
- Added widget bind result launcher (`rememberLauncherForActivityResult`) for devices that deny `bindAppWidgetIdIfAllowed`.
- Added `vm.openDialer()` and `vm.openMessages()` in `LauncherViewModel`; wired to Phone and Messages dock slots in `HomeScreen` and `SystemPanelScreen`.
- Added `res/drawable-nodpi/axiom_logo.png` for runtime wallpaper generation.
- Added Settings > Setup section with Permissions & Setup row.
- Build confirmed clean.

## Debug Build Alignment — Native VOID.SYS + Bundled Shooter

- Settings Modules section: AXIOM SHOOTER value → "Included", subtitle → "Hidden arcade Easter egg".
- Settings Modules section: VOID.SYS standalone subtitle → "Standalone: Installed / Optional".
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
