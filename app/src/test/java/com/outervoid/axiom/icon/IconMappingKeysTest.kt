package com.outervoid.axiom.icon

import org.junit.Assert.assertEquals
import org.junit.Test

class IconMappingKeysTest {
    @Test fun activityOverrideWinsAndRelativeNamesNormalize() {
        val mappings = mapOf("example.app" to "default", "example.app/.Main" to "specific")
        for (activity in listOf(".Main", "example.app.Main")) {
            val keys = IconMappingKeys.candidates("example.app", activity)
            assertEquals("specific", keys.firstNotNullOfOrNull { mappings[it] })
            assertEquals("example.app/example.app.Main", keys.first())
        }
    }

    @Test fun packageFallbackWorksWithoutActivity() {
        assertEquals(listOf("example.app"), IconMappingKeys.candidates(" example.app ", " "))
    }
}
