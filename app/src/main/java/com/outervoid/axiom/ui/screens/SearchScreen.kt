package com.outervoid.axiom.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.vm.LauncherViewModel
import com.outervoid.axiom.ui.components.AppIconCell
import com.outervoid.axiom.ui.theme.*

@Composable
fun SearchScreen(vm: LauncherViewModel, onClose: () -> Unit) {
    val ctx = LocalContext.current
    Column(Modifier.fillMaxSize().background(AxiomBackground).padding(16.dp).pointerInput(Unit) {
        detectVerticalDragGestures { _, dragAmount -> if (dragAmount > 20) onClose() }
    }) {
        Text("SEARCH APPS •", color = AxiomAmber)
        OutlinedTextField(value = vm.query.value, onValueChange = vm::setQuery, label = { Text("Query") })
        LazyVerticalGrid(columns = GridCells.Fixed(4)) {
            items(vm.filteredApps()) { app -> AppIconCell(app, onClick = { vm.launchApp(ctx, app); onClose() }, onLongClick = {}) }
        }
    }
}
