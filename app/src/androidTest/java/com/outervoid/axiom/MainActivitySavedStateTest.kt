package com.outervoid.axiom

import android.os.Bundle
import android.os.Parcel
import android.test.AndroidTestCase

class MainActivitySavedStateTest : AndroidTestCase() {
    fun testPendingWidgetOperationSurvivesBundleRoundTrip() {
        val pendingOperation = 2 to 413
        val savedState = Bundle().apply {
            putSerializable(PENDING_WIDGET_OPERATION, pendingOperation)
        }
        val parcel = Parcel.obtain()
        try {
            parcel.writeBundle(savedState)
            parcel.setDataPosition(0)
            val restoredState = parcel.readBundle(javaClass.classLoader)!!

            assertEquals(pendingOperation, restoredState.getSerializable(PENDING_WIDGET_OPERATION))
        } finally {
            parcel.recycle()
        }
    }

    companion object {
        private const val PENDING_WIDGET_OPERATION = "pending_widget_operation"
    }
}
