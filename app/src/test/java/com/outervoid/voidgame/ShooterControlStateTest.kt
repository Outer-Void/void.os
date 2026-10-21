package com.outervoid.voidgame

import org.junit.Assert.*
import org.junit.Test

class ShooterControlStateTest {
    @Test fun separateThumbsAndOneShotWeapons() {
        val input = ShooterControlState()
        assertEquals(ShooterTouchRole.Move, input.update(0, true, 100f, 200f))
        assertEquals(ShooterTouchRole.Missile, input.update(1, true, 250f, 40f))
        assertEquals(0, input.movementPointer)
        assertEquals(ShooterTouchRole.None, input.update(1, true, 320f, 40f))
        assertTrue(input.isPressed(ShooterTouchRole.Missile))
        assertFalse(input.isPressed(ShooterTouchRole.Bomb))
        input.update(1, false, 0f, 0f)
        assertEquals(ShooterTouchRole.Bomb, input.update(1, true, 320f, 40f))
    }

    @Test fun pauseQuarantinesTouchesAndClearsFeedback() {
        val input = ShooterControlState()
        input.update(0, true, 100f, 200f)
        input.update(1, true, 250f, 40f)
        input.quarantine()
        assertEquals(-1, input.movementPointer)
        assertFalse(input.isPressed(ShooterTouchRole.Missile))
        assertEquals(ShooterTouchRole.None, input.update(1, true, 250f, 40f))
        input.update(1, false, 0f, 0f)
        assertEquals(ShooterTouchRole.Missile, input.update(1, true, 250f, 40f))
    }

    @Test fun secondaryMovementThumbCanTakeOverAndLetterboxCannotFire() {
        val input = ShooterControlState()
        input.update(0, true, 100f, 200f)
        input.update(1, true, 180f, 200f)
        input.update(0, false, 0f, 0f)
        input.update(1, true, 190f, 200f)
        assertEquals(1, input.movementPointer)
        input.update(2, true, 400f, 20f)
        assertEquals(ShooterTouchRole.None, input.update(2, true, 320f, 40f))
        assertFalse(input.isPressed(ShooterTouchRole.Bomb))
    }
}
