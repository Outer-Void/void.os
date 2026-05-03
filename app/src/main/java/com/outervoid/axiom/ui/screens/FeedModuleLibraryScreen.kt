package com.outervoid.axiom.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.feed.FeedModuleCategory
import com.outervoid.axiom.feed.FeedModuleStatus
import com.outervoid.axiom.feed.FeedModuleType
import com.outervoid.axiom.feed.FeedState
import com.outervoid.axiom.ui.components.SettingsRow
import com.outervoid.axiom.ui.components.SettingsSection
import com.outervoid.axiom.ui.theme.Background
import com.outervoid.axiom.ui.theme.TextPrimary
import com.outervoid.axiom.ui.theme.TextSecondary

@Composable
fun FeedModuleLibraryScreen(
    feedState: FeedState,
    onAddModule: (FeedModuleType) -> Unit
) {
    val activeTypes = feedState.modules.map { it.type }.toSet()
    val activeModules = FeedModuleType.entries.filter { it.status != FeedModuleStatus.Planned }
    val plannedModules = FeedModuleType.entries.filter { it.status == FeedModuleStatus.Planned }

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
            Text("MODULE LIBRARY", color = TextPrimary, style = MaterialTheme.typography.titleLarge)
            Text("V1.0 ACTIVE MODULES", color = TextSecondary, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(14.dp))

            FeedModuleCategory.entries.forEach { category ->
                val modulesInCategory = activeModules.filter { it.category == category }
                if (modulesInCategory.isEmpty()) return@forEach
                SettingsSection(category.label) {
                    modulesInCategory.forEach { type ->
                        SettingsRow(
                            title = type.label,
                            subtitle = when (type.status) {
                                FeedModuleStatus.Functional -> "Functional"
                                FeedModuleStatus.SetupRequired -> "Setup required — tap to enable then grant permission"
                                FeedModuleStatus.Planned -> ""
                            },
                            value = if (type in activeTypes) "ADDED" else "ADD",
                            onClick = { onAddModule(type) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
            Text("FUTURE MODULES (v2.0)", color = TextSecondary, style = MaterialTheme.typography.titleSmall)
            Text("Not available in v1.0. Listed for roadmap visibility.", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
            Spacer(Modifier.height(10.dp))

            FeedModuleCategory.entries.forEach { category ->
                val planned = plannedModules.filter { it.category == category }
                if (planned.isEmpty()) return@forEach
                SettingsSection(category.label) {
                    planned.forEach { type ->
                        SettingsRow(
                            title = type.label,
                            subtitle = "Planned — v2.0",
                            value = "v2.0"
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
        }
    }
}
