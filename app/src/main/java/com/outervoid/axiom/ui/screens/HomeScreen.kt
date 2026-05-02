package com.outervoid.axiom.ui.screens

import android.appwidget.AppWidgetManager
import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.outervoid.axiom.data.AppInfo
import com.outervoid.axiom.settings.AxiomSettings
import com.outervoid.axiom.settings.accentColor
import com.outervoid.axiom.settings.appKey
import com.outervoid.axiom.ui.components.AppIconCell
import com.outervoid.axiom.ui.components.AxiomWordmark
import com.outervoid.axiom.ui.components.CornerBrackets
import com.outervoid.axiom.ui.components.DockBar
import com.outervoid.axiom.ui.components.EmptyWidgetPanel
import com.outervoid.axiom.ui.components.SystemOnlineIndicator
import com.outervoid.axiom.ui.theme.*
import com.outervoid.axiom.widgets.AxiomWidgetHost

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    date: String,
    time: String,
    favoriteApps: List<AppInfo>,
    settings: AxiomSettings,
    widgetHost: AxiomWidgetHost,
    onOpenAppDrawer: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenSystemPanel: () -> Unit,
    onOpenLeftFolder: () -> Unit,
    onOpenRightFolder: () -> Unit,
    onLaunchShooter: () -> Unit,
    onLaunchApp: (AppInfo) -> Unit,
    onOpenAppInfo: (AppInfo) -> Unit,
    onFavoriteKeysChanged: (Set<String>) -> Unit,
    onPickFavorite: () -> Unit,
    onOpenWidgetPicker: (Int) -> Unit,
    onOpenClock: () -> Unit,
    onOpenCalendar: () -> Unit,
    onPhone: () -> Unit,
    onMessages: () -> Unit,
    onOpenPhoneSettings: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
    val accent = settings.accentColor()
    val outerPadding = if (settings.compactMode) 12.dp else 18.dp
    var panel by remember(settings.homePanelCount) { mutableIntStateOf(0) }
    var selectedFavorite by remember { mutableStateOf<AppInfo?>(null) }
    fun haptic() {
        if (settings.hapticFeedbackEnabled) haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }
    val displayedFavorites = favoriteApps.take(settings.favoritesCount)
    val effectiveFavoriteKeys = if (settings.favoriteAppKeys.isEmpty()) {
        displayedFavorites.map { appKey(it.packageName, it.activityName) }.toSet()
    } else {
        settings.favoriteAppKeys
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (settings.searchOnSwipeUp && dragAmount < -28f) {
                        haptic()
                        onOpenAppDrawer()
                    }
                }
            }
            .pointerInput(settings.homePanelCount) {
                detectHorizontalDragGestures { _, dragAmount ->
                    if (settings.homePanelCount > 1 && kotlin.math.abs(dragAmount) > 18f) {
                        panel = if (dragAmount < 0) {
                            (panel + 1).coerceAtMost(settings.homePanelCount - 1)
                        } else {
                            (panel - 1).coerceAtLeast(0)
                        }
                    }
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = outerPadding, vertical = if (settings.compactMode) 14.dp else 20.dp)
        ) {
            AxiomWordmark(onLongPress = {
                haptic()
                onLaunchShooter()
            })
            Spacer(Modifier.height(if (settings.compactMode) 8.dp else 12.dp))
            Text(
                date,
                color = TextSecondary,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.combinedClickable(onClick = { haptic(); onOpenCalendar() })
            )
            Text(
                time,
                color = accent,
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.combinedClickable(onClick = { haptic(); onOpenClock() })
            )
            if (settings.showSystemOnline) {
                SystemOnlineIndicator(accentColor = accent)
            }
            Spacer(
                Modifier
                    .padding(top = if (settings.compactMode) 10.dp else 14.dp, bottom = if (settings.compactMode) 12.dp else 18.dp)
                    .width(104.dp)
                    .height(1.dp)
                    .background(accent)
            )
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (panel == 0) {
                    Column(Modifier.fillMaxSize()) {
                        Text("FAVORITES", color = TextDim, style = MaterialTheme.typography.labelLarge)
                        Spacer(Modifier.height(12.dp))
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(5),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(List(settings.favoritesCount) { index -> displayedFavorites.getOrNull(index) }) { _, app ->
                                if (app == null) {
                                    EmptyFavoriteSlot(
                                        compactMode = settings.compactMode,
                                        locked = settings.favoritesLocked,
                                        accentColor = accent,
                                        onClick = { haptic(); if (!settings.favoritesLocked) onPickFavorite() }
                                    )
                                } else {
                                    AppIconCell(
                                        app = app,
                                        onClick = { haptic(); onLaunchApp(app) },
                                        onLongClick = {
                                            haptic()
                                            if (!settings.favoritesLocked) selectedFavorite = app
                                        },
                                        showLabel = settings.showAppLabels,
                                        iconScale = settings.iconScale,
                                        compactMode = settings.compactMode,
                                        iconShape = settings.iconShape
                                    )
                                }
                            }
                        }
                    }
                } else {
                    val boundWidgetId = settings.widgetForPanel(panel)
                    if (boundWidgetId != null) {
                        BoundWidgetPanel(
                            widgetId = boundWidgetId,
                            widgetHost = widgetHost,
                            onLongPress = { haptic(); onOpenWidgetPicker(panel) }
                        )
                    } else {
                        EmptyWidgetPanel(panelNumber = panel + 1, onOpenWidgetPicker = { haptic(); onOpenWidgetPicker(panel) })
                    }
                }
                if (settings.homePanelCount > 1) {
                    Text(
                        "${panel + 1}/${settings.homePanelCount}",
                        color = TextDim,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.align(Alignment.BottomCenter)
                    )
                }
            }
            selectedFavorite?.let { app ->
                AlertDialog(
                    onDismissRequest = { selectedFavorite = null },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                onFavoriteKeysChanged(effectiveFavoriteKeys - appKey(app.packageName, app.activityName))
                                selectedFavorite = null
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = Danger)
                        ) {
                            Text("REMOVE", color = Danger)
                        }
                    },
                    dismissButton = {
                        Row {
                            TextButton(onClick = { onOpenAppInfo(app); selectedFavorite = null }) {
                                Text("APP INFO", color = accent)
                            }
                            TextButton(onClick = { onPickFavorite(); selectedFavorite = null }) {
                                Text("REPLACE", color = accent)
                            }
                        }
                    },
                    title = { Text(app.label, color = TextPrimary) },
                    text = { Text("Favorite app", color = TextSecondary) },
                    containerColor = Surface
                )
            }
            Spacer(Modifier.height(if (settings.compactMode) 10.dp else 16.dp))
            DockBar(
                onPhone = { haptic(); onPhone() },
                onLeftFolder = { haptic(); onOpenLeftFolder() },
                onSettings = { haptic(); onOpenSettings() },
                onLongClickSettings = { haptic(); onLaunchShooter() },
                onAppDrawer = { haptic(); onOpenAppDrawer() },
                onSystem = { haptic(); onOpenSystemPanel() },
                onLongClickSystem = { haptic(); onOpenPhoneSettings() },
                onRightFolder = { haptic(); onOpenRightFolder() },
                onMessages = { haptic(); onMessages() },
                compactMode = settings.compactMode
            )
        }
        CornerBrackets(topPadding = 92.dp, bottomPadding = 104.dp)
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun EmptyFavoriteSlot(
    compactMode: Boolean,
    locked: Boolean,
    accentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(if (compactMode) 54.dp else 62.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceAlt)
            .combinedClickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(if (locked) "LOCK" else "+", color = accentColor, style = MaterialTheme.typography.titleLarge)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BoundWidgetPanel(
    widgetId: Int,
    widgetHost: AxiomWidgetHost,
    onLongPress: () -> Unit
) {
    val context = LocalContext.current
    val manager = remember { AppWidgetManager.getInstance(context) }
    val info = remember(widgetId) { runCatching { manager.getAppWidgetInfo(widgetId) }.getOrNull() }

    if (info == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .combinedClickable(onLongClick = onLongPress, onClick = {}),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("WIDGET UNAVAILABLE", color = TextDim, style = MaterialTheme.typography.titleLarge)
                Text("Long press to replace.", color = TextDim, style = MaterialTheme.typography.bodyMedium)
            }
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .combinedClickable(onLongClick = onLongPress, onClick = {})
    ) {
        AndroidView(
            factory = { ctx ->
                widgetHost.createBoundView(ctx, widgetId, info).apply {
                    setAppWidget(widgetId, info)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
