package com.outervoid.axiom.vm

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExternalIntentLaunchTest {
    @Test
    fun testReportsSuccessfulLaunch() {
        assertTrue(startActivitySafely(Intent("test")) { })
    }

    @Test
    fun testContainsMissingActivityAndSecurityFailures() {
        assertFalse(startActivitySafely(Intent("test")) { throw android.content.ActivityNotFoundException() })
        assertFalse(startActivitySafely(Intent("test")) { throw SecurityException() })
    }
}
