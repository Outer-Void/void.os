package com.outervoid.axiom.settings

import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import com.outervoid.axiom.ui.iconshape.IconShape
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsDecoderTest {
    @Test
    fun preferencesDecodeAndPersistConfiguredValues() {
        val preferences = preferencesOf(
            SettingsKeys.FavoritesCount to 18,
            SettingsKeys.HomePanelCount to 3,
            SettingsKeys.IconShape to IconShape.CIRCLE.name,
            SettingsKeys.GridColumns to 5,
            SettingsKeys.CompactMode to true
        )

        val settings = decodeAxiomSettings(preferences)

        assertEquals(18, settings.favoritesCount)
        assertEquals(3, settings.homePanelCount)
        assertEquals(IconShape.CIRCLE, settings.iconShape)
        assertEquals(5, settings.gridColumns)
        assertTrue(settings.compactMode)
    }

    @Test
    fun malformedAndOutOfRangePreferencesFallBackSafely() {
        val preferences = preferencesOf(
            stringPreferencesKey("favorites_count") to "wrong-type",
            SettingsKeys.IconShape to "not-a-shape",
            SettingsKeys.HomePanelCount to 99,
            SettingsKeys.GridColumns to 1,
            SettingsKeys.IconScale to Float.NaN,
            SettingsKeys.FavoritesLocked to false
        )

        val settings = decodeAxiomSettings(preferences)

        assertEquals(10, settings.favoritesCount)
        assertEquals(IconShape.ROUNDED_SQUARE, settings.iconShape)
        assertEquals(5, settings.homePanelCount)
        assertEquals(3, settings.gridColumns)
        assertEquals(0.72f, settings.iconScale)
        assertFalse(settings.favoritesLocked)
    }

    @Test
    fun iconScaleSanitizerRejectsNonFiniteValuesAndClampsFiniteValues() {
        assertEquals(0.72f, sanitizeIconScale(Float.NaN))
        assertEquals(0.72f, sanitizeIconScale(Float.POSITIVE_INFINITY))
        assertEquals(0.95f, sanitizeIconScale(2f))
    }
}
