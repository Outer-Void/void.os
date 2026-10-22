# Task 009 bug ledger

| ID | Severity | Confirmed defect | Remediation / verification |
| --- | --- | --- | --- |
| T009-01 | P1 | Terminal shell creation and writes run on UI; output update can overwrite concurrent append; reader can end a replacement session | Process IO on Dispatchers.IO, atomic bounded StateFlow updates, generation/identity checks; host shell exit/restart and pending-start disposal regressions |
| T009-02 | P1 | Terminal reader exceptions silently discarded; reader/streams not explicitly released; unlimited line length | Expected IO failures visible; structured reader use/finally cleanup; 4096-character output chunks and 500 retained lines |
| T009-03 | P2 | Home RAM reports this app's JVM heap fraction, misleading system metric | ActivityManager totalMem/availMem; unavailable samples remain `--`; 64-bit/invalid-sample tests |
| T009-04 | P2 | Battery percentage multiplies Int before division | Long intermediate and large-scale regression |
| T009-05 | P2 | System panel polls while stopped if composition survives | repeatOnLifecycle STARTED; device background/foreground acceptance pending |

**Open confirmed widget risk T009-06 (P1):** `MainActivity.saveWidgetForPanel` deletes the old host ID before asynchronous `vm.setWidgetForPanel` persistence. A failed write/process death can leave saved configuration pointing to the deleted widget. Transactional replacement and orphan reconciliation require a separate tested increment; not fixed here. Widget host listening is composition-scoped rather than STARTED-scoped.

Existing critical blockers preserved: production authorization incomplete, incompatible 16 KB native LOAD alignment, physical launcher/widget/game tests absent, full per-icon artwork acceptance and legal provenance unresolved. No claim that all source was behaviorally audited or all product bugs eliminated. Terminal still uses a non-PTY shell: interactive full-screen terminal programs belong in Termux. Background commands can spawn descendants; complete descendant teardown/OEM behavior needs device verification. Commands submitted in rapid succession use asynchronous writes; command order under concurrent callers is not specified.
