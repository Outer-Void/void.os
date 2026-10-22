package com.outervoid.axiom.ui.components

import org.junit.Assert.assertEquals
import org.junit.Test

class SystemMetricsTest {
    @Test
    fun systemRamUsesAvailableMemoryAndHandlesLargeDevices() {
        assertEquals(75, usedMemoryPercent(8L * 1024 * 1024 * 1024, 2L * 1024 * 1024 * 1024))
        assertEquals(20, usedMemoryPercent(100, 80))
        assertEquals(0, usedMemoryPercent(Long.MAX_VALUE, Long.MAX_VALUE))
        assertEquals(100, usedMemoryPercent(Long.MAX_VALUE, 0))
    }

    @Test
    fun malformedRamSamplesRemainUnavailable() {
        assertEquals(null, usedMemoryPercent(0, 0))
        assertEquals(null, usedMemoryPercent(100, -1))
        assertEquals(null, usedMemoryPercent(100, 101))
    }

    @Test
    fun largeBatteryScaleDoesNotOverflow() {
        assertEquals(100, batteryPercent(Int.MAX_VALUE, Int.MAX_VALUE))
        assertEquals(100, batteryPercent(Int.MAX_VALUE, 1))
    }
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
