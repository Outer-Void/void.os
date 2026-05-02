package com.outervoid.axiom.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.data.AppInfo
import com.outervoid.axiom.settings.AxiomSettings
import com.outervoid.axiom.settings.accentColor
import com.outervoid.axiom.ui.components.AppIconCell
import com.outervoid.axiom.ui.components.CornerBrackets
import com.outervoid.axiom.ui.theme.Background
import com.outervoid.axiom.ui.theme.TextPrimary
import com.outervoid.axiom.ui.theme.TextSecondary

@Composable
fun CustomFolderPanel(
    settings: AxiomSettings,
    title: String,
    subtitle: String,
    apps: List<AppInfo>,
    onLaunchApp: (AppInfo) -> Unit,
    onOpenAppInfo: (AppInfo) -> Unit
) {
    val accent = settings.accentColor()
    val haptics = LocalHapticFeedback.current
    fun haptic() {
        if (settings.hapticFeedbackEnabled) haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = if (settings.compactMode) 12.dp else 18.dp, vertical = if (settings.compactMode) 16.dp else 22.dp)
    ) {
        Column {
            Text(title.ifBlank { "CUSTOM FOLDER" }.uppercase(), color = accent, style = MaterialTheme.typography.titleLarge)
            Text(subtitle, color = TextSecondary, style = MaterialTheme.typography.labelLarge)
            Spacer(
                Modifier
                    .padding(top = 12.dp, bottom = 18.dp)
                    .width(104.dp)
                    .height(1.dp)
                    .background(accent)
            )
            if (apps.isEmpty()) {
                Text("Add selected apps here.", color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(settings.gridColumns),
                    verticalArrangement = Arrangement.spacedBy(if (settings.compactMode) 8.dp else 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(if (settings.compactMode) 6.dp else 10.dp)
                ) {
                    items(apps, key = { "${it.packageName}/${it.activityName}" }) { app ->
                        AppIconCell(
                            app = app,
                            onClick = { haptic(); onLaunchApp(app) },
                            onLongClick = { haptic(); onOpenAppInfo(app) },
                            showLabel = settings.showAppLabels,
                            iconScale = settings.iconScale,
                            compactMode = settings.compactMode,
                            iconShape = settings.iconShape
                        )
                    }
                }
            }
        }
        CornerBrackets(topPadding = 74.dp, bottomPadding = 36.dp, alpha = 0.58f)
    }
}
