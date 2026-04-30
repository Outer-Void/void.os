package com.outervoid.axiom.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.outervoid.axiom.ui.components.CornerBrackets
import com.outervoid.axiom.ui.components.SystemOnlineIndicator
import com.outervoid.axiom.ui.theme.Amber
import com.outervoid.axiom.ui.theme.AxiomClockStyle
import com.outervoid.axiom.ui.theme.AxiomDateStyle
import com.outervoid.axiom.ui.theme.Background
import com.outervoid.axiom.ui.theme.TextPrimary
import kotlinx.coroutines.delay

@Composable
fun AmbientScreen(date: String, onComplete: () -> Unit) {
    val lineProgress = androidx.compose.runtime.remember { Animatable(0f) }
    val watermarkAlpha = androidx.compose.runtime.remember { Animatable(0f) }
    val dateAlpha = androidx.compose.runtime.remember { Animatable(0f) }
    val statusAlpha = androidx.compose.runtime.remember { Animatable(0f) }
    val pulse by rememberInfiniteTransition(label = "ambient-dot")
        .animateFloat(
            initialValue = 0.35f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(520), RepeatMode.Reverse),
            label = "ambient-dot-alpha"
        )

    LaunchedEffect(Unit) {
        lineProgress.animateTo(1f, animationSpec = tween(800))
        delay(160)
        watermarkAlpha.animateTo(1f, animationSpec = tween(420))
        dateAlpha.animateTo(1f, animationSpec = tween(260))
        statusAlpha.animateTo(1f, animationSpec = tween(260))
        delay(460)
        onComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp)
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val x = size.width / 2f
            val top = 44.dp.toPx()
            val bottom = size.height - 88.dp.toPx()
            val endY = top + (bottom - top) * lineProgress.value
            drawLine(
                color = Amber,
                start = Offset(x, top),
                end = Offset(x, endY),
                strokeWidth = 1.5.dp.toPx()
            )
            if (lineProgress.value >= 1f) {
                drawCircle(
                    color = Amber.copy(alpha = pulse),
                    radius = 5.dp.toPx(),
                    center = Offset(x, bottom)
                )
                drawCircle(
                    color = Amber.copy(alpha = pulse * 0.25f),
                    radius = 12.dp.toPx(),
                    center = Offset(x, bottom),
                    style = Stroke(width = 1.dp.toPx())
                )
            }
        }
        Text(
            text = "A",
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 110.dp),
            color = TextPrimary.copy(alpha = 0.045f * watermarkAlpha.value),
            style = AxiomClockStyle.copy(fontSize = 240.sp, lineHeight = 240.sp),
            textAlign = TextAlign.Center
        )
        Text(
            text = date,
            modifier = Modifier.offset(x = 0.dp, y = 64.dp),
            color = TextPrimary.copy(alpha = dateAlpha.value),
            style = AxiomDateStyle
        )
        SystemOnlineIndicator(
            modifier = Modifier
                .offset(x = 0.dp, y = 92.dp)
                .graphicsLayer { alpha = statusAlpha.value }
        )
        CornerBrackets()
    }
}
