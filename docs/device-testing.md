# Device Testing Matrix

## 1. Launcher

- Install the AXIOM debug APK.
- Set AXIOM as the default home launcher.
- Press Home and confirm AXIOM opens.
- Verify the app grid loads.
- Verify Search opens and filters apps.
- Verify Settings opens.
- Verify System Panel opens only when `System panel enabled` is on.
- Turn `System panel enabled` off and confirm the dock system control shows `SYSTEM PANEL DISABLED` instead of opening the panel.

## 2. VOID.SYS

- Install the VOID.SYS APK.
- Verify Settings shows VOID.SYS installed.
- Verify AXIOM attempts VOID.SYS icon lookup.
- Uninstall VOID.SYS while AXIOM is running.
- Verify Settings updates live and app icons fall back to system icons.

## 3. VOID.GAME

- Install the VOID.GAME APK.
- Verify Settings shows AXIOM Shooter installed.
- Long press the AXIOM wordmark.
- Verify the pulse transition appears.
- Verify the game opens.
- Press Back in game and confirm pause behavior.
- Exit and verify control returns to AXIOM.

## 4. Locked Behavior

- Debug build should allow `DebugBypass`.
- Release build should fail closed until licensing, Play Integrity, and product signature verification are implemented.

## 5. Failure Cases

- VOID.GAME missing: long press AXIOM wordmark should show the not-installed dialog.
- VOID.SYS missing: Settings should show not installed and launcher should use system icons.
- Missing or corrupted signature files: release should remain locked.
- Package install/uninstall while AXIOM is running should update module status live.
