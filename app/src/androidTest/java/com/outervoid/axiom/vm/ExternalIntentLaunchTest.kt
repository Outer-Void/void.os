package com.outervoid.axiom.vm

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExternalIntentLaunchTest {
    @Test
    fun testReportsSuccessfulLaunch() {
        assertTrue(safeLaunch { })
    }

    @Test
    fun testContainsMissingActivityAndSecurityFailures() {
        assertFalse(safeLaunch { throw android.content.ActivityNotFoundException() })
        assertFalse(safeLaunch { throw SecurityException() })
    }
}
