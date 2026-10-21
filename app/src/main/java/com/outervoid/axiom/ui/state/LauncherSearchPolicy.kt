package com.outervoid.axiom.ui.state

object LauncherSearchPolicy {
    fun shouldResetQuery(state: LauncherState): Boolean = state != LauncherState.AppDrawer
}
