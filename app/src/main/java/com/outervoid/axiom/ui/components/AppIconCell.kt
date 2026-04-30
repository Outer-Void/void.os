package com.outervoid.axiom.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.outervoid.axiom.data.AppInfo

@Composable
fun AppIconCell(app: AppInfo, onClick: () -> Unit, onLongClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().combinedClickable(onClick = onClick, onLongClick = onLongClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(bitmap = app.icon.toBitmap(96, 96).asImageBitmap(), contentDescription = app.label, modifier = Modifier.height(56.dp))
        Text(app.label, maxLines = 1)
    }
}
