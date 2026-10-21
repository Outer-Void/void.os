package com.outervoid.voidgame

import org.junit.Assert.*
import org.junit.Test

class ShooterInputTest {
    @Test fun controlsDoNotOverlapAndIgnoreLetterboxTouches() {
        assertEquals(ShooterTouchRole.Move, shooterTouchRole(215f, 42f))
        assertEquals(ShooterTouchRole.Missile, shooterTouchRole(216f, 42f))
        assertEquals(ShooterTouchRole.Missile, shooterTouchRole(287f, 42f))
        assertEquals(ShooterTouchRole.Bomb, shooterTouchRole(288f, 42f))
        assertEquals(ShooterTouchRole.Move, shooterTouchRole(324f, 140f))
        assertEquals(ShooterTouchRole.Pause, shooterTouchRole(180f, 600f))
        assertEquals(ShooterTouchRole.None, shooterTouchRole(-2f, 42f))
        assertEquals(ShooterTouchRole.None, shooterTouchRole(180f, 650f))
    }

    @Test fun fixedClockMatchesAcrossFrameRatesAndBoundsStalls() {
        fun steps(frames: Int, delta: Float): Int {
            val clock = ShooterClock()
            var count = 0
            repeat(frames) { clock.advance(delta) { count++; true } }
            return count
        }
        assertEquals(60, steps(60, 1f / 60f))
        assertEquals(60, steps(30, 1f / 30f))
        assertEquals(60, steps(120, 1f / 120f))
        assertTrue(steps(1, 10f) <= 6)
        assertEquals(0, steps(1, Float.NaN))
    }

    @Test fun screenTransitionStopsCatchupImmediately() {
        var count = 0
        val clock = ShooterClock()
        clock.advance(0.1f) { count++; false }
        assertEquals(1, count)
        clock.advance(1f / 60f) { count++; true }
        assertEquals(2, count)
    }
}
