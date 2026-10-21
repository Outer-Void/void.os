# AXIOM Shooter Task 007 QA

Status: source remediation with JVM regression coverage; no device playtest or GPU capture. Hidden Home-only entry and non-exported manifest activity remain. No Settings/dock/ordinary shortcut entry was added.

## Confirmed defects and fixes

| Severity | Source | Remediation |
| --- | --- | --- |
| P1 | `VoidGame.kt:setScreen/render` | Commit transitions after outgoing render returns. Dispose outgoing screen once; reject/dispose duplicate pending transitions. Forward pause/resume. Lifecycle regression uses fake screens to verify event ordering. |
| P1 | `screens/GameScreen.kt:drawShapes` | Draw power-up line glyphs in a Line pass rather than calling line in a Filled-only pass. Requires GL/device verification. |
| P1 | `GameScreen.kt`, `ScoreScreen.kt` | Restart and score retry preserve Survival/campaign mission/wave parameters. Stop fixed-step catch-up after completion. Scores insert the finished run once; separate Survival/mission keys and retain legacy Survival scores. |
| P1 | `ShooterInput.kt`, `GameScreen.kt` | Poll separate pointer ownership for movement, missile, bomb and pause. Support controls have non-overlapping 72-unit zones; ignored HUD/letterbox touches cannot slide into a weapon. Label primary fire AUTO FIRE; remove nonfunctional FIRE ring. Ship moved above control/health panels. |
| P1 | `entities/SupportMissile.kt` | Visible homing projectile travels before damage, one missile in flight, finite charge use, lost-target cancellation and wave cleanup. Bomb consumes no charge with no targets and clears enemy bullets. |
| P1 | `VoidGameActivity.kt` | Android 13+ Back posts to GL thread to pause gameplay; Home return finishes game activity on UI thread. Release entry additionally checks the fail-closed signature gate, rather than trusting source/expiry extras alone. |
| P1 | `entities/Fragment.kt` | Dive velocity is fixed on acquisition; hostiles pass through the acquired point instead of oscillating around it indefinitely after player moves away. |
| P2 | `ShooterClock` | Fixed 60 Hz simulation, maximum six steps/100ms accepted frame, drops long background stalls. Tests cover 30/60/120 Hz, nonfinite input and terminal transitions. Randomness remains unseeded; this is not a deterministic replay system. |
| P2 | `Carrier.kt`, `GameScreen.kt` | 650ms invulnerability matches damage feedback; firewall also protects contact/missed-hostile damage. Existing finite bullet/particle pools preserved. |
| P2 | `ShooterSettingsScreen.kt`, `ShipScreen.kt` | Remove ineffective audio/sensitivity/HUD options. Add persisted reduced effects that reduces particles and disables shake/full-screen combat tint/purge circles. Ship page shows actual rules instead of fictional stat bars. |
| P2 | `CampaignScreen.kt`, `StartScreen.kt`, `TrainingScreen.kt` | Correct first-mission description and stale version/settings subtitle. Training keeps intentional tap-to-fire and counts successful shots; clamp its frame delta. Survival grows wave rows to five; campaign keeps its configured limits. |

Visual changes are procedural missile/trail feedback, clearer separated controls, readable AUTO FIRE/PAUSE labels and truthful ship/settings presentation. Existing custom player/hostile silhouettes, stars and restrained dark palette are preserved. This is an incremental overhaul, not final art or balance approval.

## Regression evidence

JVM coverage includes clock, zones, actual Carrier firing/pool limits, projectile movement/arrival/cancellation, collision/charge/score rules and screen lifecycle. These tests do not prove GL shape correctness, touch behavior on physical hardware, campaign completion or layout quality. Scores remain local; no online leaderboard or purchase validation is simulated.

## Device acceptance checklist

- API 26, 33/34 and 36: Home long-press only; normal taps/swipes do not launch. External `am start` rejected. Release gated, debug intentionally unlocked.
- Physical Back and predictive Back: pause rather than accidental exit; start-menu Back returns Home; 20 repeated entry/exit loops without leaking renderers, audio/resources or leaving game activity running.
- Drag with first thumb; second thumb missile/bomb; pointer release/reorder; no firing duplicates while held; letterbox/system gesture edges and cutouts. Review target size on 320dp and tall FitViewport devices.
- Campaign all three missions: death/restart/retry preserves parameters, unlock writes once, completion ends the run; Survival 30-minute balance and frame/memory profile; Training shot count matches visible fire.
- Every power-up including firewall/contact, timeout, rapid/stacked pickup and purge transition. Verify missile target death, charge exhaustion, bomb empty/hostile-bullet-only use, collision/invulnerability feedback.
- Pause/resume, Home/background, screen off/on, rotation/config changes, GL context loss and process recreation. In-progress game is **not** persisted across process death; recreation returns to menu, persistent completed missions/scores remain.
- Reduced effects, contrast, legibility, blending, frame p95, battery use and TalkBack/accessibility alternatives. Reduced effects does not disable all backdrop/menu/hostile motion.

Remaining release risks: 4 KB native ELF alignment, no device/GPU tests, unmeasured controls/insets, insufficient combat/progression/balance coverage, no durable in-progress run restoration. PauseScreen is deprecated/unreachable legacy reference. Stock BitmapFont and several menu touch targets still need final typography/accessibility work. No audio toggle remains because audio is not implemented.

## Task 008 incremental remediation

- **P1 upward-dive wave stall:** a hostile below the ship could acquire a positive-Y dive and leave the screen indefinitely. Acquisition now requires a position above the carrier and inside the visible combat area. JVM tests cover below-ship and offscreen acquisition exclusion.
- **P1 pause ownership:** extracted `ShooterControlState`. Pause clears movement and active feedback, quarantines held touches until release, prevents support weapon reactivation on resume, and allows secondary movement pointer takeover. JVM tests exercise real ownership state, drag across weapons, release/repress and letterbox rejection.
- **P1 Home return:** every `VoidGame.exit()` now delegates to the existing Android launcher return/finish callback. Pause explicitly uses that path; score labels identify the destination. Campaign completion has a distinct `MISSION CLEAR` title; retry still preserves the configured mode/mission/waves.
- **P2 rendering:** explicit combat alpha blending; camera restored before HUD/pause rendering so shake does not move controls or distort subsequent input unprojection. Added filled graphite interceptor silhouette, silver/blue cockpit and muted engine insets beneath existing linework. Same ship appears in Training. Combined duplicate Line passes and removed training draw filters and missile/bomb temporary collections. This is not a proof of zero allocations: existing entity spawn/list iteration, HUD string/layout work and effects still require profiling.

The activity remains non-exported, Home entry remains policy-restricted, and no ordinary navigation entry or security bypass was added. Existing missile, bomb, cooldown, finite ammunition, power-up, campaign, Survival, Training, local score, fixed-step and deferred-disposal implementations are retained. No claim of full gameplay completion, balance approval, GL rendering verification or durable in-progress process-death restoration is made. All original device acceptance items remain open, particularly simultaneous pause/weapon touches, viewport target size, GL context loss and repeated exit resource cleanup.
