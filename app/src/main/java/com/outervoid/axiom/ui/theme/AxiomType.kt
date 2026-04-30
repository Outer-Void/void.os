package com.outervoid.axiom.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.outervoid.axiom.settings.AxiomSettings
import com.outervoid.axiom.settings.FontOptions

fun axiomFontFamily(fontKey: String = AxiomSettings.DEFAULT_FONT_KEY) = FontOptions.byKey(fontKey).fontFamily()

fun axiomClockStyle(fontKey: String = AxiomSettings.DEFAULT_FONT_KEY) = TextStyle(
    fontFamily = axiomFontFamily(fontKey),
    fontWeight = FontWeight.Light,
    fontSize = 64.sp,
    lineHeight = 68.sp,
    letterSpacing = 0.sp
)

fun axiomDateStyle(fontKey: String = AxiomSettings.DEFAULT_FONT_KEY) = TextStyle(
    fontFamily = axiomFontFamily(fontKey),
    fontWeight = FontWeight.Normal,
    fontSize = 13.sp,
    lineHeight = 18.sp,
    letterSpacing = 1.4.sp
)

fun axiomStatusStyle(fontKey: String = AxiomSettings.DEFAULT_FONT_KEY) = TextStyle(
    fontFamily = axiomFontFamily(fontKey),
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 1.2.sp
)

fun axiomAppLabelStyle(fontKey: String = AxiomSettings.DEFAULT_FONT_KEY) = TextStyle(
    fontFamily = axiomFontFamily(fontKey),
    fontWeight = FontWeight.Normal,
    fontSize = 11.sp,
    lineHeight = 14.sp,
    letterSpacing = 0.sp
)

fun axiomSectionLabelStyle(fontKey: String = AxiomSettings.DEFAULT_FONT_KEY) = TextStyle(
    fontFamily = axiomFontFamily(fontKey),
    fontWeight = FontWeight.SemiBold,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 2.sp
)

fun axiomSearchHintStyle(fontKey: String = AxiomSettings.DEFAULT_FONT_KEY) = TextStyle(
    fontFamily = axiomFontFamily(fontKey),
    fontWeight = FontWeight.Normal,
    fontSize = 22.sp,
    lineHeight = 28.sp,
    letterSpacing = 0.sp
)

private val BodyStyle = TextStyle(
    fontFamily = axiomFontFamily(),
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.sp
)

val AxiomClockStyle = axiomClockStyle()
val AxiomDateStyle = axiomDateStyle()
val AxiomStatusStyle = axiomStatusStyle()
val AxiomAppLabelStyle = axiomAppLabelStyle()
val AxiomSectionLabelStyle = axiomSectionLabelStyle()
val AxiomSearchHintStyle = axiomSearchHintStyle()

fun axiomTypography(fontKey: String = AxiomSettings.DEFAULT_FONT_KEY): Typography {
    val clock = axiomClockStyle(fontKey)
    val search = axiomSearchHintStyle(fontKey)
    val section = axiomSectionLabelStyle(fontKey)
    val status = axiomStatusStyle(fontKey)
    val appLabel = axiomAppLabelStyle(fontKey)
    val body = BodyStyle.copy(fontFamily = axiomFontFamily(fontKey))

    return Typography(
        displayLarge = clock,
        displayMedium = clock.copy(fontSize = 52.sp, lineHeight = 56.sp),
        displaySmall = clock.copy(fontSize = 42.sp, lineHeight = 46.sp),
        headlineLarge = search,
        headlineMedium = search.copy(fontSize = 20.sp, lineHeight = 26.sp),
        headlineSmall = section,
        titleLarge = section,
        titleMedium = status,
        titleSmall = status,
        bodyLarge = body,
        bodyMedium = body,
        bodySmall = appLabel,
        labelLarge = status,
        labelMedium = appLabel,
        labelSmall = appLabel.copy(fontSize = 10.sp, lineHeight = 12.sp)
    )
}

val AxiomType = axiomTypography()
