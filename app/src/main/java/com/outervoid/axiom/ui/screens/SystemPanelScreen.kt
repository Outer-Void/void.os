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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.settings.AxiomSettings
import com.outervoid.axiom.system.SystemAction
import com.outervoid.axiom.system.SystemActionController
import com.outervoid.axiom.ui.components.CornerBrackets
import com.outervoid.axiom.ui.components.DockBar
import com.outervoid.axiom.ui.components.SystemActionTile
import com.outervoid.axiom.ui.theme.Amber
import com.outervoid.axiom.ui.theme.Background
import com.outervoid.axiom.ui.theme.TextSecondary

@Composable
fun SystemPanelScreen(
    settings: AxiomSettings,
    onOpenSettings: () -> Unit,
    onOpenSearch: () -> Unit,
    onReturnHome: () -> Unit
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val controller = remember(context) { SystemActionController(context.applicationContext) }
    val accent = if (settings.amberAccentEnabled) Amber else TextSecondary
    fun haptic() {
        if (settings.hapticFeedbackEnabled) haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = if (settings.compactMode) 12.dp else 18.dp,
                    vertical = if (settings.compactMode) 16.dp else 22.dp
                )
        ) {
            Text("SYSTEM", color = accent, style = MaterialTheme.typography.titleLarge)
            Spacer(
                Modifier
                    .padding(top = 12.dp, bottom = 18.dp)
                    .width(104.dp)
                    .height(1.dp)
                    .background(accent)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(if (settings.compactMode) 6.dp else 10.dp),
                verticalArrangement = Arrangement.spacedBy(if (settings.compactMode) 8.dp else 12.dp)
            ) {
                items(SystemAction.PanelActions, key = { it.name }) { action ->
                    SystemActionTile(
                        action = action,
                        onClick = {
                            haptic()
                            controller.perform(action)
                        },
                        compactMode = settings.compactMode,
                        accentColor = accent
                    )
                }
            }
            Spacer(Modifier.height(if (settings.compactMode) 10.dp else 16.dp))
            DockBar(
                onSettings = { haptic(); onOpenSettings() },
                onSearch = { haptic(); onOpenSearch() },
                onSystem = { haptic(); onReturnHome() },
                compactMode = settings.compactMode,
                accentColor = accent
            )
        }
        CornerBrackets()
    }
}
