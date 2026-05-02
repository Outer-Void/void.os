package com.outervoid.axiom.ui.feed.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.feed.FeedModuleConfig
import com.outervoid.axiom.feed.FeedModuleType
import com.outervoid.axiom.feed.FeedState
import com.outervoid.axiom.ui.theme.Amber
import com.outervoid.axiom.ui.theme.Background
import com.outervoid.axiom.ui.theme.TextSecondary
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun MinimalModules(config: FeedModuleConfig, state: FeedState) {
    FeedModuleCard(config) {
        when (config.type) {
            FeedModuleType.MinimalClock -> Text(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm")), color = Amber, style = MaterialTheme.typography.displaySmall)
            FeedModuleType.SystemDotIndicator -> Box(Modifier.size(12.dp).clip(CircleShape).background(Amber))
            FeedModuleType.FocusOnlyPanel -> Text(state.priorities.firstOrNull().orEmpty().ifBlank { "FOCUS UNSET" }, color = TextSecondary, style = MaterialTheme.typography.titleMedium)
            FeedModuleType.BlackScreenModule -> Box(Modifier.height(96.dp).background(Background))
            FeedModuleType.ObserveMode -> Text("OBSERVE MODE", color = TextSecondary, style = MaterialTheme.typography.titleMedium)
            else -> Text("MINIMAL", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
