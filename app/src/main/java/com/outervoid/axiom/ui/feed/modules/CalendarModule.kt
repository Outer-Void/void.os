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
    FeedModuleCard(config, status = "SETUP REQUIRED", trailing = {
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
        Text("CALENDAR ACCESS REQUIRED", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        Text("Enable calendar access before AXIOM can show schedule data here.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
    }
}
