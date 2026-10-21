package com.outervoid.axiom.ui.state

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LauncherPolicyTest {
    @Test
    fun allNonHomeBackTargetsIncludingAmbientReturnHome() {
        assertEquals(LauncherState.Home, LauncherNavigationPolicy.backTarget(LauncherState.Ambient))
        LauncherState.values().filter { it != LauncherState.Home }.forEach { state ->
            assertEquals(LauncherState.Home, LauncherNavigationPolicy.backTarget(state))
        }
        assertNull(LauncherNavigationPolicy.backTarget(LauncherState.Home))
    }

    @Test
    fun drawerSearchPersistsOnlyWhileDrawerIsActive() {
        assertEquals(false, LauncherSearchPolicy.shouldResetQuery(LauncherState.AppDrawer))
        assertEquals(true, LauncherSearchPolicy.shouldResetQuery(LauncherState.Home))
    }
}
