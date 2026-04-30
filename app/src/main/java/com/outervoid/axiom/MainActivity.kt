package com.outervoid.axiom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.outervoid.axiom.ui.screens.AmbientScreen
import com.outervoid.axiom.ui.screens.HomeScreen
import com.outervoid.axiom.ui.screens.SearchScreen
import com.outervoid.axiom.ui.state.LauncherState
import com.outervoid.axiom.ui.theme.AxiomTheme
import com.outervoid.axiom.vm.LauncherViewModel
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    private val vm: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AxiomTheme {
                val apps by vm.apps.collectAsState()
                var state by remember { mutableStateOf(LauncherState.Ambient) }
                val now = remember { LocalDateTime.now() }
                val date = now.format(DateTimeFormatter.ofPattern("EEE, MMM d"))
                val time = now.format(DateTimeFormatter.ofPattern("HH:mm"))
                LaunchedEffect(Unit) { delay(1500); state = LauncherState.Home }
                BackHandler(enabled = state == LauncherState.Home) {}
                when (state) {
                    LauncherState.Ambient -> AmbientScreen(date)
                    LauncherState.Home -> HomeScreen(date, time, apps, vm, onOpenSearch = { state = LauncherState.Search })
                    LauncherState.Search -> SearchScreen(vm, onClose = { state = LauncherState.Home })
                }
            }
        }
    }
}
