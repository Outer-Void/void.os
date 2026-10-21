# AXIOM release qualification — remaining work

**Not release-ready.** Task 007 delivers verified incremental remediation, not commercial/device sign-off. Use the [full audit](../audits/axiom-full-audit.md), [VOID.SYS QA](../audits/voidsys-icon-qa.md), [Shooter QA](../audits/axiom-shooter-qa.md) and [machine evidence](../audits/artifacts/validation-summary.json).

## Release blockers

| Priority | Work | Required acceptance evidence |
| --- | --- | --- |
| P0 | Real paid-app entitlement, backend-verified Play Integrity, canonical cryptographic product signatures, cert/key rotation and protected signing | Valid purchase/restore, expiry, copied/replayed token, forged signature, wrong package/cert/version/install nonce, revocation and defined offline grace tests. Public verification material may be bundled; private signing/API credentials must stay outside Git/APK. Current release gates stay closed. |
| P0 | 16 KB native library support | Replace/rebuild the actual LibGDX 1.12.1 native source-of-truth and matching dependencies, avoid duplicate obsolete binaries, inspect all APK/AAB ELF/ZIP alignment, run game on a real 16 KB environment. Source arm64/x86_64 LOAD alignment is currently 4096. |
| P1 | Device-backed launcher/game qualification | API 26, 33/34, 36, two OEM families, 320dp/large text, gesture/three-button navigation and large screen. Record device/model/API, commit/APK checksum, steps, actual result, logcat and screenshots. No usable ARM64 adb was available in this task. |
| P1 | Legal/product distribution review | Bundle appropriate third-party/font notices and verify asset provenance/trademark use with the product owner. Proprietary root LICENSE alone does not establish rights to redistributed fonts/art. Complete privacy/Data safety and permission disclosures. |
| P1 | Premium icon/art and interaction acceptance | Review 98 direct targets plus generated fallbacks on real installed apps; expand recognition deliberately. Verify the four new vector glyphs, all icon shapes, font scales, labels, dock hit targets and final Shooter UI with actual screenshots. |

Google's current phone/tablet submission policy requires API 36 from August 31, 2026; the configured target satisfies that level, not the rest of policy. Verify the relevant Play Console track/exemptions at submission. Sources checked October 21, 2026: [Play target requirements](https://support.google.com/googleplay/android-developer/answer/11926878), [Android 16 KB guidance](https://developer.android.com/guide/practices/page-sizes). Console-specific extensions do not establish binary compatibility.

## Next engineering batches

1. **Task 008: native compatibility and device reliability.** Establish executable native adb; rebuild/migrate LibGDX native dependencies for 16 KB support without lowering API 36/min API 26; run the four debug tasks plus unsigned R8, APK/AAB inspection and the device matrix. Keep game/rendering and launcher regressions separate. Acceptance: recorded 16 KB GL gameplay, no native crashes, repeated Home/game cycles, widget bind/config/cancel/process-death checks and package/icon refresh recordings.
2. **Task 009: commercial authorization contract.** Product owner supplies Play/backend environment and threat-model decisions. Implement actual verification and tests. Do not invent credentials, a purchase server or licensing policy. Audit the legacy unkeyed launch digest and remove misleading signature claims as part of the real contract.
3. **Task 010: final visual and performance acceptance.** Expand the commonly installed app corpus, approve direct icon art, profile 30-minute Survival and large inventories, finish accessibility/IME/inset checks, measure contrast and frame p95. Review all remaining Home/Feed/Settings/folder/widget/system/terminal/wallpaper screens.
4. Move Settings wallpaper decode/setBitmap/preview compression off main (confirmed synchronous callbacks in MainActivity). Remediate lifecycle polling/terminal stream/state ownership, transactional widget persistence/orphan IDs, RAM metric wording, package/profile/locale behavior and 585 existing lint warnings. Keep unsupported data integrations explicitly Planned until real functionality exists.

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

Debug validation passed the four requested Gradle tasks with 39 JVM tests and zero failures/errors. Lint has zero errors and 585 warnings. The debug APK is 58,604,984 bytes (about 55.9 MiB); compressed fonts account for 32,552,880 bytes. Do not mistake debug APK size for a measured Play delivery size. Font catalog/provenance and oversized/duplicate image assets need a deliberate follow-up without silently removing existing customization.

Icon mapping/pixel inventory and PNG fixture tests pass. Native alignment check intentionally exits 1. No device installation, instrumentation execution, screenshot set, frame-rate/battery benchmark or paid-product validation was performed. CI has been updated for API 36 and instrumentation compilation, but its remote run is not a local test result.

The initially uncommitted nullable-certificate change was retained exactly and committed separately as `2fa558d`; it was not discarded or silently mixed into another patch. Existing local Home-only access and completion-plan commits are also preserved in the published history. Task commits are enumerated in the full audit.

Final qualification addition: the combined five-task build (including `:app:assembleRelease`) passed after correcting misplaced icon keep metadata. Actual shrunk-APK inspection retained all 98 direct icon names. Unsigned/minified release APK: 36,716,372 bytes; checksum in the validation summary. The earlier release compile passed but lost 58 names, demonstrating why compile success was insufficient. CI now checks those names after an unsigned release build. Neither APK is a paid-release approval.
