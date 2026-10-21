package com.outervoid.axiom.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Test

class DrawerLayoutPolicyTest {
    @Test fun narrowScreensAndLargeFontsReduceColumns() {
        assertEquals(4, drawerColumnCount(320, 1f, 6))
        assertEquals(2, drawerColumnCount(320, 2f, 6))
        assertEquals(3, drawerColumnCount(412, 1f, 3))
        assertEquals(6, drawerColumnCount(800, 1f, 6))
    }
}
