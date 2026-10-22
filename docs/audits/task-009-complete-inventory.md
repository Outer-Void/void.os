# Task 009 — live repository reconciliation and inventory

Baseline: `feat/task-008-stabilization`, `e78e28f`. Initial working tree clean. All twelve recent commits and repository AGENTS.md inspected. No archive imported; Tasks 005–008 and P1–P3 history retained. Work starts on `feat/task-009-reliability-inventory`.

This is the first coherent Task 009 increment, **not full-product completion**. Source indexing is complete for the baseline tracked paths; behavioral review and visual acceptance remain partial. Existing Task 007/008 reports are prior evidence, not fresh test results.

## Reproducible discovery

Run `python3 app/tools/task009_inventory.py`. Outputs:

- [Repository index](../../reports/task-009/repository-inventory.json): tracked paths, Kotlin/Java declarations, control sites with line numbers, routes and game screens. Includes resources, fonts, native binaries, security, CI, Gradle, documentation and test paths.
- [Feature dispositions](../../reports/task-009/completion.json): all 19 routes, nine game screens and cross-cutting systems. Existing dispositions survive inventory regeneration.
- [Every drawable/mipmap variant](../../reports/voidsys/icon-inventory.json): 2,903 records, including 2,892 PNGs, nine vectors and two adaptive icons. Each includes qualified path, dimensions, alpha metadata, hash, mapping keys/count, duplicate group and explicit pending QA/upgrade/verification status. Density variants are individual records, not individually redesigned icons.
- [Mapping/resource QA](../../reports/voidsys/source-qa/voidsys-inventory.json) and [contact sheet](../../reports/voidsys/source-qa/voidsys-contact-sheet.html). Source preview generation does not establish visual review.

The baseline contains 3,186 tracked files and 160 Kotlin source/test files. No Java files are present. The final index also includes this increment's new files once staged. Generated build outputs are excluded by Git discovery.

## Actual destinations

Launcher: Ambient, Home, AxiomFeed, AppDrawer, Settings, FeedConfiguration, FeedModuleLibrary, SystemPanel, LeftFolder, RightFolder, HiddenApps, About, Contact, WidgetPicker, LeftFolderApps, RightFolderApps, AxiomShooterTransition, PermissionSetup, TerminalPanel. Favorites, dock, widgets, wallpaper and error/locked/empty/loading states are embedded controls or overlays, not separate enum routes. Purchase/entitlement is scaffolding rather than a working purchase screen. Do not create a decorative purchase flow.

Shooter: CampaignScreen, GameScreen, LockedModuleScreen, PauseScreen, ScoreScreen, ShipScreen, ShooterSettingsScreen, StartScreen, TrainingScreen. Modes: Campaign, Survival, Training. No audio playback system/assets found. Campaign and Survival share GameScreen; training is separate safe shooting practice without targets. The complete control-site index is discovery evidence, not control execution.

## Source review dispositions

| Subsystem | Existing condition | This increment |
| --- | --- | --- |
| Inventory/icons/drawer | Task 008 coalescing, fallbacks, resource retention and layout fixes present; device unverified | Source metadata and full per-variant dispositions; no art change |
| Terminal | Start/write on UI, read-modify-write output race, broad read-error suppression, stale reader/stream cleanup risk | IO ownership, bounded output, atomic updates, generation guards, visible IO errors, shell regressions |
| Metrics | RAM showed JVM heap; battery multiplication could overflow | ActivityManager system memory, safe multiplication and unit regressions |
| System panel | One-second refresh continued while composed but Activity stopped | STARTED lifecycle ownership |
| Widgets/state/navigation | Prior policies and saved-state tests present; transaction/process-death matrix unresolved | Indexed; no new acceptance claim |
| Shooter | Prior fixed-step/input/weapon/progression work present | Indexed; no new gameplay/graphics work |
| Security | Release attestation/entitlement incomplete and fail closed | Preserved; external contract required |
| Native compatibility | Prior 64-bit ELF 4 KB alignment defect | Re-audit required; no dependency churn |
| Visual system/accessibility | Existing tokens and selective prior artwork; broad screen review pending | Current design rules recorded; no runtime screenshots |

See [bugs](task-009-bug-report.md), [validation](../release/task-009-validation.md) and [remaining work](../release/remaining-work.md). This inventory does not silently classify untouched screens/assets as verified.
