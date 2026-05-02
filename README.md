# AXIOM Launcher

Package: `com.outervoid.axiom`

AXIOM Launcher is proprietary paid Android launcher software by Outer-Void. Planned price: `$4.99`.

Status: Integration hardening + device-test ready foundation.

AXIOM provides a controlled Android environment, not just a launcher: a home launcher, app grid, search, settings, System Panel, VOID.SYS icon integration, and hidden Axiom Shooter activation.

## Product Modules

- **AXIOM Launcher**: core paid product and launcher shell.
- **VOID.SYS**: intended/default proprietary icon system for AXIOM. Also planned as a standalone `$1.99` icon pack.
- **Axiom Shooter / VOID.GAME**: hidden protected module launched only through AXIOM by long-pressing the AXIOM wordmark/name.

## Security Foundation

AXIOM includes scaffolding for paid entitlement, Play Integrity, package signature checks, a sealed product signature file, and a signed AXIOM-to-VOID.GAME launch contract. These layers are defensive controls; they are not a claim that the app is hack-proof. Release builds fail closed until real entitlement and server-side signing are connected. Debug builds use an explicit `BuildConfig.DEBUG` bypass for development.

Architecture plan: [docs/licensing-backend-plan.md](docs/licensing-backend-plan.md).

Current integration hardening includes System Panel disabled-state enforcement, reactive module status for VOID.SYS and Axiom Shooter, separated Axiom Shooter missing/locked/unavailable states, protected Axiom Shooter launch routing, and VOID.SYS native icon lookup. Device-test instructions are available in [docs/device-testing.md](docs/device-testing.md). Real licensing backend work remains future work after device behavior confirmation.

## Planned Features (v1.1)

- **AXIOM Terminal Panel**: optional developer feature documented in [docs/terminal-panel.md](docs/terminal-panel.md). It is not included in the current build.

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
