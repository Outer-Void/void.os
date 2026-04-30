package com.outervoid.axiom.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AxiomScheme = darkColorScheme(
    background = AxiomBackground,
    surface = AxiomSurface,
    onBackground = AxiomTextPrimary,
    onSurface = AxiomTextPrimary,
    primary = AxiomAmber,
    error = AxiomDanger,
    outline = AxiomBorder
)

@Composable
fun AxiomTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = AxiomScheme, typography = AxiomType, content = content)
}
