package com.outervoid.axiom.settings

import android.graphics.Typeface
import androidx.compose.ui.text.font.FontFamily

data class FontOption(
    val key: String,
    val displayName: String,
    val systemFamilyName: String
) {
    fun fontFamily(): FontFamily {
        val typeface = Typeface.create(systemFamilyName, Typeface.NORMAL)
        return FontFamily(typeface ?: Typeface.DEFAULT)
    }
}

object FontOptions {
    val All = listOf(
        FontOption("sans", "Default / Sans", "sans"),
        FontOption("serif", "Serif", "serif"),
        FontOption("monospace", "Monospace", "monospace"),
        FontOption("casual", "Casual", "casual"),
        FontOption("cursive", "Cursive", "cursive")
    )

    fun byKey(key: String): FontOption = All.firstOrNull { it.key == key } ?: All.first()
}
