# AXIOM Ecosystem Packaging Architecture

## AXIOM Launcher

Repository: `/home/jadis/void.os`  
Package: `com.outervoid.axiom`

AXIOM Launcher is the shipping home environment. It bundles native VOID.SYS resources and the native AXIOM Shooter module. Users who install AXIOM do not need the standalone icon pack or standalone game.

## VOID.SYS

Repository: `/home/jadis/void.sys`  
Package: `com.outervoid.voidsys`

VOID.SYS is also a standalone icon pack product for third-party launchers. It shares the same visual language and generated resources used by AXIOM's bundled native icon system.

## AXIOM Shooter

Repository: `/home/jadis/void.game`  
Package: `com.outervoid.voidgame`

AXIOM Shooter is also a standalone game product. The current v2 development line mirrors gameplay source between `void.game` and AXIOM's bundled native module. The intended next architecture step is a shared Gradle source/module dependency so standalone and bundled builds cannot drift.

## Launch Behavior

- Bundled AXIOM Shooter launches inside AXIOM and returns to AXIOM.
- Standalone AXIOM Shooter launches as a normal Android game and exits normally if AXIOM is not installed.
- AXIOM keeps signature/entitlement scaffolding for release hardening.
