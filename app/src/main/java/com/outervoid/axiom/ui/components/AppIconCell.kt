package com.outervoid.axiom.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.outervoid.axiom.data.AppInfo
import com.outervoid.axiom.ui.iconshape.IconShape
import com.outervoid.axiom.ui.iconshape.iconShapeMask
import com.outervoid.axiom.ui.theme.BorderAlt
import com.outervoid.axiom.ui.theme.IconBackground
import com.outervoid.axiom.ui.theme.TextDim
import com.outervoid.axiom.ui.theme.TextSecondary

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppIconCell(
    app: AppInfo,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    showLabel: Boolean = true,
    iconScale: Float = 0.72f,
    compactMode: Boolean = false,
    iconShape: IconShape = IconShape.ROUNDED_SQUARE
) {
    val iconBitmap = remember(app.packageName, app.activityName) {
        app.icon.toBitmap(96, 96).asImageBitmap()
    }

    val shape = iconShapeMask(iconShape)
    Column(
        modifier = Modifier
            .aspectRatio(if (compactMode) 0.86f else 0.74f)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = if (compactMode) 2.dp else 5.dp, vertical = if (compactMode) 3.dp else 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(if (compactMode) 50.dp else 60.dp)
                .background(IconBackground, shape)
                .border(1.dp, BorderAlt.copy(alpha = 0.72f), shape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(Modifier.fillMaxSize()) {
                drawLine(
                    color = BorderAlt.copy(alpha = 0.44f),
                    start = Offset(size.width * 0.18f, size.height * 0.18f),
                    end = Offset(size.width * 0.82f, size.height * 0.18f),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = TextDim.copy(alpha = 0.5f),
                    start = Offset(size.width * 0.14f, size.height * 0.86f),
                    end = Offset(size.width * 0.86f, size.height * 0.86f),
                    strokeWidth = 1.4.dp.toPx()
                )
                drawLine(
                    color = BorderAlt.copy(alpha = 0.36f),
                    start = Offset(size.width * 0.72f, size.height * 0.12f),
                    end = Offset(size.width * 0.9f, size.height * 0.28f),
                    strokeWidth = 0.9.dp.toPx()
                )
                drawLine(
                    color = TextDim.copy(alpha = 0.34f),
                    start = Offset(size.width * 0.88f, size.height * 0.28f),
                    end = Offset(size.width * 0.78f, size.height * 0.42f),
                    strokeWidth = 0.9.dp.toPx()
                )
            }
            Image(
                bitmap = iconBitmap,
                contentDescription = app.label,
                modifier = Modifier.fillMaxSize(iconScale.coerceIn(0.72f, 1.0f)),
                contentScale = ContentScale.Fit
            )
        }
        if (showLabel) {
            Spacer(Modifier.size(if (compactMode) 4.dp else 7.dp))
            Text(
                text = app.label,
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}
