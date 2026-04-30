package com.outervoid.axiom.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.data.AppInfo
import com.outervoid.axiom.settings.AxiomSettings
import com.outervoid.axiom.ui.components.AppIconCell
import com.outervoid.axiom.ui.components.CornerBrackets
import com.outervoid.axiom.ui.components.DockBar
import com.outervoid.axiom.ui.components.SystemOnlineIndicator
import com.outervoid.axiom.ui.theme.*
import com.outervoid.axiom.vm.LauncherViewModel

@Composable
fun HomeScreen(
    date: String,
    time: String,
    apps: List<AppInfo>,
    settings: AxiomSettings,
    vm: LauncherViewModel,
    onOpenSearch: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val ctx = LocalContext.current
    val haptics = LocalHapticFeedback.current
    var selected by remember { mutableStateOf<AppInfo?>(null) }
    val gridState = rememberLazyGridState()
    val accent = if (settings.amberAccentEnabled) Amber else TextSecondary
    val outerPadding = if (settings.compactMode) 12.dp else 18.dp
    fun haptic() {
        if (settings.hapticFeedbackEnabled) haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (settings.searchOnSwipeUp && dragAmount < -28f) {
                        haptic()
                        onOpenSearch()
                    }
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = outerPadding, vertical = if (settings.compactMode) 14.dp else 20.dp)
        ) {
            Text(date, color = TextSecondary, style = MaterialTheme.typography.titleSmall)
            Text(time, color = accent, style = MaterialTheme.typography.displayLarge)
            if (settings.showSystemOnline) {
                SystemOnlineIndicator()
            }
            Spacer(
                Modifier
                    .padding(top = if (settings.compactMode) 10.dp else 14.dp, bottom = if (settings.compactMode) 12.dp else 18.dp)
                    .width(104.dp)
                    .height(1.dp)
                    .background(accent)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(settings.gridColumns),
                modifier = Modifier.weight(1f),
                state = gridState,
                horizontalArrangement = Arrangement.spacedBy(if (settings.compactMode) 6.dp else 10.dp),
                verticalArrangement = Arrangement.spacedBy(if (settings.compactMode) 8.dp else 12.dp)
            ) {
                items(apps, key = { "${it.packageName}/${it.activityName}" }) { app ->
                    AppIconCell(
                        app = app,
                        onClick = {
                            haptic()
                            vm.launchApp(ctx, app)
                        },
                        onLongClick = {
                            haptic()
                            selected = app
                        },
                        showLabel = settings.showAppLabels,
                        iconScale = settings.iconScale,
                        compactMode = settings.compactMode
                    )
                }
            }
            Spacer(Modifier.height(if (settings.compactMode) 10.dp else 16.dp))
            DockBar(
                onSettings = { haptic(); onOpenSettings() },
                onSearch = { haptic(); onOpenSearch() },
                compactMode = settings.compactMode,
                accentColor = accent
            )
        }
        CornerBrackets()
    }

    selected?.let { app ->
        AlertDialog(
            onDismissRequest = { selected = null },
            confirmButton = {
                TextButton(
                    onClick = { vm.openAppInfo(ctx, app); selected = null },
                    colors = ButtonDefaults.textButtonColors(contentColor = Amber)
                ) {
                    Text("APP INFO", style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { vm.uninstallApp(ctx, app); selected = null },
                    colors = ButtonDefaults.textButtonColors(contentColor = Danger)
                ) {
                    Text("UNINSTALL", color = Danger, style = MaterialTheme.typography.labelLarge)
                }
            },
            title = { Text(app.label, style = MaterialTheme.typography.titleLarge) },
            text = { Text(app.packageName, style = MaterialTheme.typography.bodySmall) },
            containerColor = Surface,
            textContentColor = TextPrimary,
            titleContentColor = TextPrimary
        )
    }
}
