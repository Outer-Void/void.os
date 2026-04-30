package com.outervoid.axiom.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val AxiomFont = FontFamily.Monospace

val AxiomClockStyle = TextStyle(
    fontFamily = AxiomFont,
    fontWeight = FontWeight.Light,
    fontSize = 64.sp,
    lineHeight = 68.sp,
    letterSpacing = 0.sp
)

val AxiomDateStyle = TextStyle(
    fontFamily = AxiomFont,
    fontWeight = FontWeight.Normal,
    fontSize = 13.sp,
    lineHeight = 18.sp,
    letterSpacing = 1.4.sp
)

val AxiomStatusStyle = TextStyle(
    fontFamily = AxiomFont,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 1.2.sp
)

val AxiomAppLabelStyle = TextStyle(
    fontFamily = AxiomFont,
    fontWeight = FontWeight.Normal,
    fontSize = 11.sp,
    lineHeight = 14.sp,
    letterSpacing = 0.sp
)

val AxiomSectionLabelStyle = TextStyle(
    fontFamily = AxiomFont,
    fontWeight = FontWeight.SemiBold,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 2.sp
)

val AxiomSearchHintStyle = TextStyle(
    fontFamily = AxiomFont,
    fontWeight = FontWeight.Normal,
    fontSize = 22.sp,
    lineHeight = 28.sp,
    letterSpacing = 0.sp
)

private val BodyStyle = TextStyle(
    fontFamily = AxiomFont,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.sp
)

val AxiomType = Typography(
    displayLarge = AxiomClockStyle,
    displayMedium = AxiomClockStyle.copy(fontSize = 52.sp, lineHeight = 56.sp),
    displaySmall = AxiomClockStyle.copy(fontSize = 42.sp, lineHeight = 46.sp),
    headlineLarge = AxiomSearchHintStyle,
    headlineMedium = AxiomSearchHintStyle.copy(fontSize = 20.sp, lineHeight = 26.sp),
    headlineSmall = AxiomSectionLabelStyle,
    titleLarge = AxiomSectionLabelStyle,
    titleMedium = AxiomStatusStyle,
    titleSmall = AxiomStatusStyle,
    bodyLarge = BodyStyle,
    bodyMedium = BodyStyle,
    bodySmall = AxiomAppLabelStyle,
    labelLarge = AxiomStatusStyle,
    labelMedium = AxiomAppLabelStyle,
    labelSmall = AxiomAppLabelStyle.copy(fontSize = 10.sp, lineHeight = 12.sp)
)
