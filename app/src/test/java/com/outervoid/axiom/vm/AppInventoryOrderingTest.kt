package com.outervoid.axiom.vm

import org.junit.Assert.assertEquals
import org.junit.Test

class AppInventoryOrderingTest {
    @Test
    fun sortsLabelsAlphabeticallyWithoutChangingTies() {
        val result = AppInventoryOrdering.sort(listOf("zebra", "Axiom", "alpha")) { it }

        assertEquals(listOf("alpha", "Axiom", "zebra"), result)
    }
}
