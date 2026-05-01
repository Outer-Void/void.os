package com.outervoid.axiom.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.outervoid.axiom.ui.theme.Amber
import com.outervoid.axiom.ui.theme.TextPrimary

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AxiomWordmark(
    modifier: Modifier = Modifier,
    onLongPress: () -> Unit = {}
) {
    Row(
        modifier = modifier.combinedClickable(
            onClick = {},
            onLongClick = onLongPress
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("A X I", color = TextPrimary, style = MaterialTheme.typography.titleLarge)
        Text(" O ", color = Amber, style = MaterialTheme.typography.titleLarge)
        Text("M", color = TextPrimary, style = MaterialTheme.typography.titleLarge)
        Text(" •", color = Amber, style = MaterialTheme.typography.titleLarge)
    }
}
