package com.outervoid.axiom.ui.screens

import android.test.AndroidTestCase

class LauncherSwipePolicyTest : AndroidTestCase() {
    fun testDoesNotLockBeforeTouchSlop() {
        assertNull(lockedSwipeAxis(dx = 7f, dy = 9f, touchSlop = 10f))
    }

    fun testLocksToDominantAxisUsingCumulativeMovement() {
        assertEquals(SwipeAxis.Horizontal, lockedSwipeAxis(dx = 24f, dy = 8f, touchSlop = 10f))
        assertEquals(SwipeAxis.Vertical, lockedSwipeAxis(dx = 8f, dy = -24f, touchSlop = 10f))
    }

    fun testDistanceUsesLockedAxis() {
        assertEquals(-42f, cumulativeSwipeDistance(-42f, 30f, SwipeAxis.Horizontal))
        assertEquals(30f, cumulativeSwipeDistance(-42f, 30f, SwipeAxis.Vertical))
    }
}
