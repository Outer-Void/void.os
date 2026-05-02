package com.outervoid.axiom.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.system.SystemAction
import com.outervoid.axiom.ui.theme.Amber
import com.outervoid.axiom.ui.theme.IconBackground
import com.outervoid.axiom.ui.theme.IconBorder
import com.outervoid.axiom.ui.theme.TextSecondary

@Composable
fun SystemActionTile(
    action: SystemAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compactMode: Boolean = false,
    accentColor: Color = Amber,
    active: Boolean = false
) {
    Column(
        modifier = modifier
            .aspectRatio(if (compactMode) 0.86f else 0.74f)
            .clickable(onClick = onClick)
            .padding(horizontal = if (compactMode) 2.dp else 5.dp, vertical = if (compactMode) 3.dp else 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(if (compactMode) 50.dp else 60.dp)
                .background(IconBackground, RoundedCornerShape(15.dp))
                .border(1.dp, if (active) accentColor else IconBorder, RoundedCornerShape(15.dp)),
            contentAlignment = Alignment.Center
        ) {
            SystemActionGlyph(action = action, color = if (active) accentColor else TextSecondary)
        }
        Spacer(Modifier.size(if (compactMode) 4.dp else 7.dp))
        Text(
            text = action.label,
            color = TextSecondary,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SystemActionGlyph(action: SystemAction, color: Color) {
    Canvas(Modifier.fillMaxSize(0.56f)) {
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        val c = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension / 3.2f
        when (action) {
            SystemAction.Wifi -> {
                drawArc(color, 220f, 100f, false, style = stroke)
                drawArc(color, 235f, 70f, false, topLeft = Offset(size.width * 0.22f, size.height * 0.28f), size = androidx.compose.ui.geometry.Size(size.width * 0.56f, size.height * 0.56f), style = stroke)
                drawCircle(color, 2.2.dp.toPx(), Offset(c.x, size.height * 0.72f))
            }
            SystemAction.Bluetooth -> {
                drawLine(color, Offset(c.x, 4.dp.toPx()), Offset(c.x, size.height - 4.dp.toPx()), strokeWidth = 2.dp.toPx())
                drawLine(color, Offset(c.x, 4.dp.toPx()), Offset(size.width * 0.72f, size.height * 0.28f), strokeWidth = 2.dp.toPx())
                drawLine(color, Offset(size.width * 0.72f, size.height * 0.28f), Offset(c.x, c.y), strokeWidth = 2.dp.toPx())
                drawLine(color, Offset(c.x, c.y), Offset(size.width * 0.72f, size.height * 0.72f), strokeWidth = 2.dp.toPx())
                drawLine(color, Offset(size.width * 0.72f, size.height * 0.72f), Offset(c.x, size.height - 4.dp.toPx()), strokeWidth = 2.dp.toPx())
            }
            SystemAction.Battery -> {
                drawRoundRect(color, topLeft = Offset(4.dp.toPx(), size.height * 0.28f), size = androidx.compose.ui.geometry.Size(size.width * 0.76f, size.height * 0.44f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()), style = stroke)
                drawLine(color, Offset(size.width * 0.85f, size.height * 0.42f), Offset(size.width * 0.85f, size.height * 0.58f), strokeWidth = 2.dp.toPx())
            }
            SystemAction.MobileNetwork,
            SystemAction.DataUsage -> drawBars(color)
            SystemAction.Display,
            SystemAction.Wallpaper -> {
                drawCircle(color, r, c, style = stroke)
                drawCircle(color = androidx.compose.ui.graphics.Color.Black, radius = r * 0.82f, center = Offset(c.x + r * 0.45f, c.y - r * 0.2f))
            }
            SystemAction.Flashlight -> {
                drawRoundRect(color, Offset(size.width * 0.36f, size.height * 0.14f), androidx.compose.ui.geometry.Size(size.width * 0.28f, size.height * 0.44f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx()), style = stroke)
                drawLine(color, Offset(size.width * 0.38f, size.height * 0.6f), Offset(size.width * 0.28f, size.height * 0.86f), strokeWidth = 2.dp.toPx())
                drawLine(color, Offset(size.width * 0.62f, size.height * 0.6f), Offset(size.width * 0.72f, size.height * 0.86f), strokeWidth = 2.dp.toPx())
            }
            SystemAction.Location -> {
                drawCircle(color, r, c, style = stroke)
                drawLine(color, Offset(c.x, 2.dp.toPx()), Offset(c.x, size.height * 0.25f), strokeWidth = 2.dp.toPx())
                drawLine(color, Offset(c.x, size.height * 0.75f), Offset(c.x, size.height - 2.dp.toPx()), strokeWidth = 2.dp.toPx())
                drawLine(color, Offset(2.dp.toPx(), c.y), Offset(size.width * 0.25f, c.y), strokeWidth = 2.dp.toPx())
                drawLine(color, Offset(size.width * 0.75f, c.y), Offset(size.width - 2.dp.toPx(), c.y), strokeWidth = 2.dp.toPx())
            }
            SystemAction.Hotspot -> {
                drawCircle(color, 2.2.dp.toPx(), c)
                drawCircle(color, r * 0.65f, c, style = stroke)
                drawCircle(color, r * 1.05f, c, style = stroke)
            }
            SystemAction.Nfc -> drawRoundRect(color, Offset(size.width * 0.22f, size.height * 0.2f), androidx.compose.ui.geometry.Size(size.width * 0.56f, size.height * 0.6f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx()), style = stroke)
            SystemAction.Accounts,
            SystemAction.DefaultApps,
            SystemAction.HomeSettings,
            SystemAction.SystemUpdate -> {
                drawArc(color, 35f, 230f, false, style = stroke)
                drawArc(color, 215f, 230f, false, style = stroke)
            }
            SystemAction.Airplane -> {
                drawLine(color, Offset(c.x, 3.dp.toPx()), Offset(c.x, size.height - 3.dp.toPx()), strokeWidth = 2.dp.toPx())
                drawLine(color, Offset(5.dp.toPx(), c.y), Offset(size.width - 5.dp.toPx(), c.y), strokeWidth = 2.dp.toPx())
                drawLine(color, Offset(c.x, size.height * 0.7f), Offset(size.width * 0.7f, size.height * 0.88f), strokeWidth = 2.dp.toPx())
                drawLine(color, Offset(c.x, size.height * 0.7f), Offset(size.width * 0.3f, size.height * 0.88f), strokeWidth = 2.dp.toPx())
            }
            SystemAction.DoNotDisturb -> {
                drawCircle(color, r, c, style = stroke)
                drawLine(color, Offset(c.x - r * 0.7f, c.y + r * 0.7f), Offset(c.x + r * 0.7f, c.y - r * 0.7f), strokeWidth = 2.dp.toPx())
            }
            SystemAction.Security -> {
                drawLine(color, Offset(c.x, 3.dp.toPx()), Offset(size.width * 0.78f, size.height * 0.24f), strokeWidth = 2.dp.toPx())
                drawLine(color, Offset(size.width * 0.78f, size.height * 0.24f), Offset(size.width * 0.68f, size.height * 0.72f), strokeWidth = 2.dp.toPx())
                drawLine(color, Offset(size.width * 0.68f, size.height * 0.72f), Offset(c.x, size.height - 3.dp.toPx()), strokeWidth = 2.dp.toPx())
                drawLine(color, Offset(c.x, size.height - 3.dp.toPx()), Offset(size.width * 0.32f, size.height * 0.72f), strokeWidth = 2.dp.toPx())
                drawLine(color, Offset(size.width * 0.32f, size.height * 0.72f), Offset(size.width * 0.22f, size.height * 0.24f), strokeWidth = 2.dp.toPx())
                drawLine(color, Offset(size.width * 0.22f, size.height * 0.24f), Offset(c.x, 3.dp.toPx()), strokeWidth = 2.dp.toPx())
            }
            SystemAction.Storage -> {
                drawOval(color, topLeft = Offset(size.width * 0.2f, size.height * 0.16f), size = androidx.compose.ui.geometry.Size(size.width * 0.6f, size.height * 0.2f), style = stroke)
                drawLine(color, Offset(size.width * 0.2f, size.height * 0.26f), Offset(size.width * 0.2f, size.height * 0.72f), strokeWidth = 2.dp.toPx())
                drawLine(color, Offset(size.width * 0.8f, size.height * 0.26f), Offset(size.width * 0.8f, size.height * 0.72f), strokeWidth = 2.dp.toPx())
                drawOval(color, topLeft = Offset(size.width * 0.2f, size.height * 0.62f), size = androidx.compose.ui.geometry.Size(size.width * 0.6f, size.height * 0.2f), style = stroke)
            }
            SystemAction.Users -> {
                drawCircle(color, r * 0.45f, Offset(size.width * 0.38f, size.height * 0.36f), style = stroke)
                drawCircle(color, r * 0.4f, Offset(size.width * 0.64f, size.height * 0.42f), style = stroke)
                drawArc(color, 200f, 140f, false, topLeft = Offset(size.width * 0.16f, size.height * 0.48f), size = androidx.compose.ui.geometry.Size(size.width * 0.48f, size.height * 0.36f), style = stroke)
            }
            SystemAction.Developer -> {
                drawLine(color, Offset(size.width * 0.35f, size.height * 0.28f), Offset(size.width * 0.18f, c.y), strokeWidth = 2.dp.toPx())
                drawLine(color, Offset(size.width * 0.18f, c.y), Offset(size.width * 0.35f, size.height * 0.72f), strokeWidth = 2.dp.toPx())
                drawLine(color, Offset(size.width * 0.65f, size.height * 0.28f), Offset(size.width * 0.82f, c.y), strokeWidth = 2.dp.toPx())
                drawLine(color, Offset(size.width * 0.82f, c.y), Offset(size.width * 0.65f, size.height * 0.72f), strokeWidth = 2.dp.toPx())
            }
            else -> {
                drawCircle(color, r, c, style = stroke)
                drawLine(color, Offset(c.x - r * 0.6f, c.y), Offset(c.x + r * 0.6f, c.y), strokeWidth = 2.dp.toPx())
                drawLine(color, Offset(c.x, c.y - r * 0.6f), Offset(c.x, c.y + r * 0.6f), strokeWidth = 2.dp.toPx())
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBars(color: Color) {
    val w = 3.dp.toPx()
    listOf(0.28f, 0.46f, 0.64f, 0.82f).forEachIndexed { index, x ->
        val top = size.height * (0.72f - index * 0.14f)
        drawLine(color, Offset(size.width * x, top), Offset(size.width * x, size.height * 0.82f), strokeWidth = w, cap = StrokeCap.Round)
    }
}
