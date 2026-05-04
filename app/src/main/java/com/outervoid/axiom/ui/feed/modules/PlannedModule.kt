package com.outervoid.axiom.ui.feed.modules

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.outervoid.axiom.feed.FeedModuleConfig
import com.outervoid.axiom.ui.theme.TextSecondary

@Composable
fun PlannedModule(config: FeedModuleConfig) {
    FeedModuleCard(config, status = "FUTURE MODULE") {
        Text("V2.0 MODULE", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        Text("Shown only in the future-module section without pretending live functionality is complete.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
    }
}
