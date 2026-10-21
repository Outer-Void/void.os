package com.outervoid.axiom.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import com.outervoid.axiom.ui.theme.Amber
import com.outervoid.axiom.ui.theme.Border
import com.outervoid.axiom.ui.theme.BorderAlt
import com.outervoid.axiom.ui.theme.BorderDim
import com.outervoid.axiom.ui.theme.Surface
import com.outervoid.axiom.ui.theme.SurfaceAlt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DockBar(
    onPhone: () -> Unit,
    onLeftFolder: () -> Unit,
    onSettings: () -> Unit,
    onLongClickSettings: () -> Unit = {},
    onAppDrawer: () -> Unit,
    onSystem: () -> Unit = {},
    onLongClickSystem: () -> Unit = {},
    onRightFolder: () -> Unit,
    onMessages: () -> Unit,
    compactMode: Boolean = false,
    accentColor: Color = Amber
) {
    val padH = if (compactMode) 6.dp else 8.dp
    val padV = if (compactMode) 6.dp else 8.dp

    BoxWithConstraints(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        val (buttonSize, spacing) = dockLayoutMetrics(maxWidth, compactMode)
        Row(
            modifier = Modifier
            .background(SurfaceAlt, RoundedCornerShape(20.dp))
            .border(1.dp, BorderDim, RoundedCornerShape(20.dp))
                .padding(horizontal = padH, vertical = padV),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DockButton(onClick = onPhone, contentDescription = "Phone", size = buttonSize, accentColor = accentColor) {
                PhoneIcon(it)
            }
            DockButton(onClick = onLeftFolder, contentDescription = "Left custom folder", size = buttonSize, accentColor = accentColor) {
                FolderIcon(it)
            }
            DockButton(
                onClick = onSettings,
                onLongClick = onLongClickSettings,
                contentDescription = "Settings",
                size = buttonSize,
                accentColor = accentColor
            ) {
                PowerDialIcon(it)
            }
            DockButton(
                onClick = onAppDrawer,
                contentDescription = "App drawer",
                size = buttonSize,
                accentColor = accentColor,
                focal = true
            ) {
                GridDotsIcon(it)
            }
            DockButton(
                onClick = onSystem,
                onLongClick = onLongClickSystem,
                contentDescription = "System",
                size = buttonSize,
                accentColor = accentColor
            ) {
                RingIndicatorIcon(it)
            }
            DockButton(onClick = onRightFolder, contentDescription = "Right custom folder", size = buttonSize, accentColor = accentColor) {
                FolderIcon(it)
            }
            DockButton(onClick = onMessages, contentDescription = "Messages", size = buttonSize, accentColor = accentColor) {
                MessagesIcon(it)
            }
        }
    }
}

internal fun dockLayoutMetrics(maxWidth: Dp, compactMode: Boolean): Pair<Dp, Dp> {
    val preferredButtonSize = if (compactMode) 36.dp else 40.dp
    val preferredSpacing = if (compactMode) 4.dp else 5.dp
    val horizontalPadding = if (compactMode) 6.dp else 8.dp
    val spacing = minOf(preferredSpacing, maxWidth / 42)
    val buttonSize = minOf(
        preferredButtonSize,
        (maxWidth - horizontalPadding * 2 - spacing * 6).coerceAtLeast(0.dp) / 7
    )
    return buttonSize to spacing
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DockButton(
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    contentDescription: String,
    size: androidx.compose.ui.unit.Dp,
    accentColor: Color,
    focal: Boolean = false,
    icon: @Composable (Color) -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(Surface, RoundedCornerShape(14.dp))
            .border(1.dp, Border, RoundedCornerShape(14.dp))
            .combinedClickable(
                onClickLabel = contentDescription,
                role = Role.Button,
                onLongClick = onLongClick,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(Modifier.size(size)) {
            drawLine(
                color = BorderAlt.copy(alpha = 0.42f),
                start = Offset(size.toPx() * 0.22f, size.toPx() * 0.2f),
                end = Offset(size.toPx() * 0.78f, size.toPx() * 0.2f),
                strokeWidth = 1.dp.toPx()
            )
            drawRoundRect(
                color = BorderDim.copy(alpha = 0.7f),
                topLeft = Offset(2.dp.toPx(), this.size.height - 5.dp.toPx()),
                size = Size(this.size.width - 4.dp.toPx(), 2.dp.toPx())
            )
        }
        icon(accentColor)
        if (focal) {
            Canvas(Modifier.size(size)) {
                drawCircle(
                    color = accentColor,
                    radius = 2.dp.toPx(),
                    center = Offset(size.toPx() / 2f, this.size.height - 6.dp.toPx())
                )
            }
        }
    }
}

@Composable
private fun PhoneIcon(color: Color) {
    Canvas(Modifier.size(18.dp)) {
        val w = size.width
        val h = size.height
        val stroke = 1.6.dp.toPx()
        // Handset shape: arc + handle
        drawArc(
            color = color,
            startAngle = 200f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(w * 0.05f, h * 0.05f),
            size = androidx.compose.ui.geometry.Size(w * 0.9f, h * 0.6f),
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
        drawLine(color, Offset(w * 0.15f, h * 0.55f), Offset(w * 0.15f, h * 0.88f), stroke, StrokeCap.Round)
        drawLine(color, Offset(w * 0.85f, h * 0.55f), Offset(w * 0.85f, h * 0.88f), stroke, StrokeCap.Round)
        drawLine(color, Offset(w * 0.15f, h * 0.88f), Offset(w * 0.85f, h * 0.88f), stroke, StrokeCap.Round)
    }
}

@Composable
private fun MessagesIcon(color: Color) {
    Canvas(Modifier.size(18.dp)) {
        val w = size.width
        val h = size.height
        val stroke = 1.6.dp.toPx()
        val r = 3.dp.toPx()
        // Chat bubble
        drawRoundRect(
            color = color,
            topLeft = Offset(w * 0.06f, h * 0.08f),
            size = androidx.compose.ui.geometry.Size(w * 0.88f, h * 0.62f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(r),
            style = Stroke(width = stroke)
        )
        // Tail
        drawLine(color, Offset(w * 0.20f, h * 0.70f), Offset(w * 0.10f, h * 0.92f), stroke, StrokeCap.Round)
        drawLine(color, Offset(w * 0.10f, h * 0.92f), Offset(w * 0.38f, h * 0.70f), stroke, StrokeCap.Round)
        // Dots inside bubble
        val dotR = 1.4.dp.toPx()
        drawCircle(color, dotR, Offset(w * 0.33f, h * 0.39f))
        drawCircle(color, dotR, Offset(w * 0.50f, h * 0.39f))
        drawCircle(color, dotR, Offset(w * 0.67f, h * 0.39f))
    }
}

@Composable
private fun PowerDialIcon(color: Color) {
    Canvas(Modifier.size(18.dp)) {
        drawArc(
            color = color, startAngle = 138f, sweepAngle = 264f, useCenter = false,
            style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round)
        )
        drawLine(color, Offset(size.width / 2f, 2.dp.toPx()), Offset(size.width / 2f, size.height / 2.25f),
            1.6.dp.toPx(), StrokeCap.Round)
    }
}

@Composable
private fun GridDotsIcon(color: Color) {
    Canvas(Modifier.size(18.dp)) {
        val radius = 1.8.dp.toPx()
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
    Canvas(Modifier.size(18.dp)) {
        drawCircle(color = color, radius = size.minDimension * 0.36f, style = Stroke(width = 1.6.dp.toPx()))
        drawCircle(color = color, radius = 1.8.dp.toPx(), center = Offset(size.width * 0.72f, size.height * 0.28f))
    }
}

@Composable
private fun FolderIcon(color: Color) {
    Canvas(Modifier.size(18.dp)) {
        val stroke = 1.6.dp.toPx()
        val top = 5.dp.toPx(); val left = 2.dp.toPx(); val tabRight = 8.dp.toPx()
        val tabBottom = 8.dp.toPx(); val right = size.width - 2.dp.toPx(); val bottom = size.height - 3.dp.toPx()
        drawLine(color, Offset(left, tabBottom), Offset(left, bottom), stroke)
        drawLine(color, Offset(left, bottom), Offset(right, bottom), stroke)
        drawLine(color, Offset(right, bottom), Offset(right, tabBottom), stroke)
        drawLine(color, Offset(right, tabBottom), Offset(tabRight, tabBottom), stroke)
        drawLine(color, Offset(tabRight, tabBottom), Offset(tabRight - 2.dp.toPx(), top), stroke)
        drawLine(color, Offset(tabRight - 2.dp.toPx(), top), Offset(left, top), stroke)
        drawLine(color, Offset(left, top), Offset(left, tabBottom), stroke)
    }
}
