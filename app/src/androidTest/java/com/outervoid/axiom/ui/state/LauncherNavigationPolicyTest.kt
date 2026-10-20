package com.outervoid.axiom.ui.state

import android.test.AndroidTestCase

class LauncherNavigationPolicyTest : AndroidTestCase() {
    fun testAmbientBackReturnsHome() {
        assertEquals(LauncherState.Home, LauncherNavigationPolicy.backTarget(LauncherState.Ambient))
    }

    fun testHomeBackStaysOnHome() {
        assertNull(LauncherNavigationPolicy.backTarget(LauncherState.Home))
    }

    fun testSecondaryScreensReturnHome() {
        LauncherState.values()
            .filter { it != LauncherState.Home }
            .forEach { state ->
                assertEquals("$state should return to Home", LauncherState.Home, LauncherNavigationPolicy.backTarget(state))
            }
    }
}
