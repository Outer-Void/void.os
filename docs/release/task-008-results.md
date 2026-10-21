# Task 008 implementation results

Branch: `feat/task-008-stabilization`. Baseline: `d5bfb4a` on `feat/release-visual-overhaul`. Initial tree clean; no user modifications, prior commits or generated outputs were replaced. No push performed. Live API 36/AGP 8.10.0/Gradle 8.11.1, existing P1–P3 fixes, package queries, non-exported game, Home-only policy and raw shrinker keep metadata verified. The older ZIP was not used. Existing Task 007 audits were reused and extended.

## Severity-ranked findings and outcomes

| Priority | Confirmed issue | Result / source |
| --- | --- | --- |
| P0 | Paid entitlement/backend Integrity/canonical signature contract incomplete | Release continues to fail closed. No credentials, signing or verification bypasses added. |
| P0 | LibGDX 1.12.1 source native libraries use 4 KB LOAD alignment | Rechecked with existing native audit; still open. Dependency/native migration and 16 KB device proof required. |
| P1 | All unmapped apps in each category shared the same icon | `icon/AxiomFallbackIconProvider.kt`: category plus identity monogram; skip generic package suffixes. Same-initial collisions still possible. |
| P1 | Enemies below carrier could dive upward and remain outside playfield | `voidgame/entities/Fragment.kt`: acquire only above ship inside combat region; regression tests. |
| P1 | Pause retained stale input/feedback; secondary thumb lacked takeover | `voidgame/ShooterInput.kt`, `screens/GameScreen.kt`: tested pointer ownership, release quarantine and takeover. |
| P1 | Game exit relied on generic backend exit instead of explicit launcher return | `VoidGame.kt`, Game/Score screens: route exit through existing main-thread Home/finish callback. |
| P1 | Settings wallpaper decode/install/compression blocked UI | `MainActivity.kt`: serialized IO work, main-thread intents/dialogs. Physical responsiveness/OEM cancellation still unverified. |
| P2 | Resource QA missed qualifier collisions, malformed component keys and re-encoded identical PNGs | `app/tools/voidsys_qa.py`: expanded checks and human/machine reports, negative fixture. |
| P2 | Grid sizing omitted actual gaps and capped font budget at 150% | `ui/screens/DrawerLayoutPolicy.kt`, `SearchScreen.kt`: account for compact/normal padding/gaps, budget up to 200%, preserve equal two-line labels. |
| P2 | Lookup repetition and small combat allocation/state issues | Inventory-scoped resource-ID cache including misses; missile/bomb/training temporary lists removed; explicit combat blend state; adjacent Line passes merged. |

## Icon and game presentation

Three dedicated vector glyphs replace generic mappings: Calendar, Google Home and Notion. Total: 101 direct targets, 446 generated targets, 1,908 mappings. New resources are explicitly retained for shrinking; existing quality artwork remains. Canvas tokens centralize silver, muted gold, dark surfaces and stroke. Accent Paint restoration keeps subsequent linework consistent. System and existing standalone VOID.SYS selection behavior is preserved; no claim of general third-party pack compatibility.

Rendered/reviewed only the three new vector source previews at 48/60 pixels using local librsvg/cairo. [PNG](../audits/artifacts/task-008/voidsys-vector-preview.png). Full HTML sheet generated, not reviewed in full. No Android/fallback runtime screenshot acceptance claimed.

Shooter adds a filled graphite interceptor, cockpit/engine insets beneath existing silhouette linework, a HUD unaffected by combat shake and a distinct campaign `MISSION CLEAR` result. Existing distinct enemy types, missile travel, finite weapons, power-ups, modes, score keys, fixed-step update, restart parameters, reduced effects and deferred disposal remain. This sprint fixes the listed mechanics; it does not certify every control, every campaign wave or a finished premium game. Full UI/device matrix and long-session profiling remain required.

Home, dock, drawer/search, Settings, widget picker/operation policy, Ambient, navigation/insets and inventory were inspected with existing audits/tests. Changes are limited to confirmed issues above. No broad redesign or claim that all previously listed widget/metrics/terminal issues were closed.

## Validation

Final committed-source debug run: **BUILD SUCCESSFUL in 9m 54s**, 62 actionable tasks (18 executed, 44 up-to-date). All four requested tasks passed. **48 JVM tests, zero failures/errors/skips. Lint: zero errors, 585 warnings. Instrumentation Kotlin compiled; not executed.** See [build log](../audits/artifacts/task-008/debug-validation.txt). Host uses Java 21 with Java/Kotlin target 17. SDK variables point to the existing Termux SDK; no SDK downgrade or metadata edits.

```sh
ANDROID_HOME=/data/data/com.termux/files/home/android-sdk \
ANDROID_SDK_ROOT=/data/data/com.termux/files/home/android-sdk \
./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug \
  :app:compileDebugAndroidTestKotlin --no-daemon --max-workers=2 --console=plain \
  -Pandroid.aapt2FromMavenOverride=/data/data/com.termux/files/usr/bin/aapt2
```

Additional release validation: `:app:assembleRelease --offline` with the same SDK variables, AAPT2 override, daemon/worker/console flags **passed in 8m 7s** (49 actionable tasks: 21 executed, 28 up-to-date). Unsigned/minified APK: **36,810,026 bytes**; SHA-256 `96931300e4ba0b85c52c51a03e87394173604e5b5eff41324df725b8c3458347`. Actual `aapt2 dump resources` inspection retains **101/101 direct icon names**, zero missing. [Release log](../audits/artifacts/task-008/release-validation.txt), [validation summary](../audits/artifacts/task-008/validation-summary.json). Packaged native audit still exits 1 for 4 KB ELF LOAD alignment; ZIP alignment alone does not fix it. No paid signing or authorization claim is implied.

The initial overlapping edit/build attempt compiled an earlier main snapshot and failed test compilation when later tests referenced new source declarations. It is not final-tree evidence; its obsolete lint work was terminated after the test compilation failure. A complete run against the committed tree supersedes it; both logs retained for transparency.

Python PNG/resource audit fixtures: 2 tests passed. Source icon QA produces [machine inventory](../audits/artifacts/task-008/voidsys-inventory.json), [human summary](../audits/artifacts/task-008/voidsys-summary.md), and [contact sheet](../audits/artifacts/task-008/voidsys-contact-sheet.html). Zero source invariant failures; 15 duplicate artwork groups remain review findings, not missing mappings.

No connected instrumentation, device installation, game GL execution, performance/battery benchmark or OEM verification performed. SDK adb exists but execution fails (`no such file or directory`, incompatible host executable), and no native Termux adb is installed. This is an environment limitation, not an app source failure. No licensing blocker has been encountered in this build environment; the pre-existing duplicate `android-36-2` SDK-location warning remains local configuration work.

## Remaining acceptance and recommended next task

Follow [remaining work](remaining-work.md) and both dedicated QA checklists. Required: API 26/33–34/36 devices, 320dp/large fonts/insets/IME and large inventories, package/icon-pack refresh/removal, widget bind/config/cancel/restore/process death, wallpaper restrictions/cancellation, multi-touch weapons and pause, campaign completion/retry, Survival soak, Training, context loss, repeated entry/exit cleanup and actual frame/memory measurements. In-progress Shooter runs are not persisted across process death. Menu typography/touch targets, fallback collision recognition and broad icon art acceptance remain unfinished.

Recommended next task: rebuild/migrate matching LibGDX Java/backend/native artifacts for 16 KB support, establish executable native adb and run the launcher/Shooter device matrix. Keep entitlement/backend implementation as a separate owner-configured security workstream. Commercial release remains blocked by that contract, native compatibility, device qualification, artwork/legal provenance and final visual/performance approval.

## Commits

- `f0bf0cb` — Give VOID.SYS fallbacks identity and expand resource QA.
- `5d67575` — Stabilize Shooter touch ownership and prevent upward dive stalls.
- `93d5ad9` — Move wallpaper work off main and budget drawer grid spacing.
- Final evidence/documentation commit follows these implementation commits; use `git log` to identify its full hash.
