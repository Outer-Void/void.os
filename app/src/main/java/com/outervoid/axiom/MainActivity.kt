package com.outervoid.axiom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.outervoid.axiom.ui.screens.AmbientScreen
import com.outervoid.axiom.ui.screens.HomeScreen
import com.outervoid.axiom.ui.screens.SearchScreen
import com.outervoid.axiom.ui.state.LauncherState
import com.outervoid.axiom.ui.theme.AxiomTheme
import com.outervoid.axiom.vm.LauncherViewModel
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val vm: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AxiomTheme {
                val apps by vm.apps.collectAsState()
                val filteredApps by vm.filteredApps.collectAsState()
                val query by vm.query.collectAsState()
                var state by remember { mutableStateOf(LauncherState.Ambient) }
                var now by remember { mutableStateOf(LocalDateTime.now()) }
                val date = now.format(DateTimeFormatter.ofPattern("EEE, MMM d", Locale.US)).uppercase(Locale.US)
                val time = now.format(DateTimeFormatter.ofPattern("HH:mm", Locale.US))

                LaunchedEffect(Unit) {
                    while (true) {
                        now = LocalDateTime.now()
                        delay(1_000)
                    }
                }

                BackHandler(enabled = state == LauncherState.Home) {}
                when (state) {
                    LauncherState.Ambient -> AmbientScreen(date, onComplete = { state = LauncherState.Home })
                    LauncherState.Home -> HomeScreen(date, time, apps, vm, onOpenSearch = { state = LauncherState.Search })
                    LauncherState.Search -> SearchScreen(
                        query = query,
                        apps = filteredApps,
                        onQueryChange = vm::setQuery,
                        onLaunch = { app ->
                            vm.launchApp(this@MainActivity, app)
                            state = LauncherState.Home
                        },
                        onClose = { state = LauncherState.Home }
                    )
                }
            }
        }
    }
}
