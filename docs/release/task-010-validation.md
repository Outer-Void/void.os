# Task 010 — widget integrity, native compatibility and release hardening

**Verdict: NOT commercially release-ready.** Baseline `main` at `15867e4`; clean initial tree. Dedicated branch `feat/task-010-reliability`. Task 009 source changes, tests, evidence, inventory and outstanding blockers reviewed. No uncommitted user changes existed. No product art, mappings, game mechanics, permissions, secrets or signing configuration replaced.

## Baseline and scope

PASS — Gradle 8.11.1, AGP 8.10.0, Kotlin 1.9.24, Compose BOM 2024.06.00/compiler 1.5.14; compile/target API 36, min API 26, bytecode 17, host Java 21 retained. AGENTS.md's older AGP/test-directory description is stale; actual build files/tests are authoritative. README, packaging/security notes, Task 009 reports and complete source diff reviewed. Four tracked legacy `.so` assets were the prior native source; no APK, local SDK config, signing key or generated Gradle output is intentionally tracked. They are replaced by Maven-sourced build-time extraction, not deleted without replacement.

## Defects and root causes

| ID | Status | Defect and correction |
| --- | --- | --- |
| W1 | PASS automated; UNVERIFIED device | Old widget ID deleted before async settings persistence. Panel pointer, pending operation and garbage queue now use one atomic DataStore edit; deletion follows confirmed commit. |
| W2 | PASS automated; UNVERIFIED provider UI | Pending bind/config existed only in Compose saved state. Durable operation UUID/stage plus operation-specific ActivityResultRegistry keys restore pending work and reject stale callbacks. |
| W3 | PASS automated | Allocation/write/crash/cancel/delete failures leaked or prematurely released IDs. Owned-ID reconciliation protects all assigned/pending IDs and retries queued cleanup. |
| W4 | PASS source/compile; UNVERIFIED device | Widget host listening survived stopped Activities. STARTED lifecycle now owns listening. Return Home without a provider result restores continuation/cancellation controls. |
| W5 | PASS automated | Malformed, duplicate or wrong-type widget records were accepted nondeterministically. Decoder flags corruption, mutations/cleanup fail safe, and explicit confirmed widget-only reset repairs preference records. Raw DataStore file corruption/IO failure remains visible and does not trigger deletion; retry is explicit. |
| N1 | PASS binary; UNVERIFIED runtime | libGDX 1.12.1 tracked natives had 4 KB LOAD alignment. Matching core/backend/platform 1.14.2 replaces them; all four ABIs preserved. |
| N2 | PASS automated | Native audit checked only source on exit and passed vacuously without inputs. It now requires both 64-bit architectures, checks segment geometry, and fails packaged defects independently of source. |
| M1 | PASS automated | Large battery values overflowed on narrowing after Long arithmetic. Clamp before narrowing. System RAM subtraction suffered floating-point cancellation at exact percentages; subtract byte counts first. |

## Widget transaction design

`WidgetTransactions` serializes operations on IO. Its management-only AppWidgetHost holds application context and never creates views. Rendering uses a separate Activity/composition-scoped host with the same host ID, listening only while STARTED. AOSP retains view listeners after stopListening; the rendering host must not survive Activity recreation in a ViewModel. [Framework source](https://raw.githubusercontent.com/aosp-mirror/platform_frameworks_base/master/core/java/android/appwidget/AppWidgetHost.java). `PreferencesWidgetStore` uses the existing settings DataStore and existing `PanelWidgets` key, plus versioned pending/cleanup keys. Durable stages: Prepared → Binding → Configuring when required. The panel map remains authoritative while an operation is pending; no separate stale copy of the old assignment is trusted.

1. Allocate candidate; persist UUID/panel/provider/candidate/stage without changing the old assignment.
2. Persist Binding before platform bind/permission effects; persist Configuring before configuration launch.
3. Check candidate ownership and exact provider. In one DataStore edit, replace panel ID, clear pending state and queue the previous ID.
4. Only after edit returns, release unreferenced, host-owned IDs. A crash after deletion but before queue acknowledgement is safe to retry.
5. Cancellation clears pending state and queues only the candidate. Duplicate/stale operations cannot commit/cancel a different setup. Rapid repeated selection refuses a second active operation.
6. Allocation-before-journal crash windows and abandoned IDs are reconciled against host ownership on foreground recovery. Valid pending work never expires automatically: the user explicitly resumes/cancels it, avoiding destruction of long-running provider setup.
7. Panel-count reduction and settings reset remove assignments durably before cleanup. Widget-only reset requires confirmation and preserves other settings. Removal is exposed in the widget picker.

Malformed preference records block destructive reconciliation; a confirmed widget reset is required. Storage read failures never become an empty authoritative widget map. Errors surface with Close/Retry controls; retries do not run a background polling loop. Disk-level DataStore corruption is BLOCKED for further diagnosis rather than silently erasing configuration.

## Widget regression evidence

PASS initial run — 15 transaction tests plus three preference tests added. Existing two widget policy tests preserved. New tests exercise installation, replacement, cancellation, provider/bind failure, configuration cancellation, failed durable write, failed begin/orphan recovery, duplicate/wrong-stage callbacks, rapid selection, reconstructed coordinator/configuration continuation, retry after cleanup failure, stale-operation fencing, foreign/referenced-ID protection, malformed records, and actual disk-backed DataStore closure/reconstruction. Reconstruction is simulated process/lifecycle recovery, not physical process-death testing.

UNVERIFIED execution — new Android `WidgetHostRestorationTest` allocates an actual host ID, verifies a recreated host owns it, starts/stops listening, and releases only the test ID. Instrumentation compilation is required below; no provider automation or device execution is inferred from compilation. Binding/configuration OEM UI, Home return, Activity recreation, low-memory recovery and actual process death still require devices.

## Native inventory and alignment

Matching Maven Central coordinates: `com.badlogicgames.gdx:gdx`, `gdx-backend-android`, and `gdx-platform` native classifiers, all **1.14.2**. `gdxNatives` is a dedicated non-consumable configuration. `extractGdxNatives` syncs only `.so` files into ABI-specific `app/build/generated/gdxNatives`; merge tasks depend on it, Gradle tracks archive inputs, and Sync removes stale outputs. No generated native binaries are committed. [Archive hashes](../audits/artifacts/task-010/native-artifacts.json).

| ABI | ELF architecture | LOAD alignment | Packaging |
| --- | --- | --- | --- |
| armeabi-v7a | ARM, 32-bit | 16384 for every segment | Generated libgdx.so; debug/release checks below |
| arm64-v8a | AArch64, 64-bit | 16384 for every segment | Same |
| x86 | x86, 32-bit | 16384 for every segment | Same |
| x86_64 | x86-64, 64-bit | 16384 for every segment | Same |

PASS initial debug binary — all four APK entries are stored and 16 KB ZIP-aligned; LOAD alignments and file-offset/virtual-address congruence pass. Final debug/release audits are recorded in machine results below. ELF alignment and ZIP alignment are separate checks. Compressed entries do not require direct-mmap ZIP alignment; their ELF geometry still matters. Missing inputs fail instead of producing an empty-list success. Python parses ELF/ZIP bytes, so no incompatible host readelf/zipalign executable is required. Tool versions: Python 3, GNU readelf 2.44, Gradle/AGP above, build-tools 36.0.0, existing native Termux AAPT2. Android build notes identify NDK r25c, API 19 for armeabi-v7a/x86 and API 21 for arm64-v8a/x86_64, below the app minimum API 26. These notes are metadata, not runtime proof; [full notes](../audits/artifacts/task-010/native-build-notes.json).

UNVERIFIED — actual GL execution on a 16 KB kernel, API 26 behavior and full Shooter playthrough. Binary compliance does not prove runtime compatibility. [Android guidance](https://developer.android.com/guide/practices/page-sizes) distinguishes rebuilding ELF and package alignment. [libGDX 1.14.2 release notes](https://libgdx.com/news/2026/05/gdx-1-14-1) describe Android Back and graphics destruction fixes; migration is justified by inspected native geometry, not an indiscriminate dependency upgrade.

## ADB and device pipeline

PASS tooling — installed native Termux `android-tools` 37.0.0p1-3; adb reports version 1.0.41/37.0.0p1 and runs on aarch64. The SDK's x86-64 adb remains unsuitable. Initial `adb devices -l` returns no devices; this Termux build reports mDNS unsupported. No security settings changed automatically. Server was stopped after discovery to avoid a persistent background task. Read-only `getprop init.svc.adbd` returned `stopped`; wireless-debugging port properties were empty. [Tooling/environment evidence](../audits/artifacts/task-010/device-environment.json).

BLOCKED device execution — pairing requires user action. In native Termux:

```sh
adb pair PHONE_IP:PAIRING_PORT
# Enter the temporary pairing code in Termux, not in source or logs.
adb connect PHONE_IP:DEBUGGING_PORT
adb devices -l
```

Enable Developer options → Wireless debugging → Pair device with pairing code. Pairing and debugging ports differ; use the address/ports displayed on the phone. Manual host/port commands avoid mDNS. If OEM policy/network isolation prevents self-connection, use an authorized second Android target or supported host; do not weaken device security.

After pairing, invoke native adb directly rather than the SDK's incompatible binary:

```sh
ANDROID_HOME=/data/data/com.termux/files/home/android-sdk \
ANDROID_SDK_ROOT=/data/data/com.termux/files/home/android-sdk \
./gradlew :app:assembleDebug :app:assembleDebugAndroidTest \
  --no-daemon --max-workers=2 --console=plain \
  -Pkotlin.compiler.execution.strategy=in-process \
  -Pandroid.aapt2FromMavenOverride=/data/data/com.termux/files/usr/bin/aapt2
# In native Termux, or use the full native adb path from Debian:
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb install -r app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
adb shell am instrument -w com.outervoid.axiom.test/androidx.test.runner.AndroidJUnitRunner
```

If multiple devices are listed, select the intended target with `adb -s SERIAL` on every command. This install/test sequence remains UNVERIFIED until connected execution. Record device/API/page size, APK checksum and actual results. Capture `adb logcat`, `adb exec-out screencap -p`, `adb shell dumpsys meminfo com.outervoid.axiom`, and `adb shell dumpsys gfxinfo com.outervoid.axiom framestats` only against a connected authorized target. HOME/default-role checks require deliberate user role selection.

## Launcher, VOID.SYS, Shooter and security

PASS automated baseline regressions where covered — navigation/search policies, gestures, settings decoding, inventory coalescing/ordering, external-launch safety, icon lookup/mapping and Shooter rules/input/lifecycle. UNVERIFIED device — drawer focus/scroll, package updates, work profiles, default HOME role, edge-to-edge/insets, gestures/three-button navigation, large fonts, external returns and OEM permissions. No manifest permissions/export flags or broad package queries added. NOT APPLICABLE — no new app-authored PendingIntent flow in this patch.

VOID.SYS art/mappings preserved: 2,903 drawable/mipmap variants, 1,908 mappings, 547 mapped resource names (101 direct/446 generated). Prior 15 identical-artwork groups and 39 unused candidates remain review items. Runtime fallback frequency UNVERIFIED; no installed corpus measured. Existing fallback cache is byte-budgeted; resource-ID lookup caches remain provider/inventory scoped. No new artwork or optical acceptance claimed. Task 011 owns individual art work.

Shooter gameplay/render source unchanged; 18 existing Shooter JVM regressions pass in the initial run after dependency migration. Hidden Home-only access policy and non-exported Activity preserved. Campaign/Survival/Training/score/weapon and native lifecycle tests are source/JVM evidence, not real GL gameplay. UNVERIFIED — all-mode physical playthrough, multitouch, context loss, restart and score persistence on real storage. Task 012 owns the visual overhaul. In-progress run persistence/audio remain incomplete.

BLOCKED security — `IntegrityGate.kt:9` lacks production attestation, `EntitlementManager.kt:34` lacks real paid entitlement, `SignatureVerifier.kt:38` remains a cryptographic-verification TODO returning false. Existing `BuildConfig.DEBUG` bypasses remain debug-only; no release success stubs added. Release signing, protected keystore, backend/console configuration, replay/revocation/offline policy and legal/font/art notices remain owner-dependent. The legacy app-side launch digest is not a real product signature. Release continues to fail closed.

## Build, test and performance evidence

Initial four-task debug pass: PASS in 12m 19s, 72 JVM tests, zero failures/errors/skips, lint zero errors/585 warnings, instrumentation Kotlin compiled only. This exploratory run overlapped final review edits and is not final-tree qualification. Combined qualification passed in 12m 19s. Final integration passes cover explicit-action storage reader refresh, cancellation propagation, and Activity-scoped rendering-host ownership; final source hashes must match before committing. It includes:

```sh
ANDROID_HOME=/data/data/com.termux/files/home/android-sdk \
ANDROID_SDK_ROOT=/data/data/com.termux/files/home/android-sdk \
./gradlew :app:assembleDebug :app:testDebugUnitTest \
  :app:compileDebugAndroidTestKotlin :app:lintDebug :app:assembleRelease \
  --no-daemon --max-workers=2 --console=plain \
  -Pkotlin.compiler.execution.strategy=in-process \
  -Pandroid.aapt2FromMavenOverride=/data/data/com.termux/files/usr/bin/aapt2
python3 -m unittest discover -s app/tools -p 'test_*.py'
python3 app/tools/release_native_qa.py --apk app/build/outputs/apk/debug/app-debug.apk
python3 app/tools/release_native_qa.py --apk app/build/outputs/apk/release/app-release-unsigned.apk
# Dump release resources using native AAPT2, then run voidsys_qa.py --resource-dump DUMP.
```

Final results: PENDING. See `docs/audits/artifacts/task-010/validation-summary.json` for final outcomes, exact counts, APK sizes/checksums and evidence paths. Eight Python tests pass, including four new ELF/ZIP negative fixtures. CI now caps Gradle workers and audits debug/release native alignment after builds; remote CI execution remains UNVERIFIED.

Performance: UNVERIFIED runtime measurements. Widget bookkeeping/ownership checks run on IO; binding/Activity launch stays on Android's main thread. No FPS/startup/battery/memory improvement claimed. No extra polling introduced. First extraction implementation triggered configuration-time resolution warning; replaced with execution-time extraction before qualification. One intermediate combined build was deliberately cancelled (exit 143) after review moved widget controls inside the existing theme and product-unlocked branch; its log is not final-tree evidence. No concurrent Gradle builds were run. Source hashes captured at qualification start are verified again before committing. SDK duplicate-platform and foreign native-strip tooling warnings, if present, are environment/tooling findings, not suppressed alignment failures.

## Outstanding release gates and next steps

BLOCKED: connected device/16 KB GL and full launcher/widget/OEM matrix, real authorization/signing, legal provenance and reviewed artwork/performance acceptance. Required matrix: API 26/33/34/36, 320dp/typical/large display, increased fonts, both navigation modes, actual process death/low-memory, widget/configure cancellation, provider updates/uninstalls, all Shooter modes and repeated Home/game transitions. Final native binary audit success may close N1's binary defect; it cannot close runtime qualification.

Task 011: preserve Task 010 widget/native changes, use the existing 2,903-variant inventory, fix high-visibility dedicated icon recognition first, document intentional duplicates, capture before/after source and real launcher rendering, test fallback/cache/package-update behavior, and never count a shared background change as individual icon modernization. Pair the device and execute the widget tests/manual provider matrix before commercial qualification. See [remaining work](remaining-work.md).
