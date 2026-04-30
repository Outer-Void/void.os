# AXIOM Launcher

System-focused Android launcher built by Outer-Void.

## Status

AXIOM Launcher A4 — System Panel + VOID.SYS Integration Foundation

## Philosophy

Stripped to function.  
Built for clarity.  
Made for control.  
Nothing extra.  
Everything intentional.

## Implemented Foundations

- A1 launcher foundation: Home launcher, app grid, search, app launching
- A2 System Panel foundation: AXIOM internal control panel with safe system action routing
- A3 visual baseline: matte black VOID.SYS-aligned interface, corner framing, controlled amber accents
- A4 VOID.SYS integration foundation: native icon lookup through `com.outervoid.voidsys`
- A5 Settings foundation: persistent font, icon pack, layout, interface, and control preferences

## VOID.SYS Integration

AXIOM defaults to the intended VOID.SYS icon pack package:

`com.outervoid.voidsys`

When installed, AXIOM loads `assets/icon_map.json` from VOID.SYS using Android package context access and resolves drawable names from the remote package. Missing packages, malformed JSON, missing mappings, or missing resources fall back to system app icons.

## Roadmap

- Full VOID.SYS package completion
- Native icon mapping expansion
- A5 Settings polish
- A6 Terminal/Termux panel
- Notification badges
- Store/release packaging

## Build

```bash
./gradlew assembleDebug
```
