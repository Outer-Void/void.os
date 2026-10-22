# AXIOM visual system — Task 009 contract

Source of truth: `ui/theme/AxiomColors.kt`, `AxiomType.kt`, `AxiomTheme.kt`; VOID.SYS canvas values live in `icon/VoidSysDesignTokens.kt`. Existing `docs/visual-system.md` includes older color values and dock descriptions: prefer current source. This increment does not claim full token adoption or completed screen review.

Current matte palette: Background #0B0B0B, BackgroundAlt/Surface #111111, SurfaceAlt #151515. TextPrimary #D8D8D8, TextSecondary #777777; TextDim #444444 must remain decorative until contrast is improved for meaningful content. Existing Amber #FFD000 is the selected active accent, Danger #E53935 denotes destructive/error states. Do not spread BrandBlue into unrelated controls. Selected font customization remains supported.

Target consolidation: spacing 4/8/12/16/24/32dp, radii 4/8/12/16dp, strokes 1dp structural and 2dp focus, minimum interactive targets 48dp, restrained 120/180/240ms transitions with reduced-motion support. These are design requirements pending implementation, not claims that every existing component follows them. Use semantic text/surface/error roles and visible focus/pressed/disabled states. Prefer compositional dark surfaces to decorative shadows or gradients.

Every route in the Task 009 feature manifest still needs actual screen capture/review at 320dp, phone and large display, increased font scale, IME open/closed, orientation/insets and TalkBack/keyboard focus. Embedded dock, folders, favorites, widget placement, wallpaper, entitlement, errors and loading/empty states require separate captures. Preserve identity and working controls; no screenshots were captured in this increment. Do not mark a route reviewed from its Kotlin file alone.
