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
            Text("USER SELECTABLE CONTROL MODULES", color = TextSecondary, style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(14.dp))
            FeedModuleCategory.entries.forEach { category ->
                SettingsSection(category.label) {
                    FeedModuleType.entries
                        .filter { it.category == category }
                        .forEach { type ->
                            SettingsRow(
                                title = type.label,
                                subtitle = type.status.name.uppercase(),
                                value = if (type in activeTypes) "ADDED" else "ADD",
                                onClick = { onAddModule(type) }
                            )
                        }
                }
            }
            Spacer(Modifier.height(18.dp))
        }
    }
}
