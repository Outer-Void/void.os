package com.outervoid.axiom.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity
import com.outervoid.axiom.data.AppInfo
import com.outervoid.axiom.feed.FeedModuleRenderer
import com.outervoid.axiom.feed.FeedState
import com.outervoid.axiom.settings.AxiomSettings
import com.outervoid.axiom.settings.accentColor
import com.outervoid.axiom.ui.theme.Background
import com.outervoid.axiom.ui.theme.TextPrimary
import com.outervoid.axiom.ui.theme.TextSecondary

@Composable
fun AxiomFeedScreen(
    feedState: FeedState,
    settings: AxiomSettings,
    apps: List<AppInfo>,
    context: Context,
    onReturnHome: () -> Unit,
    onLaunchApp: (AppInfo) -> Unit,
    onOpenSystemPanel: () -> Unit,
    onOpenAppDrawer: () -> Unit,
    onOpenTerminal: () -> Unit = {},
    onUpdatePriorities: (List<String>) -> Unit,
    onUpdateTasks: (List<String>) -> Unit,
    onUpdateNextAction: (String) -> Unit,
    onUpdateNotes: (List<String>) -> Unit
) {
    val returnThreshold = with(LocalDensity.current) { 56.dp.toPx() }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .pointerInput(onReturnHome, returnThreshold) {
                var distance = 0f
                var dispatched = false
                detectHorizontalDragGestures(
                    onDragStart = { distance = 0f; dispatched = false },
                    onHorizontalDrag = { _, amount ->
                        distance += amount
                        if (!dispatched && distance <= -returnThreshold) {
                            dispatched = true
                            onReturnHome()
                        }
                    }
                )
            }
            .padding(horizontal = 18.dp, vertical = 22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text("AXIOM FEED", color = TextPrimary, style = MaterialTheme.typography.titleLarge)
            Text("CONTROL LAYER", color = TextSecondary, style = MaterialTheme.typography.labelLarge)
            Spacer(
                Modifier
                    .padding(top = 14.dp, bottom = 12.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(settings.accentColor())
            )
            feedState.activeModules.forEach { config ->
                if (config.collapsed) {
                    CollapsedFeedModule(config.titleOverride ?: config.type.label.uppercase())
                } else {
                    FeedModuleRenderer(
                        config = config,
                        state = feedState,
                        settings = settings,
                        apps = apps,
                        onLaunchApp = onLaunchApp,
                        onOpenSystemPanel = onOpenSystemPanel,
                        onOpenAppDrawer = onOpenAppDrawer,
                        onOpenTerminal = onOpenTerminal,
                        onUpdatePriorities = onUpdatePriorities,
                        onUpdateTasks = onUpdateTasks,
                        onUpdateNextAction = onUpdateNextAction,
                        onUpdateNotes = onUpdateNotes,
                        context = context
                    )
                }
            }
            Spacer(Modifier.height(18.dp))
        }
    }
}

@Composable
private fun CollapsedFeedModule(title: String) {
    Text(
        title,
        color = TextSecondary,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    )
}
