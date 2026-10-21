package com.outervoid.axiom.ui.state

/** Restricts the hidden game transition to Home; Settings and other launcher routes cannot enter it. */
internal object ShooterAccessPolicy {
    fun hiddenEntryTarget(current: LauncherState): LauncherState? =
        LauncherState.AxiomShooterTransition.takeIf { current == LauncherState.Home }
}
