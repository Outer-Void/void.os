package com.outervoid.axiom.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.ui.components.CornerBrackets
import com.outervoid.axiom.ui.theme.Amber
import com.outervoid.axiom.ui.theme.Background
import com.outervoid.axiom.ui.theme.TextPrimary
import kotlinx.coroutines.delay

@Composable
fun AxiomShooterTransitionScreen(
    accentColor: Color = Amber,
    onLaunch: () -> Unit
) {
    val pulse = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        pulse.animateTo(1f, tween(950))
        delay(160)
        onLaunch()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val radius = size.minDimension * (0.08f + pulse.value * 0.76f)
            drawCircle(
                color = accentColor.copy(alpha = 0.28f * (1f - pulse.value)),
                radius = radius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )
            drawCircle(color = accentColor, radius = 5.dp.toPx(), center = center)
        }
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("AXIOM SHOOTER", color = accentColor, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text("SYSTEM ONLINE.", color = TextPrimary, style = MaterialTheme.typography.labelLarge)
        }
        CornerBrackets()
    }
}
