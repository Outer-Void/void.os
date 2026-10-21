package com.outervoid.axiom.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WidgetPickerPolicyTest {
    @Test
    fun providersSortByDisplayLabelWithoutChangingItems() {
        val providers = listOf("Zeta" to "z", "alpha" to "a", "Beta" to "b")

        assertEquals(
            listOf("alpha", "Beta", "Zeta"),
            sortWidgetProviders(providers) { it.first }.map { it.first }
        )
    }

    @Test
    fun onlyLatestProviderQueryMayPublish() {
        assertTrue(shouldPublishWidgetProviders(queryVersion = 4, currentVersion = 4))
        assertFalse(shouldPublishWidgetProviders(queryVersion = 3, currentVersion = 4))
    }
}
