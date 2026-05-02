package com.outervoid.axiom.ui.feed.modules

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.outervoid.axiom.feed.FeedModuleConfig
import com.outervoid.axiom.ui.theme.TextSecondary

@Composable
fun WeatherModule(config: FeedModuleConfig) {
    FeedModuleCard(config, status = "SETUP REQUIRED") {
        Text(
            "WEATHER SETUP REQUIRED",
            color = TextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            "Add a manual location or weather provider in a later setup pass. No API keys are bundled.",
            color = TextSecondary,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
