package com.outervoid.axiom

import android.os.Bundle
import android.os.Parcel
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivitySavedStateTest {
    @Test
    fun testPendingWidgetOperationSurvivesBundleRoundTrip() {
        val pendingBind = 2 to 413
        val pendingConfigure = 1 to 414
        val savedState = Bundle().apply {
            putSerializable(PENDING_WIDGET_BIND, pendingBind)
            putSerializable(PENDING_WIDGET_CONFIGURE, pendingConfigure)
        }
        val parcel = Parcel.obtain()
        try {
            parcel.writeBundle(savedState)
            parcel.setDataPosition(0)
            val restoredState = parcel.readBundle(javaClass.classLoader)!!

            assertEquals(pendingBind, restoredState.getSerializable(PENDING_WIDGET_BIND))
            assertEquals(pendingConfigure, restoredState.getSerializable(PENDING_WIDGET_CONFIGURE))
        } finally {
            parcel.recycle()
        }
    }

    companion object {
        private const val PENDING_WIDGET_BIND = "pending_widget_bind"
        private const val PENDING_WIDGET_CONFIGURE = "pending_widget_configure"
    }
}
