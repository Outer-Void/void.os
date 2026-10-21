package com.outervoid.axiom.vm

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppInventoryRefreshPolicyTest {
    @Test
    fun testRefreshesWhenInventoryHasNeverLoadedOrIsAtLeastThirtySecondsOld() {
        assertTrue(AppInventoryRefreshPolicy.isDue(0L, 30_000L))
        assertTrue(AppInventoryRefreshPolicy.isDue(10_000L, 40_000L))
    }

    @Test
    fun testSkipsRecentSuccessfulRefresh() {
        assertFalse(AppInventoryRefreshPolicy.isDue(10_000L, 39_999L))
    }
}
