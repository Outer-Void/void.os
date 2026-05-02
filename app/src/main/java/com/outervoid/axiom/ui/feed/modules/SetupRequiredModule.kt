package com.outervoid.axiom.ui.feed.modules

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.outervoid.axiom.feed.FeedModuleConfig
import com.outervoid.axiom.ui.theme.TextSecondary

@Composable
fun SetupRequiredModule(config: FeedModuleConfig, onSetup: () -> Unit) {
    FeedModuleCard(config, status = "SETUP REQUIRED", trailing = { ModuleAction("Setup", onSetup) }) {
        Text("${config.type.label.uppercase()} SETUP REQUIRED", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        Text("This module needs Android permission or user configuration before it can show real data.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
    }
}
