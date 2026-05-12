# AXIOM Launcher v2.0 Development Plan

AXIOM Launcher v2.0 is the dark Android control cockpit line. It keeps every v1 system and layers a stronger dashboard, navigation, and native product integration model on top.

## Product Role

- Main product: `com.outervoid.axiom`
- Includes native VOID.SYS resources and icon map.
- Includes native AXIOM Shooter module.
- Does not require standalone VOID.SYS or standalone AXIOM Shooter.
- Detects standalone companion products only for awareness/status.

## V2 Active Direction

- Matte black dashboard with amber/orange control language.
- Home cockpit with At a Glance and real system metrics.
- AXIOM Feed remains a user-built control layer, not a recommendation feed.
- System Panel stays settings-intent backed where Android blocks direct toggles.
- Terminal Panel remains active with embedded shell and Termux fallback.
- Wallpaper supports home, lock, and both targets with system picker fallback.

## V2 Development Boundaries

- Release security remains blocked until Play Licensing, Play Integrity, and server signature verification are complete.
- Native Shooter source is mirrored from the standalone game source until a shared Gradle module is introduced.
- No v2 release tag should be created until device testing passes.
