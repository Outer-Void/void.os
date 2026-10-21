# AXIOM v1.0 Release Checklist

> This checklist must be manually verified on device before v1.0 tag is created.

---

## Build & Code

- [ ] `./gradlew clean assembleRelease` completes without error
- [ ] APK size is within expected range
- [ ] ProGuard/R8 release build does not break launcher functionality
- [ ] No debug-only code paths active in release build
- [ ] `BuildConfig.DEBUG` guards remain correct

---

## Security (MUST COMPLETE BEFORE PAID RELEASE)

- [ ] **Google Play Licensing** — implement in `EntitlementManager.kt` (currently stubbed with TODO)
- [ ] **Google Play Integrity API** — implement in `IntegrityGate.kt` (currently stubbed with TODO)
- [ ] **Server signature verification** — implement in `SignatureVerifier.kt` (currently stubbed with TODO)
- [ ] Signature gate tested with real release keystore
- [ ] `LicenseGate` and `SignatureGate` tested with unlocked AND locked state on device
- [ ] Debug bypass (`BuildConfig.DEBUG`) confirmed absent in release APK

---

## Device Testing (Functional)

- [ ] AXIOM set as default launcher — Home button routes correctly
- [ ] Home screen loads with favorites panel
- [ ] Empty favorite slots show correctly — tap to add via App Drawer
- [ ] App Drawer opens, search works, apps launch
- [ ] Settings screen opens all sections
- [ ] Feed enabled — right swipe shows AXIOM Feed
- [ ] Feed modules: System Status, Clock Panel, Focus Panel, Task List, Notes — render correctly
- [ ] Weather module — shows SETUP REQUIRED, setup button opens correct settings
- [ ] Calendar module — shows SETUP REQUIRED, button opens calendar permission request
- [ ] Metrics module — shows SETUP REQUIRED, opens Usage Access settings
- [ ] AXIOM Shooter Easter egg — only Home wordmark long-press launches; Settings and dock Settings expose no launch route
- [ ] Shooter: Start screen shows CAMPAIGN / SURVIVAL / TRAINING / SETTINGS / EXIT TO AXIOM
- [ ] Shooter Campaign: 3 missions visible, M-01 playable, completion unlocks M-02
- [ ] Shooter Survival: endless mode starts
- [ ] Shooter Training: no enemies, ship moves and fires
- [ ] Shooter Settings: audio toggle, sensitivity, HUD selector, SHIP button → Ship screen
- [ ] Shooter Ship: AXIOM-01 stats displayed with animated bars
- [ ] Shooter EXIT TO AXIOM: returns to launcher correctly
- [ ] Terminal Panel: Settings → Developer → Terminal Panel opens embedded shell
- [ ] Terminal Panel: shell commands execute and output displays
- [ ] Terminal Panel: if shell fails, Termux bridge offered (if Termux installed)
- [ ] System Panel: opens from dock long-press, shows all action tiles
- [ ] System Panel actions: Wi-Fi, Bluetooth, Flashlight, Battery, all settings tiles open correct screens
- [ ] Lock screen wallpaper: installs correctly on test device (not blank)
- [ ] Left / Right dock folders: populated from Settings → Folders
- [ ] Widget panels (2–5): long-press adds real Android widget
- [ ] Permissions & Setup screen: all rows show correct status after granting/denying permissions
- [ ] Icon shape: changing shape visibly applies to Home, Drawer, Folders
- [ ] Font: changing font applies to all screens
- [ ] Accent color: changing color applies throughout
- [ ] About / Contact screens open and navigate correctly

---

## Permissions

- [ ] POST_NOTIFICATIONS request shows on Android 13+
- [ ] READ_CALENDAR request shows when user taps calendar permission row
- [ ] READ_CONTACTS request shows when user taps contacts row
- [ ] Location permission request shows when user taps location row
- [ ] Usage Access settings opens when user taps usage stats row
- [ ] Notification Listener settings opens when user taps notification listener row
- [ ] No permission is silently granted or silently skipped

---

## Icons / VOID.SYS

- [ ] VOID.SYS bundled icons render for common apps (Google, Samsung, etc.)
- [ ] Fallback icon provider renders stylized fallback for unknown apps
- [ ] System icon mode renders raw Android icons when selected

---

## Release Blockers (must not ship without these)

1. **Play Licensing** — not implemented; paid app will not verify purchase without this
2. **Play Integrity API** — not implemented; integrity gate is stub
3. **Server signature verification** — not implemented; signature gate is stub
4. All items above marked [ ] must be ✅ before git tag v1.0 is created

---

## To Create the Release Tag

After all items above are ✅:

```bash
git tag -s v1.0 -m "AXIOM Launcher v1.0"
git push origin v1.0
```

Only create the tag after explicit device test sign-off.
