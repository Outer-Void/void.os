package com.outervoid.axiom

import android.app.AlarmManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.AlarmClock
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
import androidx.compose.ui.graphics.Color
import com.outervoid.axiom.data.AppInfo
import com.outervoid.axiom.launch.SignedGameLaunchManager
import com.outervoid.axiom.module.ModuleStatusManager
import com.outervoid.axiom.security.EntitlementManager
import com.outervoid.axiom.security.LicenseGate
import com.outervoid.axiom.security.SignatureGate
import com.outervoid.axiom.settings.ClockFormat
import com.outervoid.axiom.settings.accentColor
import com.outervoid.axiom.settings.appKey
import com.outervoid.axiom.ui.screens.AboutScreen
import com.outervoid.axiom.ui.screens.AxiomShooterTransitionScreen
import com.outervoid.axiom.ui.screens.AmbientScreen
import com.outervoid.axiom.ui.screens.AppDrawerScreen
import com.outervoid.axiom.ui.screens.CustomFolderPanel
import com.outervoid.axiom.ui.screens.FolderAppSelectionScreen
import com.outervoid.axiom.ui.screens.HiddenAppsScreen
import com.outervoid.axiom.ui.screens.HomeScreen
import com.outervoid.axiom.ui.screens.LockedScreen
import com.outervoid.axiom.ui.screens.SettingsScreen
import com.outervoid.axiom.ui.screens.SystemPanelScreen
import com.outervoid.axiom.ui.screens.WidgetPickerScreen
import com.outervoid.axiom.ui.state.LauncherState
import com.outervoid.axiom.ui.theme.Amber
import com.outervoid.axiom.ui.theme.AxiomTheme
import com.outervoid.axiom.ui.theme.Surface
import com.outervoid.axiom.ui.theme.TextPrimary
import com.outervoid.axiom.ui.theme.TextSecondary
import com.outervoid.axiom.vm.LauncherViewModel
import com.outervoid.axiom.wallpaper.AxiomWallpaperManager
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
            val allApps by vm.apps.collectAsState()
            val filteredApps by vm.filteredApps.collectAsState()
            val query by vm.query.collectAsState()
            val voidSysInstalled by moduleStatusManager.voidSysInstalled.collectAsState()
            val productUnlocked = LicenseGate.unlocks(entitlementState) && signatureGate.unlocks(entitlementState)
            var shooterDialog by remember { mutableStateOf<AxiomDialogState?>(null) }
            var systemPanelDisabledVisible by remember { mutableStateOf(false) }
            var wallpaperDialog by remember { mutableStateOf<AxiomDialogState?>(null) }

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
                var pickingFavorite by remember { mutableStateOf(false) }
                val date = now.format(DateTimeFormatter.ofPattern("EEE, MMM d", Locale.US)).uppercase(Locale.US)
                val use24Hour = when (settings.clockFormat) {
                    ClockFormat.SYSTEM -> android.text.format.DateFormat.is24HourFormat(this@MainActivity)
                    ClockFormat.TWELVE_HOUR -> false
                    ClockFormat.TWENTY_FOUR_HOUR -> true
                }
                val time = now.format(DateTimeFormatter.ofPattern(if (use24Hour) "HH:mm" else "h:mm a", Locale.US))
                val accent = settings.accentColor()
                val appByKey = allApps.associateBy { appKey(it.packageName, it.activityName) }
                val favoriteApps = if (settings.favoriteAppKeys.isEmpty()) {
                    allApps.take(settings.favoritesCount)
                } else {
                    settings.favoriteAppKeys.mapNotNull { appByKey[it] }.take(settings.favoritesCount)
                }
                val leftFolderApps = settings.leftFolderAppPackages.mapNotNull { appByKey[it] }
                val rightFolderApps = settings.rightFolderAppPackages.mapNotNull { appByKey[it] }

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
                BackHandler(
                    enabled = state == LauncherState.AppDrawer ||
                        state == LauncherState.Settings ||
                        state == LauncherState.SystemPanel ||
                        state == LauncherState.LeftFolder ||
                        state == LauncherState.RightFolder ||
                        state == LauncherState.HiddenApps ||
                        state == LauncherState.About ||
                        state == LauncherState.Contact ||
                        state == LauncherState.WidgetPicker ||
                        state == LauncherState.LeftFolderApps ||
                        state == LauncherState.RightFolderApps ||
                        state == LauncherState.AxiomShooterTransition
                ) {
                    pickingFavorite = false
                    state = LauncherState.Home
                }
                when (state) {
                    LauncherState.Ambient -> AmbientScreen(date, onComplete = { state = LauncherState.Home })
	                    LauncherState.Home -> HomeScreen(
                        date = date,
                        time = time,
                        favoriteApps = favoriteApps,
                        settings = settings,
                        onOpenAppDrawer = { state = LauncherState.AppDrawer },
                        onOpenSettings = { state = LauncherState.Settings },
                        onOpenSystemPanel = {
                            if (settings.systemPanelEnabled) {
                                state = LauncherState.SystemPanel
                            } else {
                                systemPanelDisabledVisible = true
                            }
                        },
                        onOpenLeftFolder = { state = LauncherState.LeftFolder },
                        onOpenRightFolder = { state = LauncherState.RightFolder },
                        onLaunchShooter = {
                            state = LauncherState.AxiomShooterTransition
                        },
                        onLaunchApp = { app -> vm.launchApp(this@MainActivity, app) },
                        onOpenAppInfo = { app -> vm.openAppInfo(this@MainActivity, app) },
                        onFavoriteKeysChanged = vm::setFavoriteAppKeys,
                        onPickFavorite = {
                            pickingFavorite = true
                            state = LauncherState.AppDrawer
                        },
                        onOpenWidgetPicker = { state = LauncherState.WidgetPicker },
                        onOpenClock = ::openClock,
                        onOpenCalendar = ::openCalendar
                    )
                    LauncherState.AppDrawer -> AppDrawerScreen(
                        query = query,
                        apps = filteredApps,
                        settings = settings,
                        onQueryChange = vm::setQuery,
                        onLaunch = { app ->
                            if (pickingFavorite) {
                                val effectiveKeys = if (settings.favoriteAppKeys.isEmpty()) {
                                    favoriteApps.map { appKey(it.packageName, it.activityName) }.toSet()
                                } else {
                                    settings.favoriteAppKeys
                                }
                                vm.setFavoriteAppKeys((effectiveKeys + appKey(app.packageName, app.activityName)).take(settings.favoritesCount).toSet())
                                pickingFavorite = false
                            } else {
                                vm.launchApp(this@MainActivity, app)
                            }
                            state = LauncherState.Home
                        },
                        onClose = {
                            pickingFavorite = false
                            state = LauncherState.Home
                        }
                    )
                    LauncherState.LeftFolder -> CustomFolderPanel(
                        settings = settings,
                        title = settings.leftFolderName,
                        subtitle = "LEFT SLOT",
                        apps = leftFolderApps,
                        onLaunchApp = { app ->
                            vm.launchApp(this@MainActivity, app)
                            state = LauncherState.Home
                        },
                        onOpenAppInfo = { app -> vm.openAppInfo(this@MainActivity, app) }
                    )
                    LauncherState.RightFolder -> CustomFolderPanel(
                        settings = settings,
                        title = settings.rightFolderName,
                        subtitle = "RIGHT SLOT",
                        apps = rightFolderApps,
                        onLaunchApp = { app ->
                            vm.launchApp(this@MainActivity, app)
                            state = LauncherState.Home
                        },
                        onOpenAppInfo = { app -> vm.openAppInfo(this@MainActivity, app) }
                    )
	                    LauncherState.Settings -> SettingsScreen(
                        settings = settings,
                        voidSysInstalled = voidSysInstalled,
                        onFontSelected = vm::setFont,
                        onIconPackSelected = vm::setIconPack,
                        onIconShapeSelected = vm::setIconShape,
                        onAccentColorSelected = vm::setAccentColor,
                        onClockFormatSelected = vm::setClockFormat,
                        onLockScreenWallpaper = {
                            when (AxiomWallpaperManager(this@MainActivity).setLockScreenWallpaper()) {
                                AxiomWallpaperManager.Result.Applied -> {
                                    vm.setLockScreenWallpaperEnabled(true)
                                    wallpaperDialog = AxiomDialogState("LOCK SCREEN WALLPAPER", "AXIOM lock-screen wallpaper applied.")
                                }
                                AxiomWallpaperManager.Result.Restricted -> {
                                    vm.setLockScreenWallpaperEnabled(false)
                                    wallpaperDialog = AxiomDialogState(
                                        "LOCK SCREEN WALLPAPER",
                                        "This device does not allow AXIOM to set lock-screen wallpaper directly."
                                    )
                                }
                            }
                        },
                        onFavoritesCountSelected = vm::setFavoritesCount,
                        onFavoritesLockedChanged = vm::setFavoritesLocked,
                        onHomePanelCountSelected = vm::setHomePanelCount,
                        onOpenHiddenApps = { state = LauncherState.HiddenApps },
                        onOpenLeftFolderApps = { state = LauncherState.LeftFolderApps },
                        onOpenRightFolderApps = { state = LauncherState.RightFolderApps },
                        onFolderIconStyleSelected = vm::setFolderIconStyle,
                        onChooseDefaultLauncher = ::openDefaultLauncherSettings,
                        onOpenPhoneSettings = { openSettingsIntent(Intent(android.provider.Settings.ACTION_SETTINGS)) },
                        onOpenAbout = { state = LauncherState.About },
                        onOpenContact = { state = LauncherState.Contact },
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
                        onOpenAppDrawer = { state = LauncherState.AppDrawer },
                        onOpenLeftFolder = { state = LauncherState.LeftFolder },
                        onOpenRightFolder = { state = LauncherState.RightFolder },
                        onReturnHome = { state = LauncherState.Home }
                    )
	                    LauncherState.AxiomShooterTransition -> AxiomShooterTransitionScreen(
                        accentColor = accent,
                        onLaunch = {
                            when (gameLaunchManager.launch(entitlementState)) {
                                SignedGameLaunchManager.LaunchResult.Started -> Unit
                                SignedGameLaunchManager.LaunchResult.Locked -> shooterDialog = AxiomDialogState(
                                    title = "AXIOM SHOOTER LOCKED",
                                    message = "This protected module requires a valid AXIOM entitlement."
                                )
                                SignedGameLaunchManager.LaunchResult.Unavailable -> shooterDialog = AxiomDialogState(
                                    title = "AXIOM SHOOTER UNAVAILABLE",
                                    message = "The internal arcade module could not be opened."
                                )
                            }
                            state = LauncherState.Home
                        }
                    )
                    LauncherState.HiddenApps -> HiddenAppsScreen(
                        apps = allApps,
                        settings = settings,
                        onHiddenAppsChanged = vm::setHiddenApps
                    )
                    LauncherState.LeftFolderApps -> FolderAppSelectionScreen(
                        title = "LEFT FOLDER APPS",
                        apps = allApps,
                        selectedKeys = settings.leftFolderAppPackages,
                        settings = settings,
                        onChanged = vm::setLeftFolderApps
                    )
                    LauncherState.RightFolderApps -> FolderAppSelectionScreen(
                        title = "RIGHT FOLDER APPS",
                        apps = allApps,
                        selectedKeys = settings.rightFolderAppPackages,
                        settings = settings,
                        onChanged = vm::setRightFolderApps
                    )
                    LauncherState.WidgetPicker -> WidgetPickerScreen(settings = settings)
                    LauncherState.About -> AboutScreen(
                        settings = settings,
                        title = "ABOUT AXIOM",
                        onOpenOuterVoid = { openUrl("https://github.com/Outer-Void") },
                        onOpenJustadude = { openUrl("https://github.com/Justadudeinspace") },
                        onEmail = ::openEmail
                    )
                    LauncherState.Contact -> AboutScreen(
                        settings = settings,
                        title = "CONTACT",
                        onOpenOuterVoid = { openUrl("https://github.com/Outer-Void") },
                        onOpenJustadude = { openUrl("https://github.com/Justadudeinspace") },
                        onEmail = ::openEmail
                    )
                }

                shooterDialog?.let { dialog ->
                    AxiomMessageDialog(
                        title = dialog.title,
                        message = dialog.message,
                        accentColor = accent,
                        onDismiss = { shooterDialog = null }
                    )
                }
                wallpaperDialog?.let { dialog ->
                    AxiomMessageDialog(
                        title = dialog.title,
                        message = dialog.message,
                        accentColor = accent,
                        onDismiss = { wallpaperDialog = null }
                    )
                }
                if (systemPanelDisabledVisible) {
                    AxiomMessageDialog(
                        title = "SYSTEM PANEL DISABLED",
                        message = "Enable it in Settings to access the AXIOM system control panel.",
                        accentColor = accent,
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

    private fun openSettingsIntent(intent: Intent) {
        runCatching { startActivity(intent) }
            .recoverCatching { startActivity(Intent(android.provider.Settings.ACTION_SETTINGS)) }
    }

    private fun openDefaultLauncherSettings() {
        val intents = listOf(
            Intent(android.provider.Settings.ACTION_HOME_SETTINGS),
            Intent(android.provider.Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS),
            Intent(android.provider.Settings.ACTION_SETTINGS)
        )
        intents.firstNotNullOfOrNull { intent ->
            runCatching {
                startActivity(intent)
                true
            }.getOrNull()
        }
    }

    private fun openUrl(url: String) {
        runCatching { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
    }

    private fun openEmail() {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:theoutervoid@outlook.com"))
        runCatching { startActivity(intent) }
    }

    private fun openClock() {
        val intents = listOf(
            Intent(AlarmClock.ACTION_SHOW_ALARMS),
            Intent(AlarmClock.ACTION_SET_ALARM),
            Intent(android.provider.Settings.ACTION_SETTINGS)
        )
        openFirstAvailable(intents)
    }

    private fun openCalendar() {
        val intents = buildList {
            add(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_CALENDAR))
            listOf(
                "com.google.android.calendar",
                "com.samsung.android.calendar",
                "com.android.calendar"
            ).forEach { packageName ->
                packageManager.getLaunchIntentForPackage(packageName)?.let(::add)
            }
        }
        if (!openFirstAvailable(intents)) {
            // Calendar is optional on Android builds. Failing silently keeps Home stable.
        }
    }

    private fun openFirstAvailable(intents: List<Intent>): Boolean {
        intents.forEach { intent ->
            try {
                startActivity(intent)
                return true
            } catch (_: ActivityNotFoundException) {
                // Try the next supported target.
            } catch (_: SecurityException) {
                // Try the next supported target.
            }
        }
        return false
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
    accentColor: Color = Amber,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = accentColor)
            ) {
                Text("OK")
            }
        },
        title = { Text(title, color = accentColor) },
        text = { Text(message, color = TextSecondary) },
        containerColor = Surface,
        textContentColor = TextPrimary,
        titleContentColor = accentColor
    )
}
