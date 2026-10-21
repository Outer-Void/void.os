package com.outervoid.axiom.widgets

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WidgetOperationPolicyTest {
    @Test
    fun canceledWidgetOperationsReleaseTheirAllocatedId() {
        assertEquals(413, WidgetOperationPolicy.abandonedWidgetId(413, operationAccepted = false))
    }

    @Test
    fun acceptedOperationsAndMissingIdsNeedNoCleanup() {
        assertNull(WidgetOperationPolicy.abandonedWidgetId(413, operationAccepted = true))
        assertNull(WidgetOperationPolicy.abandonedWidgetId(null, operationAccepted = false))
    }
}
