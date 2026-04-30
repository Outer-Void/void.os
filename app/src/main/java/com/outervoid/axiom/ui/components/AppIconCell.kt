package com.outervoid.axiom.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.outervoid.axiom.data.AppInfo
import com.outervoid.axiom.ui.theme.IconBackground
import com.outervoid.axiom.ui.theme.IconBorder
import com.outervoid.axiom.ui.theme.TextPrimary

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppIconCell(
    app: AppInfo,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    showLabel: Boolean = true,
    iconScale: Float = 0.72f,
    compactMode: Boolean = false
) {
    val iconBitmap = remember(app.packageName, app.activityName) {
        app.icon.toBitmap(96, 96).asImageBitmap()
    }

    Column(
        modifier = Modifier
            .aspectRatio(if (compactMode) 0.86f else 0.74f)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = if (compactMode) 2.dp else 4.dp, vertical = if (compactMode) 3.dp else 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(if (compactMode) 50.dp else 58.dp)
                .background(IconBackground, RoundedCornerShape(14.dp))
                .border(1.dp, IconBorder, RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                bitmap = iconBitmap,
                contentDescription = app.label,
                modifier = Modifier.fillMaxSize(iconScale.coerceIn(0.55f, 0.95f)),
                contentScale = ContentScale.Fit
            )
        }
        if (showLabel) {
            Spacer(Modifier.size(if (compactMode) 4.dp else 7.dp))
            Text(
                text = app.label,
                color = TextPrimary,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}
