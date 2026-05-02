package com.outervoid.axiom.ui.feed.modules

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.outervoid.axiom.feed.FeedModuleConfig
import com.outervoid.axiom.feed.FeedModuleType
import com.outervoid.axiom.ui.theme.TextSecondary

@Composable
fun MetricsModule(config: FeedModuleConfig, context: Context) {
    val notificationModule = config.type == FeedModuleType.NotificationLoad
    FeedModuleCard(config, status = "SETUP REQUIRED", trailing = {
        ModuleAction("Setup") {
            val action = if (notificationModule) "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS" else Settings.ACTION_USAGE_ACCESS_SETTINGS
            runCatching { context.startActivity(Intent(action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
        }
    }) {
        Text(
            if (notificationModule) "NOTIFICATION ACCESS REQUIRED" else "USAGE ACCESS REQUIRED",
            color = TextSecondary,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            if (notificationModule) {
                "Enable notification access for AXIOM to summarize notification load."
            } else {
                "Enable usage access for AXIOM to show behavior metrics."
            },
            color = TextSecondary,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
