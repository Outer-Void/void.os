package com.outervoid.axiom.ui.feed.modules

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.outervoid.axiom.feed.FeedModuleConfig
import com.outervoid.axiom.ui.theme.Amber
import com.outervoid.axiom.ui.theme.TextSecondary
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ClockPanelModule(config: FeedModuleConfig) {
    val now = LocalDateTime.now()
    FeedModuleCard(config) {
        Text(now.format(DateTimeFormatter.ofPattern("HH:mm", Locale.US)), color = Amber, style = MaterialTheme.typography.displayMedium)
        Text(now.format(DateTimeFormatter.ofPattern("EEE, MMM d", Locale.US)).uppercase(Locale.US), color = TextSecondary, style = MaterialTheme.typography.labelLarge)
    }
}
