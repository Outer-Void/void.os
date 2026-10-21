package com.outervoid.voidgame

import com.outervoid.voidgame.entities.SupportMissile
import org.junit.Assert.*
import org.junit.Test

class SupportMissileTest {
    @Test fun projectileTravelsBeforeImpactAndCannotLaunchTwice() {
        val missile = SupportMissile()
        assertTrue(missile.launch(0f, 0f))
        assertFalse(missile.launch(20f, 20f))
        assertFalse(missile.advance(1f / 60f, 0f, 100f))
        assertEquals(7f, missile.y, .001f)
        assertTrue(missile.active)
        repeat(20) { missile.advance(1f / 60f, 0f, 100f) }
        assertFalse(missile.active)
        assertEquals(100f, missile.y, 0f)
        assertFalse(missile.advance(1f, 0f, 100f))
    }

    @Test fun lostTargetCanCancelAndRelaunch() {
        val missile = SupportMissile()
        missile.launch(1f, 2f)
        missile.cancel()
        assertFalse(missile.advance(.1f, 20f, 20f))
        assertTrue(missile.launch(3f, 4f))
        assertFalse(missile.advance(Float.NaN, 20f, 20f))
    }
}
