# VOID.SYS icon acceptance specification

Keep recognizable application silhouettes within consistent graphite housings, pale metallic linework and restrained gold highlights. Balance optical mass and internal spacing at actual 48/60dp launcher sizes. Shared housing or fallback monograms do not constitute individual application artwork redesign.

Each asset variant is tracked in `reports/voidsys/icon-inventory.json`. Distinguish resource names, densities, direct mapped art, generated runtime fallback mappings and launcher adaptive icons. Duplicate hashes identify byte identity, not necessarily defects; density variants and intentional category families require documented decisions. Use source QA pixel groups to find re-encoded identical PNGs. Unmapped does not mean unused: direct XML/Kotlin references, adaptive compositions and dynamic lookups must be considered.

For every required upgrade: identify the application, review before art, author recognizable geometry, compare before/after at launcher sizes, validate transparency/clipping/centering, check package/component aliases and shrinker retention, then review Android runtime captures. Record reviewer/evidence and remaining issues. No Task 009 asset currently meets this entire gate. Existing Task 008 Calendar/Home/Notion source previews remain prior partial evidence only.

Use existing vector tooling for vector assets; no bitmap generation is needed for this inventory increment. Source contact sheets cover mapped targets; standalone launcher resources and runtime fallback compositions still require explicit review. A metadata inventory of 2,903 variants is not 2,903 visual improvements.
