package com.outervoid.axiom.ui.feed.modules

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.outervoid.axiom.feed.FeedModuleConfig

@Composable
fun NextActionModule(config: FeedModuleConfig, nextAction: String, onUpdate: (String) -> Unit) {
    FeedModuleCard(config) {
        OutlinedTextField(
            value = nextAction,
            onValueChange = onUpdate,
            label = { Text("Active next action") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2
        )
    }
}
