# AXIOM Packaging Architecture

AXIOM Launcher ships as one paid product.

The AXIOM APK includes:

- AXIOM Launcher.
- Native VOID.SYS icon assets and `voidsys_icon_map.json`.
- Bundled Axiom Shooter hidden arcade Easter egg.

Users do not install Axiom Shooter separately. Users also do not install the standalone VOID.SYS package for AXIOM to have its native icon system.

## Icon System

AXIOM resolves launcher icons in this order:

1. Bundled VOID.SYS icons packaged inside AXIOM.
2. External `com.outervoid.voidsys` package when installed and selected.
3. System icon fallback.

The standalone VOID.SYS package remains proprietary and may be sold separately for other launchers. It is a companion product for Nova, Lawnchair, and compatible icon-pack hosts, not a required AXIOM dependency.

## Axiom Shooter

Axiom Shooter is a hidden AXIOM Easter egg opened by long-pressing the AXIOM wordmark/logo. Release UX must treat it as included with AXIOM, not as a user-installed module.

`/home/jadis/void.game` remains the source/module repo and may still build a debug APK for development. Any standalone VOID.GAME APK behavior is development-only and is not a user-facing product.

## Licensing

AXIOM, VOID.SYS, and VOID.GAME remain proprietary. Bundling assets and game code into AXIOM does not weaken release entitlement, integrity, or signature gates. Release behavior should fail closed when real licensing/signature verification is unavailable.
