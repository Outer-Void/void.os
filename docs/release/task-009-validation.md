# Task 009 validation and continuation

**Not commercially release-ready.** This is a verified reliability/inventory increment, not the requested complete product transformation. Baseline `feat/task-008-stabilization` at `e78e28f`; initial tree clean. Implementation commit `79c759f`. No prior source/history discarded, no dependency/SDK/min API change, no secrets/signing/authorization bypass.

## Implemented and tested

Terminal process start/write/read moved to IO; generation guards prevent stale completion, output appends are atomic and retained at 500 lines with 4096-character reader chunks, streams are closed and expected IO errors are visible. Host shell tests cover natural exit/restart, pending-start disposal and bounded output/start failure. Non-PTY interactivity, child processes, rapid concurrent command order and OEM teardown remain limitations/device work.

Home RAM now uses ActivityManager system available/total memory instead of JVM heap. Battery conversion uses a Long intermediate. Three additional metric regressions cover large/invalid memory and large battery scale. System-panel polling runs only while lifecycle STARTED; runtime background/foreground validation remains pending.

## Debug evidence

Exact requested four-task Gradle invocation passed in **7m 21s**, 62 tasks (18 executed, 44 up-to-date). **54 JVM tests: zero failures, errors or skips.** Six new JVM tests. **Lint: zero errors, 585 warnings** (same count as baseline). Instrumentation Kotlin compiled, not executed. [Log](../audits/artifacts/task-009/debug-validation.txt).

```sh
ANDROID_HOME=/data/data/com.termux/files/home/android-sdk \
ANDROID_SDK_ROOT=/data/data/com.termux/files/home/android-sdk \
./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug \
  :app:compileDebugAndroidTestKotlin --no-daemon --max-workers=2 --console=plain \
  -Pandroid.aapt2FromMavenOverride=/data/data/com.termux/files/usr/bin/aapt2
python3 -m unittest discover -s app/tools -p 'test_*.py'
python3 app/tools/task009_inventory.py
python3 app/tools/voidsys_qa.py --output reports/voidsys/source-qa
```

Four Python QA tests pass (two new metadata fixtures); no measured performance improvement claimed. Build duration is a build measurement, not launcher responsiveness. At most two running background commands used; Gradle workers capped at two. Runtime uses existing Java 21; target bytecode remains Java 17.

## Inventory and visual limits

2,903 drawable/mipmap variants (2,892 PNG, nine vector, two adaptive); 1,908 map entries, 547 mapped resource names, 101 direct and 446 generated targets. Source invariants pass; 15 identical-artwork groups and 39 unused candidates remain review findings. Per-file hash groups also include legitimate density variants and are not a defect count. **Zero newly visually upgraded/verified icons; all 2,903 variants pending individual review.** No launcher or Shooter screenshot/physical review, frame-rate/battery/memory profiling, or new Shooter mechanics/visual work performed.

See [complete inventory](../audits/task-009-complete-inventory.md), [bug ledger](../audits/task-009-bug-report.md), [completion manifest](../../reports/task-009/completion.json), [icon ledger](../../reports/voidsys/icon-inventory.json).

## Final DevOps pass

Final combined debug/test/lint/instrumentation-compile and unsigned minified release build **passed in 6m 25s**, 109 tasks (12 executed, 97 up-to-date), against implementation commit `79c759f`. Previously passed debug checks were up-to-date, not forced reruns. See [final build log](../audits/artifacts/task-009/final-devops-validation.txt) and [machine results/APK sizes/checksums](../audits/artifacts/task-009/validation-summary.json). **101/101 dedicated icon names survived shrinking; zero missing. Packaged native audit exits 1 (4 KB ELF LOAD alignment); this release blocker remains open.** Debug APK: 58,605,601 bytes. Unsigned minified release APK: 36,810,026 bytes. Logs have terminal progress/line whitespace normalized; commands and outcomes retained. Source native audit exits 1 because 64-bit ELF LOAD alignment remains 4096. This is an application distribution blocker, distinct from adb host incompatibility. Unsigned release build success is not signing or commercial approval.

[adb evidence](../audits/artifacts/task-009/adb-environment.json): native Termux adb unavailable, SDK adb cannot execute in this ARM64 host. No device tests, installation or screenshots claimed. Required matrix: API 26/33/34/36, 320dp/phone/large screen, increased fonts, gesture/three-button navigation, recreation/process death/low-memory, widget/package flows, all Shooter modes/multitouch and 16 KB GL execution.

## Remaining gates and next task

Real paid entitlement, backend Integrity, canonical signatures, protected release signing/console setup, 16 KB native compatibility, device qualification, artwork/font legal provenance, every-icon visual acceptance and every-screen/game/performance review remain blockers. Release remains fail closed. Confirmed open widget replacement risk: old ID deleted before async new-state persistence; transactional persistence/orphan recovery is the next app fix, after establishing device access. Read the exact continuation prompt in [remaining work](remaining-work.md); do not repeat completed discovery.
