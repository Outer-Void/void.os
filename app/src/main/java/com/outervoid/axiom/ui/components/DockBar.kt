package com.outervoid.axiom.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.ui.theme.Amber
import com.outervoid.axiom.ui.theme.Border
import com.outervoid.axiom.ui.theme.Surface

@Composable
fun DockBar(
    onSettings: () -> Unit,
    onSearch: () -> Unit,
    onSystem: () -> Unit = {},
    compactMode: Boolean = false,
    accentColor: Color = Amber
) {
    val buttonSize = if (compactMode) 48.dp else 54.dp
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DockButton(onClick = onSettings, contentDescription = "Settings", size = buttonSize, accentColor = accentColor) {
            PowerDialIcon(it)
        }
        DockButton(onClick = onSearch, contentDescription = "Search apps", size = buttonSize, accentColor = accentColor) {
            GridDotsIcon(it)
        }
        DockButton(onClick = onSystem, contentDescription = "System", size = buttonSize, accentColor = accentColor) {
            RingIndicatorIcon(it)
        }
    }
}

@Composable
private fun DockButton(
    onClick: () -> Unit,
    contentDescription: String,
    size: androidx.compose.ui.unit.Dp,
    accentColor: Color,
    icon: @Composable (Color) -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Surface, RoundedCornerShape(16.dp))
            .border(1.dp, Border, RoundedCornerShape(16.dp))
            .clickable(onClickLabel = contentDescription, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        icon(accentColor)
    }
}

@Composable
private fun PowerDialIcon(color: Color) {
    Canvas(Modifier.size(24.dp)) {
        drawArc(
            color = color,
            startAngle = 138f,
            sweepAngle = 264f,
            useCenter = false,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
        drawLine(
            color = color,
            start = Offset(size.width / 2f, 2.dp.toPx()),
            end = Offset(size.width / 2f, size.height / 2.25f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun GridDotsIcon(color: Color) {
    Canvas(Modifier.size(24.dp)) {
        val radius = 2.dp.toPx()
        val step = size.width / 4f
        listOf(1f, 2f, 3f).forEach { x ->
            listOf(1f, 2f, 3f).forEach { y ->
                drawCircle(color = color, radius = radius, center = Offset(step * x, step * y))
            }
        }
    }
}

@Composable
private fun RingIndicatorIcon(color: Color) {
    Canvas(Modifier.size(24.dp)) {
        drawCircle(
            color = color,
            radius = size.minDimension * 0.36f,
            style = Stroke(width = 2.dp.toPx())
        )
        drawCircle(color = color, radius = 2.dp.toPx(), center = Offset(size.width * 0.72f, size.height * 0.28f))
    }
}
