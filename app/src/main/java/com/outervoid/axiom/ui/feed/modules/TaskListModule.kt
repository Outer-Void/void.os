package com.outervoid.axiom.ui.feed.modules

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.feed.FeedModuleConfig
import com.outervoid.axiom.ui.theme.TextSecondary

@Composable
fun TaskListModule(config: FeedModuleConfig, tasks: List<String>, onUpdate: (List<String>) -> Unit) {
    FeedModuleCard(config) {
        tasks.take(5).forEachIndexed { index, task ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = task.startsWith("[x]"), onCheckedChange = { checked ->
                    val text = task.removePrefix("[x]").removePrefix("[ ]").trim()
                    onUpdate(tasks.toMutableList().also { it[index] = if (checked) "[x] $text" else "[ ] $text" })
                })
                Text(task.removePrefix("[x]").removePrefix("[ ]").trim(), color = TextSecondary, style = MaterialTheme.typography.bodySmall)
            }
        }
        OutlinedTextField(
            value = "",
            onValueChange = { value -> if (value.endsWith("\n") && value.trim().isNotEmpty()) onUpdate(tasks + "[ ] ${value.trim()}") },
            label = { Text("Add task") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1
        )
        Spacer(Modifier.height(4.dp))
        Text("Checklist data stays on device.", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
    }
}
