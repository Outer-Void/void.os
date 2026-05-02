package com.outervoid.axiom.ui.feed.modules

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.outervoid.axiom.feed.FeedModuleConfig

@Composable
fun NotesModule(config: FeedModuleConfig, notes: List<String>, onUpdate: (List<String>) -> Unit) {
    FeedModuleCard(config) {
        notes.take(4).forEachIndexed { index, note ->
            OutlinedTextField(
                value = note,
                onValueChange = { changed -> onUpdate(notes.toMutableList().also { it[index] = changed }) },
                label = { Text("Note ${index + 1}") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )
        }
        ModuleAction("Add note") { onUpdate(notes + "") }
    }
}
