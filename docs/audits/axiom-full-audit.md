# AXIOM Task 007 reconciliation

Baseline: 2026-10-21, branch `feat/release-visual-overhaul`, HEAD `9616f26`.
Remote tracking HEAD `0a5a213`; two existing local commits (`e12526b`, `9616f26`) are preserved. Initial working change: nullable signatures in `security/PackageSignatureVerifier.kt`, preserved separately from task commits. No archive was used. No reset or history rewrite.

## Confirmed baseline

- Prior P1/P2/P3 and 005/006 changes remain in history. `LauncherSwipePolicy.kt` accumulates motion, locks axis, respects consumed input and dispatches once. Feed still uses a per-event threshold (P1).
- `MainActivity.kt` saves navigation and pending widget pairs; Ambient Back policy exists. Home panel index itself is not saved (P2).
- `LauncherViewModel.kt` loads inventory on IO, checks cancellation, coalesces refresh and preserves inventory on failure. Receivers unregister; package visibility uses launcher queries, not QUERY_ALL_PACKAGES.
- `SystemMetricsStrip.kt` reads actual system metrics. AppIconCell converts off main and has a bounded LRU; two-line labels exist. Identity-keyed bitmap state can briefly show old art while replacement loads (P2).
- VOID.SYS bundled/external providers prefer package keys over activity overrides (P1). Generic artwork recognition remains a product gap.
- Shooter has existing procedural craft/effects. Manifest activity is non-exported; only Home wordmark exposes entry. API 26 minimum, compile/target 36, AGP 8.10.0, Gradle 8.11.1 remain.
- P1: VoidGame does not forward pause/resume. Immediate screen disposal during render lets old screens continue drawing disposed resources. Game restart loses mode, score inserts a run twice, patch lines draw inside Filled mode, HUD input is misleading. Shooter settings expose unused audio/sensitivity/HUD options.
- P0 release blockers: Integrity and product-signature verification explicitly return false; real licensing/backend and production signing are absent. These gates must remain closed.

## Environment

Environment SDK variables point at absent `/opt/android-sdk`. Existing SDK discovered under native Termux home, including API 36; use environment overrides only. Native Termux AAPT2 override stays on the command line. Device execution and visual acceptance remain separate from compilation.

This report is a source review and an incremental remediation record, not paid-release qualification. Detailed icon/game results and continuation gates are in adjacent reports and `docs/release/remaining-work.md`.

## Implemented remediation and evidence

| Severity | Finding / source | Completed change |
| --- | --- | --- |
| P1 | `MainActivity.kt:handleWidgetSelect` and bind/configure result handlers | Catch allocation/binding exceptions; reclaim IDs after failed configuration launch or missing provider; preserve the old panel widget until a real successful result. Setup opens a real picker and enables panel 2. |
| P1 | `wallpaper/AxiomWallpaperManager.kt` | Catch real wallpaper/preview I/O failures; recycle generated bitmaps after synchronous setBitmap, including failures. Wallpaper calls still run synchronously from Settings callbacks; moving decode/setBitmap/preview compression off main remains P1 performance work. |
| P1 | `PermissionSetupScreen.kt`, manifest, Feed renderer/type registry | Remove unused notification-posting/calendar/contacts/location/usage permissions and request UI. Mark unimplemented data integrations Planned; calendar is an actual system-calendar shortcut. Fix non-invoking notification-listener callback and describe its real media-session use. No saved Feed configs are deleted. |
| P1 | `VoidGame.kt`, `GameScreen.kt`, `VoidGameActivity.kt` | Defer disposal until render ends, forward lifecycle, handle predictive Back on GL thread, finish activity on Home return, enforce signature gate on release entry. Repair modes, scores, weapons, hit feedback, rendering pass and simulation. See Shooter QA. |
| P1/P2 | `icon/IconMappingKeys.kt`, AppIconCell | Prefer activity overrides and normalize relative components; reset obsolete bitmap state on refreshed Drawable; retain bounded caches and IO conversion. |
| P2 | `HomeScreen.kt`, `AxiomFeedScreen.kt`, SearchScreen | Save Home panel, key hosted AndroidView by widget ID, accumulate Feed drag and dispatch once, cap drawer columns by width/font size, display empty search states. |

Debug validation on the frozen implementation passed all four requested tasks: `:app:assembleDebug`, `:app:testDebugUnitTest`, `:app:lintDebug`, `:app:compileDebugAndroidTestKotlin`. 39 JVM tests, zero failures/errors. Lint: zero errors, **585 warnings** (485 unused resources, 71 duplicate icons, plus dependency/toolchain, orientation, dynamic resource lookup, backup/monochrome icon and Kotlin-extension suggestions). These warnings are not waived as release acceptance. Instrumentation compilation is not device execution.

Exact environment/command:

```sh
ANDROID_HOME=/data/data/com.termux/files/home/android-sdk \
ANDROID_SDK_ROOT=/data/data/com.termux/files/home/android-sdk \
./gradlew -Pandroid.aapt2FromMavenOverride=/data/data/com.termux/files/usr/bin/aapt2 \
  :app:assembleDebug :app:testDebugUnitTest :app:lintDebug :app:compileDebugAndroidTestKotlin --console=plain
```

Java 21 host, Java/Kotlin bytecode target 17; wrapper 8.11.1, AGP 8.10.0; API/build-tools 36. Original nullable-signature working change was included in local validation and then committed verbatim as `2fa558d`, separately from new task implementation, to make the pushed API 36 tree reproducible. No repository property hardcodes SDK/AAPT2 paths.

[Earlier debug task log](artifacts/debug-validation.txt); the [final combined task log](artifacts/final-validation.txt) supersedes it after the keep-file correction. Initial combined validation passed in 6m08s but overlapped early edits and is not final-tree evidence. An intermediate run exited 143; another failed on a callback type mismatch introduced in this task. The callback was corrected; frozen-tree validation passed in 3m59s. No failed/interrupted attempt is presented as a pass.

Icon QA and PNG decoder fixtures pass. [Native alignment audit](artifacts/native-alignment.json) deliberately fails: arm64 and x86_64 native libraries use 4096-byte ELF LOAD alignment. API level alone does not satisfy 16 KB support. APK ZIP alignment does not repair incompatible ELF segments.

Device testing blocked: SDK adb is an x86-64 ELF requiring `/lib64/ld-linux-x86-64.so.2` in this ARM64 PRoot; invocation fails (exit 127). No executable native adb/device connection is available. No install, instrumentation execution, screenshots, frame-time or OEM testing is claimed.

## Remaining risks from repository review

- P0: actual Play entitlement, server-verified Integrity and cryptographic product verification, production signing/restore/revocation/offline policy remain unimplemented and fail closed. Existing `SignedGameLaunchManager` hashes an unkeyed payload; its name is not evidence of cryptographic authorization. Activity is private and release gate remains closed.
- P0: native 16 KB compatibility must be repaired with supported upstream/rebuilt LibGDX binaries and verified on 16 KB hardware/emulator. Do not merely realign the ZIP.
- P1: source fonts/third-party artwork and LibGDX distribution need a complete provenance/license/notice bundle; the repository's proprietary LICENSE is not a substitute for dependency/font notices.
- P1: device matrix and visual/product acceptance are absent. Full commercial qualification remains incomplete despite successful automation.
- P1: `MainActivity.kt` Settings wallpaper callbacks still perform decoding, bitmap allocation, binder I/O and fallback preview compression on main. This batch fixes exception/bitmap ownership, not that scheduling defect.
- P2: AtAGlancePanel still uses per-event page swipes and polls media on main every 2.2 seconds. Main clock/media polling and entitlement manager scopes need lifecycle/performance follow-up. TerminalSession has concurrent StateFlow list writes, blocking process startup/send and incomplete stream ownership; test repeated kill/restart/output flooding before enabling it by default.
- P2: widget bind/config cancellation needs real provider/process-death tests; replacement persistence and old-ID deletion are not transactional. Orphan cleanup, provider update sizing and reset/panel-count reduction still need lifecycle design.
- P2: RAM strip measures app Java heap, not whole-device RAM; label/metric scope should be clarified. Large inventories retain source Drawables beyond the conversion LRU. Work profiles/cloned apps, locale/icon changes, notification access and OEM flashlight state need device review.
- P2: fixed Home favorite columns, dock hit areas, Home/Settings large-font layouts, IME/insets, TalkBack semantics and adaptive icon clipping remain unqualified. No claim that every screen meets accessibility or visual acceptance is made.

Source review covered Git/history, build/manifest/resources/mapping inventories, all game entities/screens and high-risk launcher navigation/widget/inventory/security/settings/system/wallpaper/feed paths, with a repository-wide lifecycle/intent/placeholder scan. Manual execution and exhaustive line-by-line UI certification remain outside the available evidence.

## Focused commit record

- `b358490` — baseline/reconciliation, before implementation.
- `c7f1c40` — widget failure recovery, wallpaper I/O/bitmap ownership, truthful permissions and Feed availability, Home/Feed state.
- `019c482` — icon QA tooling, shared component-first resolution and refreshed bitmap state.
- `83b7540` — four dedicated vector glyphs, mappings and keep rules.
- `0f7e3e5` — Shooter lifecycle, fixed steps, controls, projectile, mode/score recovery and regression tests.
- `6fdf945` — actual ship rules, working reduced-effects setting and corrected menu/campaign presentation.
- `740217a` — responsive drawer/empty states and layout regression.
- `d24497a` — API 36/instrumentation-compilation CI and native alignment diagnostic.
- `2fa558d` — pre-existing nullable certificate fix preserved verbatim in its own commit.
- `9715237` — relocate icon keep metadata to raw resources so the resource shrinker applies it.
- `9d25e57` — verify actual packaged resource names in QA and CI.

The final documentation commit archives evidence and continuation requirements. Earlier P1/P2/P3/005/006 and the two initial local commits remain ancestors. Remote branch was inspected at `0a5a213`; publication uses a normal fast-forward push to preserve history, following the explicit no-force-push reconciliation rule.

## Runtime review limits

- Existing `FeedModuleLibraryScreen.kt` correctly disables Planned rows, but its v1/v2 roadmap copy is stale against the current development version. This remains a copy/polish item, not a functional integration.
- `AppIconBitmapCache` and generated-icon caches are bounded; this does not bound every original platform Drawable in the inventory or all Android resource caches.
- `ShooterClock` bounds simulation catch-up; no measured frame-rate claim follows from that change. Hostile/bullet hit detection remains discrete, not swept continuous collision.
- The release APK may compile with R8 while intentionally remaining locked and unsigned. It must not be offered as a functional paid product under those conditions.

## Post-shrink defect caught during qualification

P1, `res/values/voidsys_keep.xml`: source-only keep-list validation passed but the first actual shrunk APK removed 58 of 98 direct mapped resources. Root tools attributes in the values keep file were not applied as raw shrinker metadata. Moved the keep file to `res/raw/voidsys_keep.xml` without changing the retained target list; extended QA/CI to check the actual unsigned APK resource table. The first successful release compile therefore did **not** qualify icon retention. A negative resource-dump regression is archived separately; final validation results supersede it.

## Final qualification result

After the keep-file fix, the combined five-task Gradle run passed in 7m15s: debug assemble, all 39 JVM tests (zero failures/errors), debug lint (585 warnings, zero errors), instrumentation Kotlin compilation, and unsigned release assembly with R8/resource shrinking. [Final log](artifacts/final-validation.txt), [checksums/test summary](artifacts/validation-summary.json). Actual `aapt2 dump resources` inspection confirms **98/98 direct mapped icon names retained**. The enhanced icon checker passes that APK and deliberately fails the previous one (58 missing names). Native alignment diagnostic still exits 1 for both debug and release; that defect is not fixed by resource retention.

Final APK sizes: debug 58,604,984 bytes; unsigned/minified release 36,716,372 bytes. Release artifact is unsigned and functionality remains fail-closed. No signing secrets were added; tracked-key filename/private-key-header scan found no matching files, which is not a substitute for a full security/provenance review.
