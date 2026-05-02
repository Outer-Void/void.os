package com.outervoid.axiom.ui.feed.modules

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.feed.FeedModuleConfig
import com.outervoid.axiom.ui.theme.TextSecondary

@Composable
fun FocusPanelModule(config: FeedModuleConfig, priorities: List<String>, onUpdate: (List<String>) -> Unit) {
    FeedModuleCard(config) {
        Column {
            List(3) { index -> priorities.getOrNull(index).orEmpty() }.forEachIndexed { index, value ->
                OutlinedTextField(
                    value = value,
                    onValueChange = { changed ->
                        val next = List(3) { i -> priorities.getOrNull(i).orEmpty() }.toMutableList()
                        next[index] = changed
                        onUpdate(next)
                    },
                    label = { Text("Priority ${index + 1}") },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(6.dp))
            }
            Text("1-3 priorities, stored locally.", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
        }
    }
}
