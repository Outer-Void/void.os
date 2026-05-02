# Device Testing Matrix

## Build APKs Locally

AXIOM:

```bash
cd /home/jadis/void.os
./gradlew assembleDebug
```

## Install Order

```bash
adb install -r /home/jadis/void.os/app/build/outputs/apk/debug/app-debug.apk
```

Optional companion-package checks:

```bash
adb install -r /home/jadis/void.sys/app/build/outputs/apk/debug/app-debug.apk
```

The VOID.GAME debug APK is development-only. Do not include it in the AXIOM release install flow.

## 1. AXIOM As Launcher

- Set AXIOM as the default launcher.
- Press Home.
- Confirm Home loads.
- Confirm the app grid loads.
- Confirm the center dock button opens App Drawer.
- Confirm Home does not render the full installed app list.
- Confirm left and right custom folder dock slots open placeholder panels.
- Confirm Settings opens.
- Change fonts in Settings and confirm Home, App Drawer, Settings, System Panel, dialogs, and transition text use visibly different bundled fonts.
- Confirm System Panel opens only when `System panel enabled` is on.
- Turn `System panel enabled` off and confirm the dock system control shows `SYSTEM PANEL DISABLED` with `Enable it in Settings to access the AXIOM system control panel.`

## 2. VOID.SYS

- Confirm AXIOM native icons exist without standalone VOID.SYS installed.
- Confirm Settings shows `Native: Included`.
- Optionally install standalone VOID.SYS and confirm Settings shows the standalone package as installed.
- Uninstall standalone VOID.SYS and confirm AXIOM native icons remain available.

## 3. VOID.GAME

- Confirm Settings shows AXIOM Shooter `Included`.
- Long-press the AXIOM wordmark/logo.
- Confirm amber pulse transition.
- Confirm `AXIOM SHOOTER` / `SYSTEM ONLINE.` text.
- Confirm bundled Axiom Shooter opens.
- Press Back and confirm Pause opens.
- Exit and confirm control returns to AXIOM/Android.

## 4. Locked Behavior

- Confirm debug bypass allows development testing.
- Document that release builds intentionally fail closed until real Play Licensing, Play Integrity, and server signatures exist.
