package com.outervoid.axiom.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AxiomScheme = darkColorScheme(
    background = Background,
    inverseSurface = BackgroundAlt,
    surface = Surface,
    surfaceVariant = SurfaceAlt,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    primary = Amber,
    onPrimary = Background,
    secondary = BrandBlue,
    onSecondary = Background,
    error = Danger,
    onError = TextPrimary,
    outline = Border
)

@Composable
fun AxiomTheme(fontKey: String = com.outervoid.axiom.settings.AxiomSettings.DEFAULT_FONT_KEY, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = AxiomScheme, typography = axiomTypography(fontKey), content = content)
}
