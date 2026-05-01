# AXIOM Launcher

Package: `com.outervoid.axiom`

AXIOM Launcher is proprietary paid Android launcher software by Outer-Void. Planned price: `$4.99`.

Status: AXIOM Launcher integration hardening baseline.

AXIOM provides a controlled Android environment, not just a launcher: a home launcher, app grid, search, settings, System Panel, VOID.SYS icon integration, and hidden Axiom Shooter activation.

## Product Modules

- **AXIOM Launcher**: core paid product and launcher shell.
- **VOID.SYS**: intended/default proprietary icon system for AXIOM. Also planned as a standalone `$1.99` icon pack.
- **Axiom Shooter / VOID.GAME**: hidden protected module launched only through AXIOM by long-pressing the AXIOM wordmark/name.

## Security Foundation

AXIOM includes scaffolding for paid entitlement, Play Integrity, package signature checks, a sealed product signature file, and a signed AXIOM-to-VOID.GAME launch contract. These layers are defensive controls; they are not a claim that the app is hack-proof. Release builds fail closed until real entitlement and server-side signing are connected. Debug builds use an explicit `BuildConfig.DEBUG` bypass for development.

Architecture plan: [docs/licensing-backend-plan.md](docs/licensing-backend-plan.md).

Current integration hardening includes System Panel setting enforcement, reactive module status for VOID.SYS and Axiom Shooter, protected Axiom Shooter launch routing, and VOID.SYS native icon lookup. Release security still requires Google Play Licensing, Play Integrity, and server-issued signature verification before production distribution.

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
