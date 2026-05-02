package com.outervoid.axiom.ui.feed.modules

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.outervoid.axiom.feed.FeedModuleConfig
import com.outervoid.axiom.ui.theme.Amber
import kotlinx.coroutines.delay

@Composable
fun SessionTimerModule(config: FeedModuleConfig) {
    var running by remember { mutableStateOf(false) }
    var seconds by remember { mutableIntStateOf(0) }
    LaunchedEffect(running) {
        while (running) {
            delay(1000)
            seconds += 1
        }
    }
    FeedModuleCard(config, trailing = {
        ModuleAction(if (running) "Pause" else "Start") { running = !running }
    }) {
        Text("%02d:%02d".format(seconds / 60, seconds % 60), color = Amber, style = MaterialTheme.typography.displaySmall)
        ModuleAction("Reset") { seconds = 0; running = false }
    }
}
