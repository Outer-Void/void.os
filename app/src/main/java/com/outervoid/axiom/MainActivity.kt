package com.outervoid.axiom

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.outervoid.axiom.launch.SignedGameLaunchManager
import com.outervoid.axiom.module.ModuleStatusManager
import com.outervoid.axiom.security.EntitlementManager
import com.outervoid.axiom.security.LicenseGate
import com.outervoid.axiom.security.SignatureGate
import com.outervoid.axiom.ui.screens.AxiomShooterTransitionScreen
import com.outervoid.axiom.ui.screens.AmbientScreen
import com.outervoid.axiom.ui.screens.HomeScreen
import com.outervoid.axiom.ui.screens.LockedScreen
import com.outervoid.axiom.ui.screens.SearchScreen
import com.outervoid.axiom.ui.screens.SettingsScreen
import com.outervoid.axiom.ui.screens.SystemPanelScreen
import com.outervoid.axiom.ui.state.LauncherState
import com.outervoid.axiom.ui.theme.Amber
import com.outervoid.axiom.ui.theme.AxiomTheme
import com.outervoid.axiom.ui.theme.Surface
import com.outervoid.axiom.ui.theme.TextPrimary
import com.outervoid.axiom.ui.theme.TextSecondary
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
            val entitlementManager = remember { EntitlementManager(applicationContext).also { it.initialize() } }
            val signatureGate = remember { SignatureGate(applicationContext) }
            val gameLaunchManager = remember { SignedGameLaunchManager(applicationContext) }
            val moduleStatusManager = remember { ModuleStatusManager(applicationContext) }
            val entitlementState by entitlementManager.state.collectAsState()
            val settings by vm.settings.collectAsState()
            val apps by vm.apps.collectAsState()
            val filteredApps by vm.filteredApps.collectAsState()
            val query by vm.query.collectAsState()
            val voidSysInstalled by moduleStatusManager.voidSysInstalled.collectAsState()
            val gameInstalled by moduleStatusManager.voidGameInstalled.collectAsState()
            val productUnlocked = LicenseGate.unlocks(entitlementState) && signatureGate.unlocks(entitlementState)
            var shooterDialog by remember { mutableStateOf<AxiomDialogState?>(null) }
            var systemPanelDisabledVisible by remember { mutableStateOf(false) }

            DisposableEffect(moduleStatusManager) {
                moduleStatusManager.register()
                onDispose { moduleStatusManager.unregister() }
            }

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

                LaunchedEffect(state, settings.systemPanelEnabled) {
                    if (state == LauncherState.SystemPanel && !settings.systemPanelEnabled) {
                        state = LauncherState.Home
                        systemPanelDisabledVisible = true
                    }
                }

                if (!productUnlocked) {
                    LockedScreen(
                        onCheckLicense = entitlementManager::refresh,
                        onOpenPlayStore = ::openAxiomPlayStore
                    )
                    return@AxiomTheme
                }

                BackHandler(enabled = state == LauncherState.Home) {}
                BackHandler(enabled = state == LauncherState.Search || state == LauncherState.Settings || state == LauncherState.SystemPanel || state == LauncherState.AxiomShooterTransition) {
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
                        onOpenSystemPanel = {
                            if (settings.systemPanelEnabled) {
                                state = LauncherState.SystemPanel
                            } else {
                                systemPanelDisabledVisible = true
                            }
                        },
                        onLaunchShooter = {
                            if (gameInstalled) {
                                state = LauncherState.AxiomShooterTransition
                            } else {
                                shooterDialog = AxiomDialogState(
                                    title = "AXIOM SHOOTER NOT INSTALLED",
                                    message = "Install the AXIOM Shooter module to continue."
                                )
                            }
                        }
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
                        onResetSettings = vm::resetSettings,
                        axiomShooterInstalled = gameInstalled
                    )
                    LauncherState.SystemPanel -> SystemPanelScreen(
                        settings = settings,
                        onOpenSettings = { state = LauncherState.Settings },
                        onOpenSearch = { state = LauncherState.Search },
                        onReturnHome = { state = LauncherState.Home }
                    )
                    LauncherState.AxiomShooterTransition -> AxiomShooterTransitionScreen(
                        onLaunch = {
                            when (gameLaunchManager.launch(entitlementState)) {
                                SignedGameLaunchManager.LaunchResult.Started -> Unit
                                SignedGameLaunchManager.LaunchResult.Missing -> shooterDialog = AxiomDialogState(
                                    title = "AXIOM SHOOTER NOT INSTALLED",
                                    message = "Install the AXIOM Shooter module to continue."
                                )
                                SignedGameLaunchManager.LaunchResult.Locked -> shooterDialog = AxiomDialogState(
                                    title = "AXIOM SHOOTER LOCKED",
                                    message = "This protected module requires a valid AXIOM entitlement."
                                )
                                SignedGameLaunchManager.LaunchResult.Unavailable -> shooterDialog = AxiomDialogState(
                                    title = "AXIOM SHOOTER UNAVAILABLE",
                                    message = "The module could not be opened."
                                )
                            }
                            state = LauncherState.Home
                        }
                    )
                }

                shooterDialog?.let { dialog ->
                    AxiomMessageDialog(
                        title = dialog.title,
                        message = dialog.message,
                        onDismiss = { shooterDialog = null }
                    )
                }
                if (systemPanelDisabledVisible) {
                    AxiomMessageDialog(
                        title = "SYSTEM PANEL DISABLED",
                        message = "Enable it in Settings to access the AXIOM system control panel.",
                        onDismiss = { systemPanelDisabledVisible = false }
                    )
                }
            }
        }
    }

    private fun openAxiomPlayStore() {
        val uri = Uri.parse("market://details?id=$packageName")
        val webUri = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
        runCatching { startActivity(Intent(Intent.ACTION_VIEW, uri)) }
            .recoverCatching { startActivity(Intent(Intent.ACTION_VIEW, webUri)) }
    }

}

@Stable
private data class AxiomDialogState(
    val title: String,
    val message: String
)

@androidx.compose.runtime.Composable
private fun AxiomMessageDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = Amber)
            ) {
                Text("OK")
            }
        },
        title = { Text(title, color = Amber) },
        text = { Text(message, color = TextSecondary) },
        containerColor = Surface,
        textContentColor = TextPrimary,
        titleContentColor = Amber
    )
}
