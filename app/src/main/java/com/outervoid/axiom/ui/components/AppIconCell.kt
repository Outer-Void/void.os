package com.outervoid.axiom.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.outervoid.axiom.data.AppInfo
import com.outervoid.axiom.ui.iconshape.IconShape
import com.outervoid.axiom.ui.iconshape.iconShapeMask
import com.outervoid.axiom.ui.theme.BorderAlt
import com.outervoid.axiom.ui.theme.IconBackground
import com.outervoid.axiom.ui.theme.TextDim
import com.outervoid.axiom.ui.theme.TextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
    val iconBitmap by produceState<android.graphics.Bitmap?>(null, app.packageName, app.activityName, app.icon) {
        value = AppIconBitmapCache.get(app.icon)
            ?: withContext(Dispatchers.IO) {
                val renderDrawable = app.icon.constantState
                    ?.newDrawable()
                    ?.mutate()
                    ?: app.icon
                renderDrawable.toBitmap(96, 96)
            }
                .also { AppIconBitmapCache.put(app.icon, it) }
    }

    val shape = iconShapeMask(iconShape)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = if (compactMode) 2.dp else 5.dp, vertical = if (compactMode) 3.dp else 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BoxWithConstraints {
            val iconSize = maxWidth.coerceAtMost(if (compactMode) 50.dp else 60.dp)
            Box(
                modifier = Modifier
                    .size(iconSize)
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
                iconBitmap?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = app.label,
                        modifier = Modifier.fillMaxSize(iconScale.coerceIn(0.55f, 1.0f)),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
        if (showLabel) {
            Spacer(Modifier.size(if (compactMode) 4.dp else 7.dp))
            Text(
                text = app.label,
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 14.sp),
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

private object AppIconBitmapCache {
    private val bitmaps = object : android.util.LruCache<android.graphics.drawable.Drawable, android.graphics.Bitmap>(4 * 1024) {
        override fun sizeOf(key: android.graphics.drawable.Drawable, value: android.graphics.Bitmap): Int = value.byteCount / 1024
    }

    fun get(drawable: android.graphics.drawable.Drawable) = bitmaps.get(drawable)
    fun put(drawable: android.graphics.drawable.Drawable, bitmap: android.graphics.Bitmap) = bitmaps.put(drawable, bitmap)
}
