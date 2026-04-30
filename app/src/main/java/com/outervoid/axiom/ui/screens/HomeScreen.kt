package com.outervoid.axiom.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.data.AppInfo
import com.outervoid.axiom.ui.components.AppIconCell
import com.outervoid.axiom.ui.components.DockBar
import com.outervoid.axiom.ui.components.SystemOnlineIndicator
import com.outervoid.axiom.ui.theme.*
import com.outervoid.axiom.vm.LauncherViewModel

@Composable
fun HomeScreen(date: String, time: String, apps: List<AppInfo>, vm: LauncherViewModel, onOpenSearch: () -> Unit) {
    val ctx = LocalContext.current
    var selected by remember { mutableStateOf<AppInfo?>(null) }
    Column(Modifier.fillMaxSize().background(AxiomBackground).padding(16.dp)) {
        Text(date, color = AxiomTextSecondary)
        Text(time, color = AxiomTextPrimary)
        SystemOnlineIndicator(AxiomAmber, AxiomTextPrimary)
        LazyVerticalGrid(columns = GridCells.Fixed(4), modifier = Modifier.weight(1f)) {
            items(apps) { app -> AppIconCell(app, onClick = { vm.launchApp(ctx, app) }, onLongClick = { selected = app }) }
        }
        DockBar(onOpenSearch)
    }
    selected?.let { app ->
        AlertDialog(onDismissRequest = { selected = null }, confirmButton = {
            TextButton(onClick = { vm.openAppInfo(ctx, app); selected = null }) { Text("App Info") }
        }, dismissButton = {
            TextButton(onClick = { vm.uninstallApp(ctx, app); selected = null }) { Text("Uninstall") }
        }, title = { Text(app.label) }, text = { Text(app.packageName) })
    }
}
