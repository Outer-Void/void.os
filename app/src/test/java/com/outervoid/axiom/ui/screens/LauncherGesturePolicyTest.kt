package com.outervoid.axiom.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LauncherGesturePolicyTest {
    @Test
    fun locksAfterCumulativeTouchSlopAndKeepsTheDominantDirection() {
        assertNull(lockedSwipeAxis(dx = 7f, dy = 9f, touchSlop = 10f))
        assertEquals(SwipeAxis.Horizontal, lockedSwipeAxis(dx = 24f, dy = 8f, touchSlop = 10f))
        assertEquals(SwipeAxis.Vertical, lockedSwipeAxis(dx = 8f, dy = -24f, touchSlop = 10f))
        assertEquals(-24f, cumulativeSwipeDistance(-24f, 30f, SwipeAxis.Horizontal))
        assertEquals(30f, cumulativeSwipeDistance(-24f, 30f, SwipeAxis.Vertical))
    }
}
