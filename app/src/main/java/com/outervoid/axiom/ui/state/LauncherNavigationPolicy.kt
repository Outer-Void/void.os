package com.outervoid.axiom.ui.state

/** Back always returns the launcher to its Home surface, including from Ambient. */
internal object LauncherNavigationPolicy {
    fun backTarget(current: LauncherState): LauncherState? =
        if (current == LauncherState.Home) null else LauncherState.Home
}
