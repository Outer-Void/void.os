# VOID.SYS Task 007 QA

Run `python3 app/tools/voidsys_qa.py` from the checkout. Exit 1 means a broken map/name/alias/keep invariant. PNG decoding and filter reversal have a dependency-free fixture test: `python3 -m unittest discover -s app/tools -p 'test_*.py'`.

## Results

- 1,908 entries, 547 mapped drawable names; 449 mapped names intentionally use generated category/monogram fallbacks, 98 use dedicated resources after this batch.
- Zero missing targets, duplicate JSON keys, invalid drawable names, conflicting normalized component aliases or direct targets missing shrinker keep rules. Zero density inconsistencies in PNG dimensions.
- 15 groups of byte-identical PNG artwork; no collisions among direct mapped PNG targets. Identical generic artwork remains a recognition limitation, not proof of per-app coverage.
- 36 unused-resource candidates, 19 generic-list-only names. These are review candidates, not deletion instructions. Direct Kotlin/XML references are checked; reflection/other dynamic contracts require manual review.
- Earlier resource inventories counted `.gitkeep` as a drawable; this validator excludes non-resource files.

[Machine inventory](artifacts/voidsys-inventory.json) includes alpha bounds, transparent fraction, bright-glyph bounds/centroids and bright-pixel fraction. These are heuristics. It does **not** certify contrast, optical centering, downsampled legibility or recognition.

[Contact sheet](artifacts/voidsys-contact-sheet.html) displays source PNGs and the new vectors at 48/60 CSS pixels. Open it within the repository so relative PNG references resolve. Generic PNG tiles are explicitly marked as replaced at runtime. This is not a runtime screenshot or review of generated Android Canvas fallbacks.

## Implemented

- `icon/IconMappingKeys.kt` normalizes relative/full activity names and prefers component overrides over package defaults. Bundled and external providers use the same policy. Regression tests cover activity overrides and package-only fallback.
- `ui/components/AppIconCell.kt` keys bitmap state by drawable identity so a refreshed item cannot display the old bitmap while conversion is pending. Existing IO conversion and 4 MiB bitmap LRU remain; generated glyph cache remains 2 MiB. Inventory source Drawables still require device memory profiling.
- Four authored vector glyphs: Gmail envelope/M, Termux prompt, Google Authenticator clock/return, Microsoft Authenticator shield/lock. Shared 108-unit canvas, rounded near-black tile, silver strokes and restrained muted-gold details. Maps and shrinker keep rules updated together. Existing PNG resources retained.
- `SearchScreen.kt` caps configured columns by width/font scale, preserves two-line labels, and adds truthful empty results. User selection remains an upper bound at small widths. System-icon mode and the existing custom VOID.SYS package contract remain intact; arbitrary third-party pack support is not claimed.

## Outstanding

P1: recognize common apps beyond the 98 dedicated targets; review these four vectors on actual devices before accepting them as final artwork. Review adaptive shape clipping and double tile framing. Existing bundled-first precedence over the optional standalone package remains the established contract; clarify that product behavior before changing it.

P2: QA all aliases with actual installed activities, package upgrade/removal, pack switching, locale changes, R8 resource lookup, bitmap memory, 320dp layouts, labels at 200% text and TalkBack. Run contrast measurements after final rendered colors, glyphs and sizes are approved. No mass replacement assets were generated.

## Release artifact regression discovered after source QA

The first unsigned R8/resource-shrunk APK compiled, but `aapt2 dump resources` showed **58 of 98 direct mapped names missing**. The earlier keep file was under `res/values/voidsys_keep.xml`; its root `tools:keep` attributes did not survive as shrinker metadata. The static existence/list check was insufficient. The file is moved to `res/raw/voidsys_keep.xml`, the documented keep-file location, and QA now accepts `--resource-dump` to fail on missing actual packaged names. CI builds the unsigned release and runs that check as well as source QA.

[Pre-fix APK finding](artifacts/release-before-keep-fix.json). A deliberate negative run against that artifact must exit 1; final-tree retention results are recorded in the validation summary. Official reference: [Android resource keep files](https://developer.android.com/topic/performance/app-optimization/customize-which-resources-to-keep). The old source-only report is superseded by this packaged-resource check.

Final packaged check: **98/98 direct icon names retained, zero missing**, after the raw keep-file relocation. The enhanced QA exits 0 for the rebuilt APK, and exits 1 with 58 missing names for the pre-fix APK. See [final task log](artifacts/final-validation.txt) and [validation summary](artifacts/validation-summary.json). This verifies resource-name retention, not device visual quality.
