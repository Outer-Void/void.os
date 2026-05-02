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
- Confirm left and right custom folder dock slots open custom folder panels.
- Confirm Settings opens.
- Change fonts in Settings and confirm Home, App Drawer, Settings, System Panel, dialogs, and transition text use visibly different bundled fonts.
- Change Accent Color and confirm headers, active toggles, dock controls, SYSTEM ONLINE, clock, and Axiom Shooter pulse change color.
- Change Icon Shape and confirm Home favorites, App Drawer, folder panels, and dock-adjacent app tiles change masks.
- Set Icon Style Mode to `VOID.SYS Native` and confirm common apps use matte VOID.SYS icons while unmatched apps use AXIOM fallback icons.
- Set Icon Style Mode to `AXIOM Fallback` and confirm all app tiles remain dark symbolic icons.
- Set Icon Style Mode to `System Icons` and confirm colorful installed app icons appear only in this explicit mode.
- Set Accent Color to Red and confirm the UI remains mostly TextPrimary/TextSecondary/TextDim with red limited to clock, small rules, active toggles, and dock/system glyphs.
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
- Set AXIOM Lock Screen Wallpaper and confirm success or `WALLPAPER UNAVAILABLE`.
- Confirm System Panel opens only when `System panel enabled` is on.
- Turn `System panel enabled` off and confirm the dock system control shows `SYSTEM PANEL DISABLED` with `Enable it in Settings to access the AXIOM system control panel.`
- Swipe left from Home and confirm AXIOM Feed opens when enabled.
- Swipe right from AXIOM Feed and confirm Home returns.
- Press Back from AXIOM Feed and confirm Home returns.

## 2. VOID.SYS

- Confirm AXIOM native icons exist without standalone VOID.SYS installed.
- Confirm the APK contains the expanded native VOID.SYS icon map and matte drawable assets.
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

## 4. 7-Slot Dock

- Confirm dock shows 7 buttons: Phone, Left Folder, Settings (power-dial icon), App Drawer (grid dots, focal dot below), System (ring indicator), Right Folder, Messages (chat bubble).
- Tap Phone and confirm dialer opens.
- Tap Messages and confirm messaging app opens (messaging category / `smsto:` URI / known package fallback).
- Long-press Settings and confirm Axiom Shooter transition launches.
- Tap System and confirm the AXIOM System Panel opens when enabled, or `SYSTEM PANEL DISABLED` appears when disabled.
- Long-press System and confirm Android Phone Settings opens.
- Tap Left Folder and Right Folder and confirm custom folder panels open.
- Confirm compact mode reduces button size (36dp) and spacing (4dp).

## 5. Widgets

- Set Home Panels to 2+ in Settings.
- Swipe to a widget panel and confirm "WIDGET PANEL / Long press to add widget." text.
- Long-press the widget panel and confirm `WidgetPickerScreen` opens showing "PANEL 2" (or correct panel number).
- Confirm the picker lists installed widget providers sorted alphabetically.
- Select a widget. Confirm AXIOM shows `WIDGET PERMISSION REQUIRED` when Android requires host approval, then confirm the widget appears on the correct panel after bind/configuration.
- Long-press the bound widget and confirm the picker re-opens for replacement.
- If device denies bind permission, confirm the system bind dialog appears and the widget is saved on OK or discarded on Cancel. If the OEM blocks direct bind requests, confirm `WIDGET UNAVAILABLE`.

## 6. Permissions & Setup

- Open Settings > Setup > Permissions & Setup.
- Confirm Default Launcher row shows ACTIVE when AXIOM is default, NOT SET otherwise.
- Tap NOT SET and confirm Android home/default-apps settings opens.
- Confirm Widget Binding row shows AVAILABLE and explains the long-press picker plus Android approval flow.
- Confirm VOID.SYS section shows Native Icons: INCLUDED and Standalone: INSTALLED / OPTIONAL.
- Press Back and confirm return to Settings.

## 7. About / Contact Routing

- Open Settings > About > About AXIOM.
- Confirm screen shows Version, Product, VOID.SYS, Axiom Shooter, License, and two GitHub rows. No Email row.
- Open Settings > About > Contact.
- Confirm separate ContactScreen with Email (OPEN), Outer-Void GitHub (OPEN), Justadudeinspace GitHub (OPEN).
- Tap Email and confirm mail client opens with theoutervoid@outlook.com pre-filled.
- Tap GitHub links and confirm browser opens correct GitHub pages.

## 8. Locked Behavior

- Confirm debug bypass allows development testing (no LockedScreen flash on launch).
- Document that release builds intentionally fail closed until real Play Licensing, Play Integrity, and server signatures exist.

## 9. AXIOM Feed

- Open Settings > AXIOM FEED.
- Toggle Enable AXIOM Feed off and confirm left-swipe no longer opens the feed.
- Toggle Enable AXIOM Feed on and confirm left-swipe opens `AXIOM FEED / CONTROL LAYER`.
- Open Configure Feed and confirm Active Feed Layout appears.
- Use Up / Down controls and confirm module order changes.
- Toggle a module row and confirm it enables/disables.
- Collapse a module and confirm the feed shows only the compact header row.
- Remove a module, open Add Module, and re-add it from Module Library.
- Reset Feed Layout and confirm defaults return: System Status, Weather, Calendar, Focus Panel, Quick Actions.
- Enable Minimal Feed Mode and confirm the feed contains Minimal Clock, System Dot Indicator, and Focus Only Panel.
- Confirm functional modules persist local state: Focus priorities, Task List, Next Action, and Notes.
- Confirm setup-required modules show clear permission/setup text rather than fake data.
- Confirm planned modules show `PLANNED` or `PLANNED / V1.1`.
- Confirm the feed does not show algorithmic content, recommendations, ads, trending items, or infinite-scroll news behavior.
