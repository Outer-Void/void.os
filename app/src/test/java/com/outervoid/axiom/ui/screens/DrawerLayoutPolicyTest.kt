package com.outervoid.axiom.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Test

class DrawerLayoutPolicyTest {
    @Test fun narrowScreensAndLargeFontsReduceColumns() {
        assertEquals(3, drawerColumnCount(320, 1f, 6))
        assertEquals(2, drawerColumnCount(320, 2f, 6))
        assertEquals(3, drawerColumnCount(412, 1f, 3))
        assertEquals(6, drawerColumnCount(800, 1f, 6))
    }

    @Test fun gapAndPaddingBudgetFitsEverySupportedColumnCount() {
        for (width in listOf(280, 320, 360, 412, 600, 800)) {
            for (compact in listOf(false, true)) {
                val count = drawerColumnCount(width, 1f, 6, compact)
                val used = count * 68 + (count - 1) * (if (compact) 6 else 10) + (if (compact) 24 else 36)
                org.junit.Assert.assertTrue("width=$width compact=$compact columns=$count", used <= width)
            }
        }
        assertEquals(2, drawerColumnCount(412, 2f, 6))
        assertEquals(3, drawerColumnCount(320, Float.NaN, 6))
    }
}
