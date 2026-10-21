# AXIOM Launcher

Package: `com.outervoid.axiom`

AXIOM Launcher is proprietary paid Android launcher software by Outer-Void. Planned price: `$4.99`, including the launcher, native VOID.SYS icon assets, and the hidden Axiom Shooter Easter egg.

Status: v2.0.0-dev cockpit build on top of the v1 device-test foundation.

AXIOM provides a controlled Android environment, not just a launcher: a separated home surface, app drawer/search, AXIOM Feed, settings, System Panel, bundled font customization, native VOID.SYS icon integration, real favorites, widget panels, hidden apps, basic dock folder customization, lock-screen wallpaper setup, and hidden bundled Axiom Shooter activation.

## Product Modules

- **AXIOM Launcher**: core paid product and launcher shell.
- **Bundled fonts**: the active font selector uses packaged open-license font resources under `app/src/main/res/font`.
- **Customization controls**: accent color, icon shape, clock format, favorites count/lock, home panels, hidden apps, lock-screen wallpaper, default-launcher chooser, and expanded phone settings shortcuts.
- **At a Glance**: Home clock/date is a wired control surface that can swipe between clock/date/system status and currently playing Android media sessions after notification access setup.
- **VOID.SYS native icon integration**: bundled package/activity lookup data with dedicated artwork and generated AXIOM fallbacks. In this repository the map has 1,908 keys and refers to 547 drawable names; many targets intentionally use category/monogram fallbacks. Users do not need the standalone VOID.SYS package for the bundled integration.
- **AXIOM fallback icons**: unmatched apps use generated dark symbolic fallback icons by default. Colorful Android system icons appear only when the user selects `System Icons`.
- **Axiom Shooter / VOID.GAME**: hidden protected Easter egg bundled inside AXIOM and launched by long-pressing the AXIOM wordmark/name. Users do not install Axiom Shooter separately.
- **Wallpaper setup**: bundled `res/drawable-nodpi/axiom_wallpaper.png` is the full AXIOM lock-screen artwork generated from `docs/assets/axiom-launcher-wallpaper.png`. Devices that block direct lock-screen wallpaper writes get a system wallpaper setup handoff when Android allows it, otherwise AXIOM shows a clear failure message.
- **Widget setup**: widget panels use Android `AppWidgetHost`, real installed widget providers, platform bind/configuration flows, and persisted widget IDs per panel.
- **Seven-slot dock**: Phone, Left Folder, AXIOM Settings, App Drawer, System/Phone Settings, Right Folder, and Messages.
- **AXIOM Feed**: optional right-swipe user-built control layer, not a news feed. Default modules are System Status, Weather setup, Calendar setup, Focus Panel, and Quick Actions. Users can configure, reorder, collapse, remove, and re-add modules from the module library.
- **VOID.SYS standalone**: separate proprietary `$1.99` icon pack for Nova, Lawnchair, and other compatible launchers.

## Security Foundation

AXIOM includes scaffolding for paid entitlement, Play Integrity, package signature checks, a sealed product signature file, and a signed AXIOM-to-VOID.GAME launch contract. These layers are defensive controls; they are not a claim that the app is hack-proof. Release builds fail closed until real entitlement and server-side signing are connected. Debug builds use an explicit `BuildConfig.DEBUG` bypass for development.

Architecture plan: [docs/licensing-backend-plan.md](docs/licensing-backend-plan.md).

Current integration hardening includes System Panel disabled-state enforcement, bundled VOID.SYS icon lookup, standalone VOID.SYS package status, protected internal Axiom Shooter launch routing, and locked/unavailable handling for release gates. Device-test instructions are available in [docs/device-testing.md](docs/device-testing.md). Real licensing backend work remains future work after device behavior confirmation.

AXIOM Feed documentation: [docs/axiom-feed.md](docs/axiom-feed.md).
V2 plan: [docs/v2-plan.md](docs/v2-plan.md). Packaging architecture: [docs/packaging-architecture.md](docs/packaging-architecture.md).

## V2 Development Notes

- **AXIOM Terminal Panel**: active embedded shell with Termux fallback, documented in [docs/terminal-panel.md](docs/terminal-panel.md).
- **AXIOM Shooter**: bundled native module remains inside AXIOM; standalone `void.game` now mirrors the same gameplay source for v2 development.
- **Advanced custom dock folder ordering**: v1/v2 includes names, icon style, app selection, and launch panels. Drag/drop ordering remains future work.

## Build

```bash
./gradlew assembleDebug
```

## Release

```bash
./gradlew assembleRelease
```

## License

Proprietary and confidential. See `LICENSE`.
