package com.outervoid.axiom.ui.feed.modules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.height
import com.outervoid.axiom.data.AppInfo
import com.outervoid.axiom.feed.FeedModuleConfig
import com.outervoid.axiom.settings.appKey
import com.outervoid.axiom.ui.components.AppIconCell

@Composable
fun AppShortcutsModule(
    config: FeedModuleConfig,
    apps: List<AppInfo>,
    selectedKeys: Set<String>,
    onLaunchApp: (AppInfo) -> Unit,
    onOpenAppDrawer: () -> Unit
) {
    val selectedApps = apps.filter { appKey(it.packageName, it.activityName) in selectedKeys }.ifEmpty { apps.take(4) }
    FeedModuleCard(config, trailing = { ModuleAction("Drawer", onOpenAppDrawer) }) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.height(170.dp)
        ) {
            items(selectedApps.take(8)) { app ->
                AppIconCell(app = app, onClick = { onLaunchApp(app) }, onLongClick = {}, showLabel = true, compactMode = true)
            }
        }
    }
}
