package com.outervoid.axiom.ui.screens

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LauncherSwipePolicyTest {
    @Test
    fun testDoesNotLockBeforeTouchSlop() {
        assertNull(lockedSwipeAxis(dx = 7f, dy = 9f, touchSlop = 10f))
    }

    @Test
    fun testLocksToDominantAxisUsingCumulativeMovement() {
        assertEquals(SwipeAxis.Horizontal, lockedSwipeAxis(dx = 24f, dy = 8f, touchSlop = 10f))
        assertEquals(SwipeAxis.Vertical, lockedSwipeAxis(dx = 8f, dy = -24f, touchSlop = 10f))
    }

    @Test
    fun testDistanceUsesLockedAxis() {
        assertTrue(cumulativeSwipeDistance(-42f, 30f, SwipeAxis.Horizontal) == -42f)
        assertTrue(cumulativeSwipeDistance(-42f, 30f, SwipeAxis.Vertical) == 30f)
    }
}
