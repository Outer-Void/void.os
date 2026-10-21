package com.outervoid.voidgame

import com.outervoid.voidgame.entities.Fragment
import com.outervoid.voidgame.entities.FragmentType
import org.junit.Assert.*
import org.junit.Test

class FragmentTest {
    @Test fun enemiesBelowShipCannotAcquireAnUpwardDiveAndStallWave() {
        val fragment = Fragment(100f, 90f, FragmentType.SHARD)
        // Repeated acquisition opportunities must never direct an enemy back upward.
        repeat(1000) { fragment.update(0f, 8, 180f, 124f) }
        assertFalse(fragment.diving)
        repeat(300) { fragment.update(1f / 60f, 8, 180f, 124f) }
        assertFalse(fragment.active)
    }

    @Test fun offscreenSpawnsCannotDiveBeforeEnteringCombatArea() {
        val fragment = Fragment(100f, 680f, FragmentType.SHARD)
        repeat(1000) { fragment.update(0f, 8, 180f, 124f) }
        assertFalse(fragment.diving)
    }
}
