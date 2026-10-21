package com.outervoid.axiom.vm

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SafeLaunchTest {
    @Test
    fun reportsSuccessAndContainsExpectedLaunchFailures() {
        assertTrue(safeLaunch { })
        assertFalse(safeLaunch { throw SecurityException("blocked") })
        assertFalse(safeLaunch { throw IllegalStateException("no handler") })
    }
}
