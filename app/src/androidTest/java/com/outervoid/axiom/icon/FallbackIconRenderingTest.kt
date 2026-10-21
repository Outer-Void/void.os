package com.outervoid.axiom.icon

import android.graphics.drawable.BitmapDrawable
import androidx.test.platform.app.InstrumentationRegistry
import com.outervoid.axiom.settings.IconPackOptions
import org.junit.Assert.*
import org.junit.Test

class FallbackIconRenderingTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun sameCategoryRetainsDistinctIdentityAndStablePixels() {
        val provider = AxiomFallbackIconProvider(context)
        val fallback = android.graphics.drawable.ColorDrawable(android.graphics.Color.WHITE)
        val alpha = (provider.iconFor("com.alpha.mobile", "BrowserActivity", fallback) as BitmapDrawable).bitmap
        val beta = (provider.iconFor("com.delta.mobile", "BrowserActivity", fallback) as BitmapDrawable).bitmap
        assertFalse(alpha.sameAs(beta))
        assertTrue(alpha.sameAs((provider.iconFor("com.alpha.mobile", "BrowserActivity", fallback) as BitmapDrawable).bitmap))
    }

    @Test fun systemSelectionPreservesOriginalDrawable() {
        val original = android.graphics.drawable.ColorDrawable(android.graphics.Color.GREEN)
        val provider = BundledVoidSysIconProvider(context, IconPackOptions.SystemPackage)
        assertSame(original, provider.iconFor("com.google.android.gm", null, original))
    }
}
