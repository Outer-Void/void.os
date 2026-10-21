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

## Task 008 incremental remediation

Baseline `d5bfb4a`, branch `feat/task-008-stabilization`. Existing Task 007 audits remain applicable except where superseded here.

- All category fallbacks now include deterministic identity monograms. Generic package suffixes (`mobile`, `client`, `app`, etc.) no longer override the meaningful name segment. Same initials can still collide; labels remain essential. This is not equivalent to dedicated artwork for every mapped application.
- Centralized Canvas palette/size/stroke tokens in `icon/VoidSysDesignTokens.kt`; gold is now muted `#C5A66A`. Accent strokes restore Paint state so subsequent silver linework keeps its intended weight/color.
- Added dedicated Calendar (date grid), Google Home (roof/door), and Notion (N/page) vectors: 101 direct targets, 446 generated targets across 1,908 entries. Old PNGs remain untouched. New names are explicitly retained in `res/raw/voidsys_keep.xml`.
- Provider-scoped resource-ID caches include misses and are recreated by each inventory refresh. Existing package broadcasts, coalescing, pack changes, fresh Drawable identities and bounded bitmap caches remain the invalidation mechanism. No global resource-ID cache is introduced.
- QA now detects duplicate resource variants within one qualifier folder, broken local drawable references, malformed component names, and identical decoded PNG pixels regardless of encoding metadata. Duplicate artwork remains a review finding, not an automatic deletion. XML vector-equivalence/perceptual similarity and arbitrary non-drawable resource references are not checked.
- Machine report: [Task 008 inventory](artifacts/task-008/voidsys-inventory.json). Human report: [summary](artifacts/task-008/voidsys-summary.md). Full [HTML contact sheet](artifacts/task-008/voidsys-contact-sheet.html) generated; not visually reviewed in full.
- Three new vectors **rendered and visually reviewed** at 48/60 pixels using local librsvg/cairo: [PNG preview](artifacts/task-008/voidsys-vector-preview.png). Clear source glyphs and consistent spacing observed. This does not verify Android rasterization, adaptive masking, fallback rendering or real installed-app recognition. Reproduce with `python3 app/tools/render_voidsys_vectors.py docs/audits/artifacts/task-008/voidsys-vector-preview.png`.

Regression additions: JVM monogram and resource-lookup lifetime tests; instrumentation tests for category identity pixels, repeat rendering and preservation of system selection. Instrumentation requires a connected device; compilation alone is not execution. Drawer column budgets now include actual gaps/padding and up to 200% font scaling; two-line fixed label counts preserved. Device QA at 280/320dp, landscape/insets and large inventories remains open.

Task 008 packaged release check: unsigned minified release built successfully; `aapt2 dump resources` retains **101/101 direct icon names** (zero missing), including all three new vectors. See [release log](artifacts/task-008/release-validation.txt) and [validation summary](artifacts/task-008/validation-summary.json).
