package com.outervoid.axiom.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.feed.FeedModuleConfig
import com.outervoid.axiom.feed.FeedState
import com.outervoid.axiom.ui.components.SettingsRow
import com.outervoid.axiom.ui.components.SettingsSection
import com.outervoid.axiom.ui.theme.Background
import com.outervoid.axiom.ui.theme.TextPrimary
import com.outervoid.axiom.ui.theme.TextSecondary

@Composable
fun FeedConfigurationScreen(
    feedState: FeedState,
    onToggleEnabled: (FeedModuleConfig) -> Unit,
    onToggleCollapsed: (FeedModuleConfig) -> Unit,
    onRemove: (FeedModuleConfig) -> Unit,
    onMoveUp: (FeedModuleConfig) -> Unit,
    onMoveDown: (FeedModuleConfig) -> Unit,
    onOpenLibrary: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 18.dp, vertical = 22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text("CONFIGURE FEED", color = TextPrimary, style = MaterialTheme.typography.titleLarge)
            Text("ACTIVE FEED LAYOUT", color = TextSecondary, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(14.dp))
            SettingsRow("Add Module", value = "OPEN", onClick = onOpenLibrary)
            SettingsSection("Active Feed Layout") {
                feedState.modules.sortedBy { it.position }.forEach { module ->
                    ModuleConfigRow(
                        module = module,
                        onToggleEnabled = { onToggleEnabled(module) },
                        onToggleCollapsed = { onToggleCollapsed(module) },
                        onRemove = { onRemove(module) },
                        onMoveUp = { onMoveUp(module) },
                        onMoveDown = { onMoveDown(module) }
                    )
                }
            }
            Spacer(Modifier.height(18.dp))
        }
    }
}

@Composable
private fun ModuleConfigRow(
    module: FeedModuleConfig,
    onToggleEnabled: () -> Unit,
    onToggleCollapsed: () -> Unit,
    onRemove: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    Column {
        SettingsRow(
            title = module.type.label,
            subtitle = "${module.type.category.label} / ${module.type.status.name.uppercase()}",
            value = if (module.enabled) "ON" else "OFF",
            onClick = onToggleEnabled
        )
        Row(Modifier.fillMaxWidth()) {
            SettingsRow("Up", modifier = Modifier.weight(1f), onClick = onMoveUp)
            SettingsRow("Down", modifier = Modifier.weight(1f), onClick = onMoveDown)
        }
        Row(Modifier.fillMaxWidth()) {
            SettingsRow(
                if (module.collapsed) "Expand" else "Collapse",
                modifier = Modifier.weight(1f),
                onClick = onToggleCollapsed
            )
            SettingsRow("Remove", modifier = Modifier.weight(1f), onClick = onRemove)
        }
        Spacer(Modifier.height(8.dp))
    }
}
