package com.outervoid.axiom.ui.components

import org.junit.Assert.assertEquals
import org.junit.Test

class SystemMetricsTest {
    @Test
    fun batteryUpdatesAreConvertedAndBounded() {
        assertEquals(50, batteryPercent(level = 1, scale = 2))
        assertEquals(100, batteryPercent(level = 110, scale = 100))
        assertEquals(null, batteryPercent(level = -1, scale = 100))
    }

    @Test
    fun unavailableBatteryValuesKeepExistingPlaceholderFormatting() {
        assertEquals(null, batteryPercent(level = 50, scale = 0))
        assertEquals("--", formatMetricPercent(null))
        assertEquals("73%", formatMetricPercent(73))
    }
}
