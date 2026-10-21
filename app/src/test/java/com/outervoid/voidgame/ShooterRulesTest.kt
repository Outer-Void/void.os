package com.outervoid.voidgame

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShooterRulesTest {
    @Test
    fun clampsFrameStepAndIgnoresNegativeTime() {
        assertEquals(1f / 30f, ShooterRules.frameStep(0.2f), 0.0001f)
        assertEquals(0f, ShooterRules.frameStep(-1f), 0f)
        assertEquals(0.016f, ShooterRules.frameStep(0.016f), 0.0001f)
    }

    @Test
    fun collisionUsesStrictRectangleOverlap() {
        assertTrue(ShooterRules.overlaps(0f, 0f, 10f, 10f, 9f, 9f, 4f, 4f))
        assertFalse(ShooterRules.overlaps(0f, 0f, 10f, 10f, 10f, 0f, 4f, 4f))
    }

    @Test
    fun scoresRemainDescendingAndLimited() {
        assertEquals(listOf(120L, 90L, 80L), ShooterRules.topScores(listOf(80, 120, 40), 90, 3))
        assertEquals(emptyList<Long>(), ShooterRules.topScores(listOf(2), 1, 0))
    }

    @Test
    fun campaignAdvancesOnlyAtConfiguredWaveLimit() {
        assertFalse(ShooterRules.campaignMissionComplete(4, 5))
        assertTrue(ShooterRules.campaignMissionComplete(5, 5))
        assertFalse(ShooterRules.campaignMissionComplete(5, 0))
    }

    @Test
    fun weaponChargesCannotFallBelowZero() {
        assertEquals(2, ShooterRules.useWeaponCharge(3))
        assertEquals(0, ShooterRules.useWeaponCharge(1))
        assertEquals(null, ShooterRules.useWeaponCharge(0))
    }
}
