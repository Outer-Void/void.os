# AXIOM release qualification — remaining work

**Not release-ready.** Task 010 implements durable widget transactions and migrates matching libGDX artifacts for 16 KB binary alignment; see [Task 010 results](task-010-validation.md). Task 008 adds focused icon, Shooter and launcher remediation; see [results](task-008-results.md). Task 007 remains the baseline evidence, not commercial/device sign-off. Use the [full audit](../audits/axiom-full-audit.md), [VOID.SYS QA](../audits/voidsys-icon-qa.md), [Shooter QA](../audits/axiom-shooter-qa.md) and [machine evidence](../audits/artifacts/validation-summary.json).

## Release blockers

| Priority | Work | Required acceptance evidence |
| --- | --- | --- |
| P0 | Real paid-app entitlement, backend-verified Play Integrity, canonical cryptographic product signatures, cert/key rotation and protected signing | Valid purchase/restore, expiry, copied/replayed token, forged signature, wrong package/cert/version/install nonce, revocation and defined offline grace tests. Public verification material may be bundled; private signing/API credentials must stay outside Git/APK. Current release gates stay closed. |
| P1 | 16 KB runtime qualification | Task 010 matching libGDX 1.14.2 migration corrects source/debug binary LOAD alignment to 16384; final debug/release evidence in Task 010 report. Required: actual 16 KB GL/device gameplay, context loss and distribution AAB checks. Binary alignment alone is insufficient. |
| P1 | Device-backed launcher/game qualification | API 26, 33/34, 36, two OEM families, 320dp/large text, gesture/three-button navigation and large screen. Record device/model/API, commit/APK checksum, steps, actual result, logcat and screenshots. No usable ARM64 adb was available in this task. |
| P1 | Legal/product distribution review | Bundle appropriate third-party/font notices and verify asset provenance/trademark use with the product owner. Proprietary root LICENSE alone does not establish rights to redistributed fonts/art. Complete privacy/Data safety and permission disclosures. |
| P1 | Premium icon/art and interaction acceptance | Review 101 direct targets plus generated fallbacks on real installed apps; expand recognition deliberately. Verify the seven authored vector glyphs, all icon shapes, font scales, labels, dock hit targets and final Shooter UI with actual screenshots. |

Google's current phone/tablet submission policy requires API 36 from August 31, 2026; the configured target satisfies that level, not the rest of policy. Verify the relevant Play Console track/exemptions at submission. Sources checked October 21, 2026: [Play target requirements](https://support.google.com/googleplay/android-developer/answer/11926878), [Android 16 KB guidance](https://developer.android.com/guide/practices/page-sizes). Console-specific extensions do not establish binary compatibility.

## Next engineering batches

1. **Next task: native compatibility and device reliability.** Establish executable native adb; rebuild/migrate LibGDX native dependencies for 16 KB support without lowering API 36/min API 26; run the four debug tasks plus unsigned R8, APK/AAB inspection and the device matrix. Keep game/rendering and launcher regressions separate. Acceptance: recorded 16 KB GL gameplay, no native crashes, repeated Home/game cycles, widget bind/config/cancel/process-death checks and package/icon refresh recordings.
2. **Task 009: commercial authorization contract.** Product owner supplies Play/backend environment and threat-model decisions. Implement actual verification and tests. Do not invent credentials, a purchase server or licensing policy. Audit the legacy unkeyed launch digest and remove misleading signature claims as part of the real contract.
3. **Task 010: final visual and performance acceptance.** Expand the commonly installed app corpus, approve direct icon art, profile 30-minute Survival and large inventories, finish accessibility/IME/inset checks, measure contrast and frame p95. Review all remaining Home/Feed/Settings/folder/widget/system/terminal/wallpaper screens.
4. Task 008 moved Settings wallpaper decode/setBitmap/preview compression to serialized IO coroutines. Device-check completion/cancellation/repeated taps and OEM handoff. Remediate lifecycle polling/terminal stream/state ownership, transactional widget persistence/orphan IDs, RAM metric wording, package/profile/locale behavior and 585 existing lint warnings. Keep unsupported data integrations explicitly Planned until real functionality exists.

## Device-only checklist

- Launcher: cold boot/default launcher, Back from every state, Ambient, slow/diagonal drags and nested scrolling, Home reselect, drawer IME/Enter/no results, long labels/200% font, TalkBack and accessible click targets.
- Inventory/icons: large installs, remove/update while open, pack selection/update/removal, aliases, fresh icons after refresh, process recreation and work/cloned profiles.
- Widgets: allocate/bind/configure success and every cancel/failure; unavailable providers, replacement view ID, package updates, resizing, background/restore/process death, reset and panel-count reduction.
- Folders/favorites: persistence, hide/unhide, launch/uninstall/app info, empty/large sets, locked favorites, font scaling and all seven dock targets.
- Feed: no permissions requested for planned data integrations; actual notes/tasks/timers/shortcuts persist and behave truthfully. Media permission reflects real Android access.
- Wallpaper/system: home/lock/both and OEM restrictions, picker URI grants, no I/O crash, denied camera/unsupported intents, truthful torch/network state.
- Shooter: every item in its dedicated QA checklist; hidden entry only, release gate denial, restart mode, actual missile/bomb/hit/cooldown behavior, pause/predictive Back/Home return, GL context recreation and process death.
- Commercial: signed internal-track build, purchase/restore/revocation/offline/security matrix, upgrade paths, privacy/notice review and final product-owner approval. No release tag/publish before these gates close.

## Build environment and evidence limits

The existing Termux SDK has API/build-tools 36. Use SDK variables and the existing command-line AAPT2 override; do not hardcode personal paths in Gradle or downgrade versions. The SDK manager reports a duplicate API 36 platform in `android-36-2`; resolve that local installation separately. Java 21 was used locally, Java 17 remains CI/bytecode target.

**Task 007 baseline:** debug validation passed the four requested Gradle tasks with 39 JVM tests and zero failures/errors. Lint has zero errors and 585 warnings. The debug APK is 58,604,984 bytes (about 55.9 MiB); compressed fonts account for 32,552,880 bytes. Do not mistake debug APK size for a measured Play delivery size. Font catalog/provenance and oversized/duplicate image assets need a deliberate follow-up without silently removing existing customization.

Icon mapping/pixel inventory and PNG fixture tests pass. Native alignment check intentionally exits 1. No device installation, instrumentation execution, screenshot set, frame-rate/battery benchmark or paid-product validation was performed. CI has been updated for API 36 and instrumentation compilation, but its remote run is not a local test result.

The initially uncommitted nullable-certificate change was retained exactly and committed separately as `2fa558d`; it was not discarded or silently mixed into another patch. Existing local Home-only access and completion-plan commits are also preserved in the published history. Task commits are enumerated in the full audit.

Final qualification addition: the combined five-task build (including `:app:assembleRelease`) passed after correcting misplaced icon keep metadata. Actual shrunk-APK inspection retained all 98 direct icon names. Unsigned/minified release APK: 36,716,372 bytes; checksum in the validation summary. The earlier release compile passed but lost 58 names, demonstrating why compile success was insufficient. CI now checks those names after an unsigned release build. Neither APK is a paid-release approval.

## Task 008 continuation checklist

- Use `feat/task-008-stabilization` and the exact commit/test evidence in `task-008-results.md`; preserve all Task 007 history.
- Device-test the new monogram/category composition, three vectors, 200% font/gap-aware drawer, serialized wallpaper writes/handoff and cancellation. Review fallback identity collisions and the existing nested tile framing before expanding artwork.
- Playtest downward-only dive acquisition, multi-thumb takeover, pause/release quarantine, all weapon/power-up combinations, mission-clear title, retry mode and explicit Home return. Capture GL/frame/memory evidence for the filled ship and stable HUD.
- Complete the broader premium icon corpus, Shooter menu typography/touch targets, in-progress recovery and launcher widget/metric/lifecycle work listed above. Task 008 does not close those product acceptance items.
- Preserve fail-closed entitlement/signature/Integrity behavior. No signing credentials or authorization bypasses were introduced.

Task 008 final evidence supersedes the baseline counts: four requested debug tasks pass; 48 JVM tests, zero failures/errors/skips; lint zero errors/585 warnings; instrumentation compiled only. Unsigned minified release passes, with 101/101 dedicated icon resource names retained. Source and packaged 16 KB native audits still fail. See the Task 008 results and machine summary for APK sizes/checksums and exact limits.

## Task 009 first increment — continuation

Baseline `e78e28f` preserved. See `task-009-validation.md` and `../audits/task-009-complete-inventory.md` for new evidence. Task 009 fixes Terminal ownership/output/cleanup, the misleading Home RAM metric and stopped-Activity System polling. This does not close the prior commercial blockers or any device gates.

Next session prompt:

> Continue Task 009 from current main and its recorded commits. Read AGENTS.md, task-009-validation.md, task-009-complete-inventory.md, bug ledger, reports/task-009/completion.json and this file; do not repeat discovery or discard prior fixes. Keep at most two running background commands and two Gradle workers. First establish executable native adb and a real API 36/16 KB test environment, then qualify Terminal teardown/restart, system memory, stopped/resumed polling and widget bind/config/cancel/process-death recovery. Implement transactional widget orphan-ID fixes only after reviewing MainActivity/AxiomWidgetHost state. Keep API 36/min 26 and fail-closed authorization. Next, incrementally address per-icon artwork using the 2,903-variant inventory; record actual before/after reviews. Complete the remaining Shooter and screen matrix without calling source indexing visual acceptance. Run the four requested debug tasks, meaningful regressions, minified release/icon/native audits, record device evidence honestly, and commit focused changes. Do not publish or rewrite remote history.

Remaining stages: finish full Stage 1 behavioral classification (index exists), Stage 2 broad lifecycle/widget/profile review, Stage 3 actual shared token adoption/contrast/accessibility, Stage 4 every required individual icon upgrade/review, Stage 5 device runtime cache/drawer QA, Stage 6 all gameplay modes/controls/device acceptance, Stage 7 reviewed Shooter visual transformation, Stage 8 every screen walkthrough, Stage 9 measured performance and broad regression/device tests, Stage 10 actual production authorization/signing/legal/distribution qualification. No commercial completion claim.


## Task 010 continuation — authoritative next work

Read [Task 010 validation](task-010-validation.md) and `docs/audits/artifacts/task-010/validation-summary.json` before repeating audits. Baseline `15867e4` and all prior work retained. New code: atomic panel/pending/cleanup DataStore writes, UUID/stage-scoped external-result handling, old-widget preservation until confirmed commit, ownership-checked orphan recovery, explicit removal/reset, lifecycle-scoped host listening and storage retry. Matching libGDX 1.14.2 core/backend/four ABI archives replace obsolete tracked 1.12.1 natives; generated libraries stay in ignored build outputs. Native audit now rejects missing inputs and packaged defects; CI runs it on debug/release.

**Next acceptance task:** Finish native adb pairing in native Termux (`adb pair IP:PAIRING_PORT`, then `adb connect IP:DEBUGGING_PORT`); mDNS is unavailable in this package. Do not turn off security or auto-select the HOME role. Build/install the instrumentation APK using native adb, run WidgetHostRestorationTest and the existing suite, then perform real bind/config/replace/cancel/Home-return/recreate/process-death/provider-removal/reset/panel-reduction flows. Test on 16 KB and ordinary kernels, API 26/33/34/36, 320dp/large font/large screen and both navigation modes. Record model/API/page size, APK checksum, logcat and screenshots. Host JVM disk reconstruction is simulated recovery, not a physical process-death result.

**Task 011:** Dedicated VOID.SYS artwork overhaul using unchanged 2,903-variant inventory and 1,908 mappings. Prioritize high-visibility recognizable glyphs, document 15 duplicate-art groups/39 unused candidates without deleting blindly, capture before/after, review runtime fallbacks and all supported shapes/sizes, verify package/cache/shrinker behavior. Zero newly redesigned or visually approved icons in Task 010.

**Task 012:** Preserve native compatibility and hidden Home-only access while completing Shooter graphics and remaining gameplay/device qualification. Audio and in-progress run persistence remain incomplete; campaign/Survival/Training playthroughs and actual GL cleanup/frame timing are still required.

**Other open work:** Real entitlement/Integrity/cryptographic verification and protected signing/console setup remain fail closed; asset/font notices and legal review remain blockers. Broader launcher profile/locale/IME/inset/accessibility/default-HOME checks, 585 baseline lint warnings, terminal command ordering/descendant cleanup, wallpaper/OEM behavior and measured startup/frame/memory/battery results are not closed by the widget/native increment. No production release approval.

Continuation prompt: Continue from current main and Task 010 evidence; preserve the widget transaction journal and reproducible gdxNatives extraction. Keep at most two background commands and one Gradle build, using `--no-daemon --max-workers=2 --console=plain` plus native AAPT2 and in-process Kotlin compilation on this ARM64 host. Device pairing and the widget/native physical matrix take priority. Advance Task 011 artwork only with individual recorded visual evidence. Keep API 36/min 26 and release fail-closed security. Test, document, commit focused changes, fetch/check divergence, synchronize main, push with force-with-lease, verify remote identity and a clean tree.
