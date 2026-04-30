package com.outervoid.axiom.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AxiomScheme = darkColorScheme(
    background = Background,
    surface = Surface,
    surfaceVariant = SurfaceAlt,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    primary = Amber,
    secondary = BrandBlue,
    error = Danger,
    outline = Border
)

@Composable
fun AxiomTheme(fontKey: String = com.outervoid.axiom.settings.AxiomSettings.DEFAULT_FONT_KEY, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = AxiomScheme, typography = axiomTypography(fontKey), content = content)
}
