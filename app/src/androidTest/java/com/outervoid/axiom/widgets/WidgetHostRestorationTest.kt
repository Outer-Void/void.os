package com.outervoid.axiom.widgets

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WidgetHostRestorationTest {
    @Test fun allocatedIdBelongsToRecreatedHostAndCanBeReleased() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val first = AxiomWidgetHost(context)
        val id = first.allocateAppWidgetId()
        try {
            val restored = AxiomWidgetHost(context)
            assertTrue(id in restored.appWidgetIds)
            restored.startListening()
            restored.stopListening()
        } finally {
            if (id in first.appWidgetIds) first.deleteAppWidgetId(id)
        }
        assertTrue(id !in AxiomWidgetHost(context).appWidgetIds)
    }
}
