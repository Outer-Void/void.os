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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.data.AppInfo
import com.outervoid.axiom.ui.components.AppIconCell
import com.outervoid.axiom.ui.components.CornerBrackets
import com.outervoid.axiom.ui.components.DockBar
import com.outervoid.axiom.ui.components.SystemOnlineIndicator
import com.outervoid.axiom.ui.theme.*
import com.outervoid.axiom.vm.LauncherViewModel

@Composable
fun HomeScreen(date: String, time: String, apps: List<AppInfo>, vm: LauncherViewModel, onOpenSearch: () -> Unit) {
    val ctx = LocalContext.current
    var selected by remember { mutableStateOf<AppInfo?>(null) }
    val gridState = rememberLazyGridState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -28f) onOpenSearch()
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 20.dp)
        ) {
            Text(date, color = TextSecondary, style = AxiomDateStyle)
            Text(time, color = Amber, style = AxiomClockStyle)
            SystemOnlineIndicator()
            Spacer(
                Modifier
                    .padding(top = 14.dp, bottom = 18.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Amber)
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.weight(1f),
                state = gridState,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(apps, key = { "${it.packageName}/${it.activityName}" }) { app ->
                    AppIconCell(
                        app = app,
                        onClick = { vm.launchApp(ctx, app) },
                        onLongClick = { selected = app }
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            DockBar(onOpenSearch)
        }
        CornerBrackets()
    }

    selected?.let { app ->
        AlertDialog(
            onDismissRequest = { selected = null },
            confirmButton = {
                TextButton(onClick = { vm.openAppInfo(ctx, app); selected = null }) {
                    Text("APP INFO", style = AxiomStatusStyle)
                }
            },
            dismissButton = {
                TextButton(onClick = { vm.uninstallApp(ctx, app); selected = null }) {
                    Text("UNINSTALL", color = Danger, style = AxiomStatusStyle)
                }
            },
            title = { Text(app.label, style = AxiomSectionLabelStyle) },
            text = { Text(app.packageName, style = AxiomAppLabelStyle) },
            containerColor = Surface,
            textContentColor = TextPrimary,
            titleContentColor = TextPrimary
        )
    }
}
