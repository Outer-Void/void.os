package com.outervoid.axiom.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.outervoid.axiom.ui.theme.TextDim
import com.outervoid.axiom.ui.theme.TextPrimary
import com.outervoid.axiom.ui.theme.TextSecondary

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmptyWidgetPanel(
    panelNumber: Int,
    onOpenWidgetPicker: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .combinedClickable(onLongClick = onOpenWidgetPicker, onClick = {}),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("WIDGET PANEL", color = TextPrimary, style = MaterialTheme.typography.titleLarge)
            Text("Long press to add widget.", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
            Text("PANEL $panelNumber", color = TextDim, style = MaterialTheme.typography.labelSmall)
        }
    }
}
