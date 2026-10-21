package com.outervoid.axiom.ui.feed.modules

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.outervoid.axiom.feed.FeedModuleConfig
import com.outervoid.axiom.ui.theme.TextSecondary

@Composable
fun CalendarModule(config: FeedModuleConfig, context: Context) {
    FeedModuleCard(config, status = "CALENDAR SHORTCUT", trailing = {
        ModuleAction("Open") {
            runCatching {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW)
                        .setData(CalendarContract.CONTENT_URI.buildUpon().appendPath("time").build())
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        }
    }) {
        Text("OPEN SYSTEM CALENDAR", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        Text("Opens your calendar app. Event summaries are not implemented.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
    }
}
