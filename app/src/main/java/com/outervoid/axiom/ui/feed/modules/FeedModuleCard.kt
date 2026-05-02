package com.outervoid.axiom.ui.feed.modules

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.feed.FeedModuleConfig
import com.outervoid.axiom.feed.FeedModuleStatus
import com.outervoid.axiom.ui.theme.Amber
import com.outervoid.axiom.ui.theme.Border
import com.outervoid.axiom.ui.theme.Surface
import com.outervoid.axiom.ui.theme.TextDim
import com.outervoid.axiom.ui.theme.TextPrimary
import com.outervoid.axiom.ui.theme.TextSecondary

@Composable
internal fun FeedModuleCard(
    config: FeedModuleConfig,
    modifier: Modifier = Modifier,
    status: String? = null,
    accent: Color = Amber,
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .background(Surface, RoundedCornerShape(7.dp))
            .border(1.dp, Border, RoundedCornerShape(7.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    config.titleOverride ?: config.type.label.uppercase(),
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    status ?: config.type.status.label(),
                    color = if (config.type.status == FeedModuleStatus.Functional) TextDim else accent,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            trailing?.invoke()
        }
        Spacer(Modifier.height(10.dp))
        content()
    }
}

@Composable
internal fun ModuleLine(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
        Text(value, color = TextPrimary, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
internal fun ModuleAction(label: String, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(label.uppercase(), color = Amber, style = MaterialTheme.typography.labelLarge)
    }
}

internal fun FeedModuleStatus.label(): String = when (this) {
    FeedModuleStatus.Functional -> "FUNCTIONAL"
    FeedModuleStatus.SetupRequired -> "SETUP REQUIRED"
    FeedModuleStatus.Planned -> "PLANNED"
}
