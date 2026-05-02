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
- Change Accent Color and confirm headers, active toggles, dock controls, SYSTEM ONLINE, clock, and Axiom Shooter pulse change color.
- Change Icon Shape and confirm Home favorites and App Drawer icon tiles change masks.
- Change Clock Format and confirm Home clock follows System / 12 hour / 24 hour.
- Tap the clock and confirm a clock/alarm app or supported fallback opens.
- Tap the date and confirm a calendar app opens when present.
- Change Favorites Count to 5, 10, 15, 20, and 25 and confirm Home changes slot count.
- Enable Lock Favorites and confirm favorite long-press edit actions are blocked.
- Set Home Panels to 2-5, swipe horizontally, and confirm widget panels appear.
- Long-press a widget panel and confirm the picker lists real installed Android widget providers.
- Hide an app in Settings > App Drawer > Hidden Apps and confirm it disappears from App Drawer/Search.
- Use Choose Default Launcher and confirm Android default/home settings opens.
- Use About AXIOM, GitHub links, and Email and confirm external intents open.
- Use Phone Settings actions and confirm each opens an Android settings screen or falls back to general Settings.
- Select apps for left/right dock folders and confirm those panels show and launch selected apps.
- Set AXIOM Lock Screen Wallpaper and confirm success or the documented device restriction message.
- Confirm System Panel opens only when `System panel enabled` is on.
- Turn `System panel enabled` off and confirm the dock system control shows `SYSTEM PANEL DISABLED` with `Enable it in Settings to access the AXIOM system control panel.`

## 2. VOID.SYS

- Confirm AXIOM native icons exist without standalone VOID.SYS installed.
- Confirm Settings shows `Native: Included`.
- Optionally install standalone VOID.SYS and confirm Settings shows the standalone package as installed.
- Uninstall standalone VOID.SYS and confirm AXIOM native icons remain available.

## 3. VOID.GAME

- Confirm Settings > Modules shows AXIOM SHOOTER `Included` and subtitle `Hidden arcade Easter egg`.
- Long-press the AXIOM wordmark/logo on the Home screen.
- Confirm amber pulse transition overlay.
- Confirm `AXIOM SHOOTER` / `SYSTEM ONLINE.` text during transition.
- Confirm bundled Axiom Shooter opens (no external package install required).
- Press Back during gameplay and confirm pause overlay appears inside GameScreen.
- Tap RESUME, RESTART, or EXIT TO AXIOM from the pause overlay.
- Exit and confirm control returns to AXIOM/Android.
- Note: PauseScreen.kt is deprecated dead code — all pause logic lives inside GameScreen inline.

## 4. Locked Behavior

- Confirm debug bypass allows development testing.
- Document that release builds intentionally fail closed until real Play Licensing, Play Integrity, and server signatures exist.
