package com.outervoid.axiom.ui.components

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertTrue
import org.junit.Test

class DockBarLayoutTest {
    @Test
    fun standardAndCompactDockWidthsStayInsideTheViewport() {
        listOf(280.dp, 320.dp, 360.dp).forEach { width ->
            val (button, spacing) = dockLayoutMetrics(width, compactMode = false)
            assertTrue(button * 7 + spacing * 6 + 16.dp <= width)
        }
        val compactWidth = 220.dp
        val (button, spacing) = dockLayoutMetrics(compactWidth, compactMode = true)
        assertTrue(button * 7 + spacing * 6 + 12.dp <= compactWidth)
    }
}
