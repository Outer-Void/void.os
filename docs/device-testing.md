# Device Testing Matrix

## Build APKs Locally

AXIOM:

```bash
cd /home/jadis/void.os
./gradlew assembleDebug
```

VOID.SYS:

```bash
cd /home/jadis/void.sys
./gradlew assembleDebug
```

VOID.GAME:

```bash
cd /home/jadis/void.game
./gradlew assembleDebug
```

## Install Order

```bash
adb install -r /home/jadis/void.sys/app/build/outputs/apk/debug/app-debug.apk
adb install -r /home/jadis/void.game/android/build/outputs/apk/debug/android-debug.apk
adb install -r /home/jadis/void.os/app/build/outputs/apk/debug/app-debug.apk
```

## 1. AXIOM As Launcher

- Set AXIOM as the default launcher.
- Press Home.
- Confirm Home loads.
- Confirm the app grid loads.
- Confirm Search opens.
- Confirm Settings opens.
- Confirm System Panel opens only when `System panel enabled` is on.
- Turn `System panel enabled` off and confirm the dock system control shows `SYSTEM PANEL DISABLED` with `Enable it in Settings to access the AXIOM system control panel.`

## 2. VOID.SYS

- Confirm Settings shows VOID.SYS installed.
- Confirm the app grid attempts VOID.SYS icons.
- Uninstall VOID.SYS.
- Confirm Settings updates live.
- Confirm system icon fallback.

## 3. VOID.GAME

- Confirm Settings shows AXIOM Shooter installed.
- Long-press the AXIOM wordmark/logo.
- Confirm amber pulse transition.
- Confirm `AXIOM SHOOTER` / `SYSTEM ONLINE.` text.
- Confirm VOID.GAME opens.
- Press Back and confirm Pause opens.
- Exit and confirm control returns to AXIOM/Android.

## 4. Missing Module

- Uninstall VOID.GAME.
- Long-press the AXIOM wordmark/logo.
- Confirm `AXIOM SHOOTER NOT INSTALLED` dialog.

## 5. Locked Behavior

- Confirm debug bypass allows development testing.
- Document that release builds intentionally fail closed until real Play Licensing, Play Integrity, and server signatures exist.
