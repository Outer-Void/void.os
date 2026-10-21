package com.outervoid.voidgame

import com.outervoid.voidgame.entities.Bullet
import com.outervoid.voidgame.entities.Carrier
import org.junit.Assert.*
import org.junit.Test

class CarrierTest {
    @Test fun fireRespectsCooldownAndPoolCapacity() {
        val carrier = Carrier()
        val bullets = arrayOf(Bullet())
        assertTrue(carrier.tryFire(bullets))
        assertFalse(carrier.tryFire(bullets))
        carrier.update(GameConfig.BULLET_COOLDOWN, null)
        assertFalse(carrier.tryFire(bullets))
        bullets[0].active = false
        assertTrue(carrier.tryFire(bullets))
    }

    @Test fun movementStaysOnScreenAndHitFeedbackExpires() {
        val carrier = Carrier()
        carrier.update(0f, -999f)
        assertEquals(0f, carrier.x, 0f)
        carrier.update(0f, 999f)
        assertEquals(GameConfig.WORLD_WIDTH - carrier.width, carrier.x, 0f)
        carrier.registerHit()
        assertTrue(carrier.damageTimer > 0f)
        carrier.update(1f, null)
        assertEquals(0f, carrier.damageTimer, 0f)
    }
}
