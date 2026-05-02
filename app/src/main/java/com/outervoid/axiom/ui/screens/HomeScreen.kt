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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.settings.AxiomSettings
import com.outervoid.axiom.ui.components.AxiomWordmark
import com.outervoid.axiom.ui.components.CornerBrackets
import com.outervoid.axiom.ui.components.DockBar
import com.outervoid.axiom.ui.components.SystemOnlineIndicator
import com.outervoid.axiom.ui.theme.*

@Composable
fun HomeScreen(
    date: String,
    time: String,
    settings: AxiomSettings,
    onOpenAppDrawer: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenSystemPanel: () -> Unit,
    onOpenLeftFolder: () -> Unit,
    onOpenRightFolder: () -> Unit,
    onLaunchShooter: () -> Unit
) {
    val haptics = LocalHapticFeedback.current
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
                        onOpenAppDrawer()
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
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Text("FAVORITES", color = TextDim, style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
                ) {
                    repeat(4) {
                        Box(
                            modifier = Modifier
                                .size(if (settings.compactMode) 54.dp else 62.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceAlt)
                        )
                    }
                }
            }
            Spacer(Modifier.height(if (settings.compactMode) 10.dp else 16.dp))
            DockBar(
                onLeftFolder = { haptic(); onOpenLeftFolder() },
                onSettings = { haptic(); onOpenSettings() },
                onLongClickSettings = { haptic(); onLaunchShooter() },
                onAppDrawer = { haptic(); onOpenAppDrawer() },
                onSystem = { haptic(); onOpenSystemPanel() },
                onRightFolder = { haptic(); onOpenRightFolder() },
                compactMode = settings.compactMode,
                accentColor = accent
            )
        }
        CornerBrackets(topPadding = 92.dp, bottomPadding = 104.dp)
    }
}
