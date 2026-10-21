package com.outervoid.axiom.ui.components

import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DockBarLayoutTest {
    @Test
    fun testDockFitsNarrowAndStandardWidths() {
        listOf(280.dp, 320.dp, 360.dp).forEach { width ->
            val (button, spacing) = dockLayoutMetrics(width, compactMode = false)
            assertTrue("dock should fit $width", button * 7 + spacing * 6 + 16.dp <= width)
        }
    }

    @Test
    fun testCompactModeDoesNotUseMoreSpaceThanAvailable() {
        val width = 220.dp
        val (button, spacing) = dockLayoutMetrics(width, compactMode = true)
        assertTrue(button * 7 + spacing * 6 + 12.dp <= width)
    }
}
