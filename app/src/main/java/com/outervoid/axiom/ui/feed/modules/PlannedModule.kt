package com.outervoid.axiom.ui.feed.modules

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.outervoid.axiom.feed.FeedModuleConfig
import com.outervoid.axiom.ui.theme.TextSecondary

@Composable
fun PlannedModule(config: FeedModuleConfig) {
    FeedModuleCard(config, status = "PLANNED") {
        Text("PLANNED / V1.1", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        Text("Available in the module library without pretending live functionality is complete.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
    }
}
