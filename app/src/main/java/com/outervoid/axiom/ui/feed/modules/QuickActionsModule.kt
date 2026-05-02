package com.outervoid.axiom.ui.feed.modules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.feed.FeedModuleConfig

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuickActionsModule(
    config: FeedModuleConfig,
    actions: List<String>,
    onOpenSystemPanel: () -> Unit,
    onOpenAppDrawer: () -> Unit,
    onAction: (String) -> Unit
) {
    FeedModuleCard(config) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            actions.forEach { action ->
                ModuleAction(action.displayAction()) {
                    when (action) {
                        "SystemPanelShortcut" -> onOpenSystemPanel()
                        "AppDrawer" -> onOpenAppDrawer()
                        else -> onAction(action)
                    }
                }
            }
            if (actions.isEmpty()) {
                ModuleAction("App Drawer", onOpenAppDrawer)
            }
        }
    }
}

private fun String.displayAction(): String = when (this) {
    "Wifi" -> "Wi-Fi"
    "PhoneSettings" -> "Settings"
    "SystemPanelShortcut" -> "System"
    else -> this
}
