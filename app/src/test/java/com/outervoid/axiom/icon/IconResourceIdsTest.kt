package com.outervoid.axiom.icon

import org.junit.Assert.*
import org.junit.Test

class IconResourceIdsTest {
    @Test fun resolvesEachNameOnceIncludingMissingResources() {
        var calls = 0
        val ids = IconResourceIds { calls++; if (it == "known") 42 else 0 }
        repeat(4) {
            assertEquals(42, ids.resolve("known"))
            assertEquals(0, ids.resolve("missing"))
        }
        assertEquals(2, calls)
    }

    @Test fun newInventoryDoesNotReuseIdsOrNegativeHitsFromOldPack() {
        var resource = 0
        val oldInventory = IconResourceIds { resource }
        assertEquals(0, oldInventory.resolve("app"))
        resource = 73
        val refreshedInventory = IconResourceIds { resource }
        assertEquals(73, refreshedInventory.resolve("app"))
        assertEquals(0, oldInventory.resolve("app"))
    }
}
