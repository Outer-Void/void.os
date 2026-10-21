package com.outervoid.axiom.ui.state

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LauncherNavigationPolicyTest {
    @Test
    fun testAmbientBackReturnsHome() {
        assertEquals(LauncherState.Home, LauncherNavigationPolicy.backTarget(LauncherState.Ambient))
    }

    @Test
    fun testHomeBackStaysOnHome() {
        assertNull(LauncherNavigationPolicy.backTarget(LauncherState.Home))
    }

    @Test
    fun testSecondaryScreensReturnHome() {
        LauncherState.values()
            .filter { it != LauncherState.Home }
            .forEach { state ->
                assertEquals("$state should return to Home", LauncherState.Home, LauncherNavigationPolicy.backTarget(state))
            }
    }
}
