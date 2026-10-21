package com.outervoid.axiom.ui.state

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ShooterAccessPolicyTest {
    @Test
    fun hiddenEntryOnlyTransitionsFromHome() {
        assertEquals(
            LauncherState.AxiomShooterTransition,
            ShooterAccessPolicy.hiddenEntryTarget(LauncherState.Home)
        )
        LauncherState.values()
            .filter { it != LauncherState.Home }
            .forEach { state -> assertNull("$state must not expose Shooter", ShooterAccessPolicy.hiddenEntryTarget(state)) }
    }
}
