# AXIOM Launcher

Package: `com.outervoid.axiom`

AXIOM Launcher is proprietary paid Android launcher software by Outer-Void. Planned price: `$4.99`, including the launcher, native VOID.SYS icon assets, and the hidden Axiom Shooter Easter egg.

Status: v1 customization build + device-test ready foundation.

AXIOM provides a controlled Android environment, not just a launcher: a separated home surface, app drawer/search, settings, System Panel, bundled font customization, native VOID.SYS icon integration, real favorites, widget panels, hidden apps, basic dock folder customization, lock-screen wallpaper setup, and hidden bundled Axiom Shooter activation.

## Product Modules

- **AXIOM Launcher**: core paid product and launcher shell.
- **Bundled fonts**: the active font selector uses packaged open-license font resources under `app/src/main/res/font`.
- **Customization controls**: accent color, icon shape, clock format, favorites count/lock, home panels, hidden apps, lock-screen wallpaper, default-launcher chooser, and expanded phone settings shortcuts.
- **VOID.SYS native icon system**: bundled inside AXIOM. Users do not install the standalone VOID.SYS package for AXIOM to have native icons.
- **Axiom Shooter / VOID.GAME**: hidden protected Easter egg bundled inside AXIOM and launched by long-pressing the AXIOM wordmark/name. Users do not install Axiom Shooter separately.
- **Wallpaper setup**: bundled `res/drawable-nodpi/axiom_logo.png` is used to generate a matte black AXIOM lock-screen wallpaper at runtime. Devices that block direct lock-screen wallpaper writes show a clear AXIOM-styled failure message.
- **Widget setup**: widget panels use Android `AppWidgetHost`, real installed widget providers, platform bind/configuration flows, and persisted widget IDs per panel.
- **Seven-slot dock**: Phone, Left Folder, AXIOM Settings, App Drawer, System/Phone Settings, Right Folder, and Messages.
- **VOID.SYS standalone**: separate proprietary `$1.99` icon pack for Nova, Lawnchair, and other compatible launchers.

## Security Foundation

AXIOM includes scaffolding for paid entitlement, Play Integrity, package signature checks, a sealed product signature file, and a signed AXIOM-to-VOID.GAME launch contract. These layers are defensive controls; they are not a claim that the app is hack-proof. Release builds fail closed until real entitlement and server-side signing are connected. Debug builds use an explicit `BuildConfig.DEBUG` bypass for development.

Architecture plan: [docs/licensing-backend-plan.md](docs/licensing-backend-plan.md).

Current integration hardening includes System Panel disabled-state enforcement, bundled VOID.SYS icon lookup, standalone VOID.SYS package status, protected internal Axiom Shooter launch routing, and locked/unavailable handling for release gates. Device-test instructions are available in [docs/device-testing.md](docs/device-testing.md). Real licensing backend work remains future work after device behavior confirmation.

## Planned Features (v1.1)

- **AXIOM Terminal Panel**: optional developer feature documented in [docs/terminal-panel.md](docs/terminal-panel.md). It is not included in the current build.
- **Advanced custom dock folder management**: v1 includes names, icon style, app selection, and launch panels. Drag/drop ordering remains planned.

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
