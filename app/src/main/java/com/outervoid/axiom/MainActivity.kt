package com.outervoid.axiom

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.outervoid.axiom.ui.screens.AmbientScreen
import com.outervoid.axiom.ui.screens.HomeScreen
import com.outervoid.axiom.ui.screens.SearchScreen
import com.outervoid.axiom.ui.screens.SettingsScreen
import com.outervoid.axiom.ui.screens.SystemPanelScreen
import com.outervoid.axiom.ui.state.LauncherState
import com.outervoid.axiom.ui.theme.AxiomTheme
import com.outervoid.axiom.vm.LauncherViewModel
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val vm: LauncherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val settings by vm.settings.collectAsState()
            val apps by vm.apps.collectAsState()
            val filteredApps by vm.filteredApps.collectAsState()
            val query by vm.query.collectAsState()
            val voidSysInstalled by vm.voidSysInstalled.collectAsState()

            DisposableEffect(settings.hideStatusBar, settings.hideNavigationBar) {
                val controller = WindowCompat.getInsetsController(window, window.decorView)
                WindowCompat.setDecorFitsSystemWindows(window, true)
                if (settings.hideStatusBar) {
                    controller.hide(WindowInsetsCompat.Type.statusBars())
                } else {
                    controller.show(WindowInsetsCompat.Type.statusBars())
                }
                if (settings.hideNavigationBar) {
                    controller.hide(WindowInsetsCompat.Type.navigationBars())
                } else {
                    controller.show(WindowInsetsCompat.Type.navigationBars())
                }
                onDispose {
                    controller.show(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
                }
            }

            AxiomTheme(fontKey = settings.selectedFontKey) {
                var state by remember { mutableStateOf(if (settings.ambientBootEnabled) LauncherState.Ambient else LauncherState.Home) }
                var now by remember { mutableStateOf(LocalDateTime.now()) }
                val date = now.format(DateTimeFormatter.ofPattern("EEE, MMM d", Locale.US)).uppercase(Locale.US)
                val time = now.format(DateTimeFormatter.ofPattern("HH:mm", Locale.US))

                LaunchedEffect(Unit) {
                    while (true) {
                        now = LocalDateTime.now()
                        delay(1_000)
                    }
                }

                LaunchedEffect(settings.ambientBootEnabled) {
                    if (!settings.ambientBootEnabled && state == LauncherState.Ambient) {
                        state = LauncherState.Home
                    }
                }

                BackHandler(enabled = state == LauncherState.Home) {}
                BackHandler(enabled = state == LauncherState.Search || state == LauncherState.Settings || state == LauncherState.SystemPanel) {
                    state = LauncherState.Home
                }
                when (state) {
                    LauncherState.Ambient -> AmbientScreen(date, onComplete = { state = LauncherState.Home })
                    LauncherState.Home -> HomeScreen(
                        date = date,
                        time = time,
                        apps = apps,
                        settings = settings,
                        vm = vm,
                        onOpenSearch = { state = LauncherState.Search },
                        onOpenSettings = { state = LauncherState.Settings },
                        onOpenSystemPanel = { state = LauncherState.SystemPanel },
                        onLaunchShooter = { launchVoidGame() }
                    )
                    LauncherState.Search -> SearchScreen(
                        query = query,
                        apps = filteredApps,
                        settings = settings,
                        onQueryChange = vm::setQuery,
                        onLaunch = { app ->
                            vm.launchApp(this@MainActivity, app)
                            state = LauncherState.Home
                        },
                        onClose = { state = LauncherState.Home }
                    )
                    LauncherState.Settings -> SettingsScreen(
                        settings = settings,
                        voidSysInstalled = voidSysInstalled,
                        onFontSelected = vm::setFont,
                        onIconPackSelected = vm::setIconPack,
                        onShowAppLabelsChanged = vm::setShowAppLabels,
                        onGridColumnsChanged = vm::setGridColumns,
                        onIconScaleChanged = vm::setIconScale,
                        onShowSystemOnlineChanged = vm::setShowSystemOnline,
                        onAmbientBootChanged = vm::setAmbientBoot,
                        onAmberAccentChanged = vm::setAmberAccent,
                        onCompactModeChanged = vm::setCompactMode,
                        onHideStatusBarChanged = vm::setHideStatusBar,
                        onHideNavigationBarChanged = vm::setHideNavigationBar,
                        onSearchOnSwipeUpChanged = vm::setSearchOnSwipeUp,
                        onSystemPanelChanged = vm::setSystemPanel,
                        onHapticFeedbackChanged = vm::setHapticFeedback,
                        onReloadApps = vm::loadApps,
                        onResetSettings = vm::resetSettings
                    )
                    LauncherState.SystemPanel -> SystemPanelScreen(
                        settings = settings,
                        onOpenSettings = { state = LauncherState.Settings },
                        onOpenSearch = { state = LauncherState.Search },
                        onReturnHome = { state = LauncherState.Home }
                    )
                }
            }
        }
    }

    private fun launchVoidGame() {
        val intent = Intent().apply {
            setClassName("com.outervoid.voidgame", "com.outervoid.voidgame.VoidGameActivity")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { startActivity(intent) }
    }
}
