package com.outervoid.axiom.ui.state

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LauncherSearchPolicyTest {
    @Test
    fun testQueryIsPreservedWhileDrawerRemainsActive() {
        assertFalse(LauncherSearchPolicy.shouldResetQuery(LauncherState.AppDrawer))
    }

    @Test
    fun testQueryResetsAfterClosingOrNavigatingAway() {
        listOf(LauncherState.Home, LauncherState.Settings, LauncherState.LeftFolder)
            .forEach { state -> assertTrue(LauncherSearchPolicy.shouldResetQuery(state)) }
    }
}
