# AXIOM Product Completion Plan

## Audit basis and completion rule

This audit was performed against `feat/release-visual-overhaul` at `0a5a213` before this task's edits. The working tree was clean at inspection. It covers launcher source, resource maps and drawables, Shooter source, the manifest, Gradle configuration, tests, and product/device documentation. The Settings access-policy change below is also part of this task.

Compilation and unit tests establish only that code builds and selected rules pass. They do not establish interactive behavior, visual quality, accessibility, installed-package discovery, performance, or release readiness. This document is a plan, not a release sign-off.

## P0 — Release blockers

1. **Paid entitlement and integrity are not implemented.** `security/IntegrityGate.kt` returns `false` in release, so `security/EntitlementManager.kt` currently returns `IntegrityFailed`. Its later TODO path would return `Unlicensed` even if integrity passed. `security/SignatureVerifier.kt` always returns `false` after its field checks. Release users therefore fail closed. Connect real Play Licensing/Billing entitlement, Play Integrity, and server-side entitlement/signature services before paid release. Test expiry, replay, offline behavior, restoration, and account recovery; keep gates closed until then.
2. **The Shooter launch token is not a cryptographic signature.** `launch/SignedGameLaunchManager.kt` computes an unkeyed SHA-256 value. `voidgame/VoidGameActivity.kt` checks package names and expiry but does not validate the signature, issued time, nonce, or entitlement extras. The activity is non-exported in `AndroidManifest.xml`, limiting external launch, but this token is not signed entitlement. Replace it with a versioned, server-verifiable contract and replay policy.
3. **No production signing setup exists.** Add protected release signing through CI/managed keystore access; document rotation and recovery; prove the certificate matches server and Play configuration. Never commit signing material.
4. **No current API 36 build has been demonstrated in this checkout.** `app/build.gradle.kts` targets API 36 with AGP 8.10.0 and the wrapper uses Gradle 8.11.1. The prior release review describes an API 36 license failure, but this turn's `ANDROID_HOME=/opt/android-sdk` does not exist. Re-run at API 36 without changing compile/target SDKs or downgrading AGP/Gradle.
5. **Play policy and disclosure review is outstanding.** The app queries installed launcher activities as a core feature and requests capabilities for optional integrations. Complete declarations and privacy/data-safety review against actual release behavior.
6. **Hardware and large-screen verification is outstanding.** Both activities are portrait-locked. Verify target API behavior, Android 16 large screens, foldables, cutouts, system bars, widget hosts, input, and OEM wallpaper restrictions.

## A. Shooter access policy

### Current behavior and change

- `ui/components/AxiomWordmark.kt` uses a long press; `ui/screens/HomeScreen.kt` wires it to the transition. A normal tap is a no-op.
- `SettingsScreen.kt` exposed a Shooter Modules row and `MainActivity.kt` connected it to `LauncherState.AxiomShooterTransition`.
- `HomeScreen.kt` also connected long-pressing the Settings dock button to the game. The About screen described the egg. Those Settings-area paths have been removed; only the Home wordmark callback remains.
- The transition is now gated by `ui/state/ShooterAccessPolicy.kt`. `ShooterAccessPolicyTest` checks Home and every other launcher state. This proves a state policy, not Compose pointer delivery.

### Acceptance and test strategy

- Home wordmark long press and its labelled accessibility long-click action start one transition; a tap does not. No Settings row, nested menu, or dock gesture launches the game.
- App drawer/search, feed, widgets, favorites, system panel, dock taps, Back, and Home swipes retain their behavior when gestures start at or near the wordmark.
- TalkBack users must not encounter a dead or misleading wordmark action. Test action semantics and focus order without adding a visible Shooter menu entry.
- Keep the internal activity packaged and `exported=false`; direct external activity launch must be rejected. Paid/release entitlement remains fail-closed pending P0 work.
- `ShooterAccessPolicyTest` covers state. Add Compose/instrumentation coverage when a Compose test runner is configured: Settings tree contains no Shooter label/action; the Home wordmark has the intended semantics; Settings dock has no Shooter long-click action.
- Hardware checks: tap, short press, long press, TalkBack actions, touch then swipe in each direction, normal Settings tap/hold, 320dp width, gesture navigation and three-button navigation. These remain outstanding.

## B. VOID.SYS icon design and completeness audit

### Resource inventory

Counts were computed from this checkout's `voidsys_icon_map.json`, `voidsys_generic_drawables.json`, and drawable resource filenames.

| Measure | Observed |
| --- | ---: |
| JSON map entries | 1,908 |
| Package-only keys | 588 |
| Activity/slash keys | 660 |
| `ComponentInfo{...}` keys | 660 |
| Unique mapped drawable names | 547 |
| Map targets missing from drawable resources | 0 |
| Unique drawable names | 583 |
| Drawable names not referenced by map | 36 |
| Mapped names listed as generic | 453 |
| Names in generic asset list | 468 (15 are not map targets) |
| Direct mapped names in `values/voidsys_keep.xml` | 94 |
| `drawable-xxxhdpi` PNG names / distinct byte hashes | 578 / 124 |

The 1,361 repeated references mostly alias package/activity keys to the same drawable; they are not 1,361 duplicate artworks. Separately, 454 of 578 `xxxhdpi` names share bytes with another name, indicating category-level reused images. There are 36 unreferenced drawable names; these include dock/system assets and possible legacy icons, so establish references and intent before deleting anything. No map target currently dangles.

### Rendering and visual findings

- `icon/BundledVoidSysIconProvider.kt` tries the bundled map first. The 453 targets listed as generic are rendered as generated AXIOM category glyphs/monograms rather than those PNGs. Direct mapped assets are looked up by resource name. `values/voidsys_keep.xml` retains the 94 direct targets for resource shrinking. If bundled lookup fails, the provider tries the selected external VOID.SYS package, then AXIOM fallback.
- `icon/VoidSysIconProvider.kt` loads `icon_map.json` and drawable resources from `com.outervoid.voidsys` through a package context. This is a custom map/resource contract, not evidence that arbitrary third-party icon-pack protocols work. System mode returns installed app icons; AXIOM fallback mode generates icons.
- `LauncherViewModel.queryAppInventory()` queries installed `MAIN`/`LAUNCHER` activities and supplies their launcher icons as fallback. Settings offers VOID.SYS, AXIOM fallback, and system icons. The manifest limits package queries to VOID.SYS and Termux plus launcher intent discovery. Package visibility and refresh behavior still need device checks.
- Observed bundled art uses dark rounded tiles, thin gray framing, light line glyphs, and restrained amber details. Representative Spotify waveform and Acrobat document glyphs follow this design. Thin interior detail and low luminance can disappear at launcher scale. Generated fallbacks share the tile/glyph language, but substring-based category classification can misclassify brands; unknowns use package-derived monograms.
- Recognition is the main completeness gap: 94 unique mapped targets render dedicated direct art, 453 targets redirect to generated category/monogram glyphs, and 578 `xxxhdpi` files contain only 124 distinct images. The generic list describes an intentional fallback set but does not show that mapped apps are recognizable.
- This is source/resource accounting plus spot visual review, not a complete contact-sheet or runtime review. No physical-device screenshot set, installed icon-pack comparison, 48dp/60dp runtime rendering, or small-size legibility review was available. Shape, padding, line weight, clipping, contrast, and recognition need systematic evaluation.

### Prioritized icon redesign/coverage plan

1. **P1 — Establish the truth set.** Produce package/activity → display label → target → direct/generic → file/hash → observed-app inventory. Group aliases without discarding them. Account for the 36 unused resources and 15 generic-list-only names. Acceptance: every map entry is classified as alias, intentional generic fallback, dedicated icon, or obsolete candidate; zero missing targets; no deletion until source references, keep rules, and intent are reviewed.
2. **P1 — Prioritize recognition.** Use a real app corpus of at least 100 launcher activities across Google, Samsung, Microsoft, finance, messaging, media, navigation, utilities, and regional apps. Acceptance: top 100 each have recognizable dedicated art or an explicitly approved fallback; report dedicated/generic/unknown rates by category and include multi-activity apps.
3. **P2 — Define visual tokens.** Preserve graphite/black and AXIOM amber, constrain amber to meaningful accents, and standardize optical bounds, padding, radius, stroke hierarchy, and glyph scale. Acceptance: 48dp and 60dp sheets show no clipping or illegible microdetail; small text contrast ≥4.5:1 and large text / essential glyph contrast ≥3:1; labels remain readable independently of icons.
4. **P2 — Specify external pack behavior.** Version the VOID.SYS JSON/resource contract. Test missing package, malformed map, missing drawable, alias precedence, package update/removal, mode switching, R8 and cache refresh. Acceptance: each mode has deterministic precedence, every activity resolves without crash, pack updates refresh icons, system mode uses the installed launcher icon, and no general third-party compatibility is claimed without tests.
5. **P3 — Validate runtime presentation.** Capture Home, drawer, favorites, folders and search at 320dp, 360–412dp, large font, compact mode, icon shapes and scales on two device families. Acceptance: stable layout and hit targets, measured cache memory, and ≥95% correct identification of the top-100 corpus in a blinded review.

## C. AXIOM Shooter audit

Existing `ShooterRulesTest.kt` covers frame clamping, rectangle overlap, top-score selection, mission threshold, and charge arithmetic only. “Source present” below does not mean a feature passed a play session.

| Area | Source evidence | Status / gap |
| --- | --- | --- |
| Start menu | `screens/StartScreen.kt`: campaign, survival, training, settings, local leaderboard, exit; geometric ship preview | Source exists; stale `v1.0` label conflicts with app `2.0.0-dev`; menu touch layout/hierarchy unverified. |
| Campaign | `CampaignScreen.kt`, `GameScreen.kt`: three mission definitions, preference completion count, sequential unlock, wave threshold | Source path exists; no play test. M-01 says “void cores”, but its three waves do not reach the every-fifth-wave core. |
| Survival | `StartScreen.kt` passes `survival = true` | **Defect:** `GameScreen.survival` is never read; same loop as a default run. |
| Training | `TrainingScreen.kt`: carrier/bullets, no enemies, shot count | **Mismatch:** `Carrier.tryFire()` runs during update; displayed shot count increments only on `justTouched`. Instruction and behavior disagree. |
| Movement/fire | `GameScreen.kt`, `entities/Carrier.kt` | Ship tracks touched x only above y=76; HUD marks MOVE ring at y=54. `tryFire()` runs every active frame, so fire is automatic and the FIRE region is not the trigger. Reconcile controls, labels, sensitivity and alternatives. |
| Missile/bomb | `GameScreen.kt`, `ShooterRules.kt` | Missile immediately damages nearest-by-Manhattan target by 2, without projectile/targeting animation. Bomb clears fragments and damages core; it spends a charge even without a target. Balance/feedback unverified. |
| Pause/resume | Inline `GameScreen` overlay; `pause()` sets paused; activity registers Android 13+ Back callback | Source path exists; process recreation, Back dispatch, background/foreground and input reset unverified on hardware. Deprecated duplicate `PauseScreen.kt` remains. |
| Restart/exit | `GameScreen.kt`, `ScoreScreen.kt`, `VoidGame.kt` | **Defect:** pause restart creates default `GameScreen(game)`, losing campaign/survival mode. Score “START AGAIN” also creates default mode. Test exact intended semantics. |
| Scoring | `ScoreScreen.kt`, `ShooterRules.kt`: local top-five preferences | Source persists scores; corruption, tie ordering, mode separation and reset/privacy behavior untested. |
| Audio | `ShooterSettingsScreen.kt` stores `audio` | **Incomplete:** no audio assets/playback use found; toggle has no game effect. |
| Sensitivity/HUD | Settings stores `sensitivity` and `hud_style` | **Incomplete:** neither value is read by gameplay/rendering; HUD is fixed. |
| Ship screen | `ShipScreen.kt`: hard-coded AXIOM-01 bars and Carrier line art | **Placeholder/static data:** five fixed percentages, no selection or stats linkage; only hull bar/thruster pulse animate. |
| Art/animation | `ShooterVisuals.kt`, entity drawing via ShapeRenderer/BitmapFont | Custom geometric outlines, stars/grid, warning pulses, hit shake, particles, engine flicker and core-purge effect exist. No sprite-sheet/texture/audio asset pipeline was found. Effects are procedural and limited; stale version and stock bitmap font need art review. |
| Resource lifecycle | `VoidGame.setScreen()` disposes old screen; each screen disposes renderers/batches/fonts | Good source ownership pattern; transitions, context loss and repeated loops still need device profiling. |

### Premium direction

Use an **AXIOM tactical arcade** language: near-black space and restrained graphite grid; bright, distinct player/hostile silhouettes; amber for interaction/identity and red for immediate danger. Keep combat objects brighter and simpler than borders. Give hostile types different silhouettes and motion as well as colors. Distinguish core, incoming fire, missile, bomb, and status effects. Replace placeholder ship/menu art with authored vector/sprite assets under measured performance budgets. Design thumb controls around safe areas and actual device size without letting decoration compete with play.

### Phased Shooter plan and acceptance

1. **P1 — Correct rules.** Add deterministic tests for distinct Survival behavior, wave advancement, campaign unlock/completion, mode-preserving restart, score rules, no-target bomb use, and settings effects. Acceptance: each menu mode has distinct documented behavior; restart/exit preserve intended mode and progress; no dead settings remain.
2. **P1 — Repair input/HUD contract.** Define movement, auto/manual fire, pause, missile and bomb regions from one layout model. Acceptance: measure at 320dp and 360dp, ensure ≥48dp targets or reviewed accessible alternatives, no overlap, labels match behavior, and one-handed operation is reviewed by five testers or documented internal playtest.
3. **P1 — Lifecycle regression.** Test Home → egg → game → pause → resume/restart/exit, background/foreground, recreation, GL disposal and launcher return on API 26, 33/34, 36. Acceptance: 20 repeated transitions per target class without black screen, duplicate activity, lost mode/progression, stuck input, crash or leaked GL resource.
4. **P2 — Balance core loop.** Instrument wave duration, deaths, hit rate, drops and weapon usage. Acceptance: agree on duration/death ranges; ten fresh campaign runs can unlock all missions without exploits; Survival runs 30 minutes with bounded entities and stable frame time.
5. **P2 — Complete screens and media.** Ship final menu, ship, enemy, weapon and effect art plus optional sound, or remove unsupported settings. Acceptance: art contact-sheet sign-off; no fake audio/sensitivity/HUD controls; enabled settings measurably affect play; motion respects reduced-motion/performance needs.
6. **P3 — Profile/accessibility.** Test named small/typical/large devices and TalkBack. Acceptance: 60fps target on baseline hardware at 95th percentile ≤16.7ms, bounded memory/battery in a 30-minute run, no safe-area clipping, and documented manual accessibility review.

## D. Launcher product-quality audit

### Defects and release decisions

- **Paid release is locked by code** (P0). Do not relax fail-closed gates to make a demo appear usable.
- **Settings-related exposure included a Modules launch row, a Settings dock long-press shortcut, and an About gesture hint.** This task removes them; confirm no ordinary Settings sub-screen exposes Shooter.
- **Feed return gesture is per-event, not cumulative.** `ui/screens/AxiomFeedScreen.kt` navigates Home only when one `detectHorizontalDragGestures` callback reports `< -28px`; slow drags may never satisfy it. Replace with touch-slop/cumulative policy; test speed, cancellation, nested scroll and direction.
- **Dock visuals are 36dp compact / 40dp normal.** Measure actual interactive bounds after Compose minimum-touch-target behavior; design an accessible non-overlapping strategy at 320dp.
- **Home favorites always use five columns**, while `gridColumns` configures the drawer. Clarify scope in settings copy or change only if product intent requires it; test 5–25 items, empty slots, compact mode, labels and small width.
- **App cells/wordmark need accessibility verification.** `AppIconCell.kt` uses a combined-click parent and labeled image; `AxiomWordmark.kt` has an empty tap plus hidden long press. Verify semantics merge, focus order, announcement, targets and TalkBack action.
- **Existing build/lint evidence is stale until rerun.** `release-hardening-task-005-006.md` records another SDK/toolchain state. Replace evidence only after API 36 validation.

### Source-reviewed flows and required evidence

| Area | Source | Required end-to-end evidence |
| --- | --- | --- |
| Home/navigation | `MainActivity.kt`, `HomeScreen.kt`, swipe/navigation policies | Back from every state; slow/diagonal/edge swipes; favorites/widgets; compact mode, safe insets, cutouts; egg gesture arbitration. |
| Drawer/search | `SearchScreen.kt`, `LauncherViewModel.kt` | Zero/one/many results, IME/Enter, duplicate activities, inventory loading/error/stale, package changes, work profile, large labels/font, TalkBack. |
| Dock/folders | `DockBar.kt`, `CustomFolderPanel.kt`, `FolderAppSelectionScreen.kt` | Every action; empty/large folders; 7 target hit areas; Settings hold and System long press. |
| Settings | `SettingsScreen.kt`, `settings/SettingsRepository.kt`, MainActivity callbacks | Persistence across process restart, correct application of each option, reset and value migration. |
| Icons | `BundledVoidSysIconProvider.kt`, `VoidSysIconProvider.kt`, `AxiomFallbackIconProvider.kt`, `AppIconCell.kt` | Section B pack update/removal, R8, actual package corpus and visual acceptance. |
| Wallpaper | `wallpaper/AxiomWallpaperManager.kt`, Settings and MainActivity result flow | Home/lock/both across APIs/OEMs; crop, colors, lock state, system-picker fallback, restrictions. |
| Widgets | `WidgetPickerScreen.kt`, `AxiomWidgetHost`, `WidgetOperationPolicy`, activity-result flow | Provider changes, bind/configure/cancel, process death, replacement, abandoned ID reclamation, sizing and OEM host compatibility. |
| System Panel | `SystemPanelScreen.kt`, `SystemActionController`, `SystemSettingsNavigator` | Every tile state/action. Verify truthful settings handoff/unavailable behavior on API/OEM matrix. |
| Feed | `AxiomFeedScreen.kt`, feed renderers/repositories | Module add/remove/reorder/collapse/persistence; setup-required permissions; nested scroll and slow return; restart data retention. |
| Package discovery | `LauncherViewModel.queryAppInventory()`, manifest `<queries>` | Complete launchable inventory under package visibility, install/update/remove, aliases, work profile/clones, stale state and Play declaration. |

### Optional enhancements

- Add onboarding or a gesture hint only if it does not create a normal Shooter menu entry.
- Consider configurable Home favorite columns, per-icon shape exceptions, category search, refined feed editing, larger-screen layouts or cloud backup after product/privacy requirements are explicit.
- Consider shared Shooter module architecture only after access and gameplay contracts are stable; avoid a broad source migration during release hardening.

## E. Phases and release gates

### Phase 0 — Security and reproducible evidence

- Keep gates closed; implement actual entitlement, integrity, signatures and protected release signing.
- Provision licensed API 36 SDK in CI and reproducible Termux AAPT2 override. Record Java, Gradle, AGP, SDK and AAPT2. Do not lower SDK or downgrade toolchain to mask an environment failure.
- Add CI release assemble, unit tests, lint, resource-shrink verification and instrumentation compilation. Acceptance: clean repeatable API 36 build with archived logs, artifact checksum and no unexplained new warnings.

### Phase 1 — Correctness and coverage

- Complete P1 access, Feed navigation, icon truth-set, and Shooter rule/input work.
- Expand policy/persistence tests; add Compose UI tests for Settings absence, semantics, interactions and responsiveness.
- Acceptance: each P1 item passes automation where feasible and has hardware evidence where not.

### Phase 2 — Visual and interaction sign-off

- Deliver icon and Shooter visual systems; inspect every launcher screen/text at supported display/font sizes.
- Acceptance: annotated screenshots for Home, Feed, Drawer/Search, Settings, System Panel, widgets, folders, wallpaper handoff, Shooter start/settings/game/pause/score/ship; no clipping, truncation, low-contrast controls, dead settings or unexplained empty placeholders.

### Phase 3 — Paid release

- Complete security, keys, Play declarations, privacy/store metadata, diagnostics/recovery, upgrade/install/uninstall tests and device matrix.
- Acceptance: security review signed off; Play internal track purchase/restore/expired/offline states verified; API 26, 33, 34, 36, two OEMs and a foldable/large-screen target signed off; product owner approves screenshots/checklist. No tag or publish before blockers close.

## Validation matrix

| Layer | Checks | Evidence |
| --- | --- | --- |
| Pure rules | Swipe/access policies, icon map parsing/coverage, settings decoding, widget lifecycle, game progression/scoring/combat/modes | JUnit report and code review |
| Android build | `./gradlew -Pandroid.aapt2FromMavenOverride=/data/data/com.termux/files/usr/bin/aapt2 :app:assembleDebug :app:testDebugUnitTest :app:lintDebug`; release assemble after signing setup | Exact command/toolchain/SDK license/outcome; no downgrade |
| Instrumentation | Compose semantics/interactions, widget permission/configuration, Home gestures, package/icon behavior | CI/emulator report and physical-device records |
| Manual product | Full launcher, wallpaper/widgets/system panel, paid states, game modes/lifecycle | Versioned checklist with device/API/model, recordings/logs |
| Visual | Runtime icon sheets and screenshots; Shooter action readability | Annotated artifacts with named reviewers |
| Security/release | Entitlement, Integrity, signature, cert, replay/expiry/offline/restore | Security test report and Play purchase matrix |

## Remaining work

- Compose and device verification of hidden Shooter activation, Settings absence, pointer arbitration and TalkBack.
- Current API 36 build/test/lint. This environment has the Termux AAPT2 executable but no Android SDK at its configured path.
- Full runtime/package/visual audit and prioritized redesign/launcher fixes.
- Real entitlement, Integrity, server signature verification, production signing, Play declarations and hardware/large-screen sign-off.
- Shooter mode/input/settings fixes, art/audio, balance, progression and device test suite.
