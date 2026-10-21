package com.outervoid.axiom

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.AlarmClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.outervoid.axiom.data.AppInfo
import com.outervoid.axiom.feed.FeedRepository
import com.outervoid.axiom.feed.FeedState
import com.outervoid.axiom.launch.SignedGameLaunchManager
import com.outervoid.axiom.module.ModuleStatusManager
import com.outervoid.axiom.security.EntitlementManager
import com.outervoid.axiom.security.LicenseGate
import com.outervoid.axiom.security.SignatureGate
import com.outervoid.axiom.settings.ClockFormat
import com.outervoid.axiom.settings.accentColor
import com.outervoid.axiom.settings.appKey
import com.outervoid.axiom.system.SystemSettingsNavigator
import com.outervoid.axiom.ui.screens.AboutScreen
import com.outervoid.axiom.ui.screens.AxiomShooterTransitionScreen
import com.outervoid.axiom.ui.screens.AmbientScreen
import com.outervoid.axiom.ui.screens.AppDrawerScreen
import com.outervoid.axiom.ui.screens.ContactScreen
import com.outervoid.axiom.ui.screens.CustomFolderPanel
import com.outervoid.axiom.ui.screens.AxiomFeedScreen
import com.outervoid.axiom.ui.screens.FeedConfigurationScreen
import com.outervoid.axiom.ui.screens.FeedModuleLibraryScreen
import com.outervoid.axiom.ui.screens.FolderAppSelectionScreen
import com.outervoid.axiom.ui.screens.HiddenAppsScreen
import com.outervoid.axiom.ui.screens.HomeScreen
import com.outervoid.axiom.ui.screens.LockedScreen
import com.outervoid.axiom.ui.screens.PermissionSetupScreen
import com.outervoid.axiom.ui.screens.SettingsScreen
import com.outervoid.axiom.ui.screens.SystemPanelScreen
import com.outervoid.axiom.ui.screens.TerminalPanelScreen
import com.outervoid.axiom.ui.screens.WidgetPickerScreen
import com.outervoid.axiom.ui.state.LauncherNavigationPolicy
import com.outervoid.axiom.ui.state.LauncherSearchPolicy
import com.outervoid.axiom.ui.state.LauncherState
import com.outervoid.axiom.ui.theme.Amber
import com.outervoid.axiom.ui.theme.AxiomTheme
import com.outervoid.axiom.ui.theme.Surface
import com.outervoid.axiom.ui.theme.TextPrimary
import com.outervoid.axiom.ui.theme.TextSecondary
import com.outervoid.axiom.vm.LauncherViewModel
import com.outervoid.axiom.wallpaper.AxiomWallpaperManager
import com.outervoid.axiom.widgets.AxiomWidgetHost
import com.outervoid.axiom.widgets.WidgetOperationPolicy
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
            val gameLaunchManager = remember { SignedGameLaunchManager(this@MainActivity) }
            val moduleStatusManager = remember { ModuleStatusManager(applicationContext) }
            val feedRepository = remember { FeedRepository(applicationContext) }
            val scope = rememberCoroutineScope()
            val entitlementState by entitlementManager.state.collectAsState()
            val settings by vm.settings.collectAsState()
            val feedState by feedRepository.state.collectAsState(initial = FeedState())
            val allApps by vm.apps.collectAsState()
            val appInventoryLoading by vm.appInventoryLoading.collectAsState()
            val appInventoryError by vm.appInventoryError.collectAsState()
            val filteredApps by vm.filteredApps.collectAsState()
            val query by vm.query.collectAsState()
            val voidSysInstalled by moduleStatusManager.voidSysInstalled.collectAsState()
            val productUnlocked = LicenseGate.unlocks(entitlementState) && signatureGate.unlocks(entitlementState)
            var shooterDialog by remember { mutableStateOf<AxiomDialogState?>(null) }
            var systemPanelDisabledVisible by remember { mutableStateOf(false) }
            var wallpaperDialog by remember { mutableStateOf<AxiomDialogState?>(null) }
            var widgetDialog by remember { mutableStateOf<AxiomDialogState?>(null) }

            var widgetPickerPanelIndex by rememberSaveable { mutableIntStateOf(0) }
            var pendingWidgetBind by rememberSaveable { mutableStateOf<Pair<Int, Int>?>(null) }
            var pendingWidgetConfigure by rememberSaveable { mutableStateOf<Pair<Int, Int>?>(null) }
            val widgetHost = remember { AxiomWidgetHost(applicationContext) }
            val appWidgetManager = remember { AppWidgetManager.getInstance(applicationContext) }

            DisposableEffect(Unit) {
                widgetHost.startListening()
                onDispose { widgetHost.stopListening() }
            }

            fun saveWidgetForPanel(panelIndex: Int, widgetId: Int) {
                settings.widgetForPanel(panelIndex)
                    ?.takeIf { it != widgetId }
                    ?.let { oldWidgetId -> runCatching { widgetHost.deleteAppWidgetId(oldWidgetId) } }
                vm.setWidgetForPanel(panelIndex, widgetId)
            }

            val widgetConfigLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                val pending = pendingWidgetConfigure
                if (pending != null) {
                    if (result.resultCode == Activity.RESULT_OK) {
                        saveWidgetForPanel(pending.first, pending.second)
                    }
                    WidgetOperationPolicy.abandonedWidgetId(
                        pending.second,
                        operationAccepted = result.resultCode == Activity.RESULT_OK
                    )?.let { widgetId -> runCatching { widgetHost.deleteAppWidgetId(widgetId) } }
                    pendingWidgetConfigure = null
                }
            }

            val bindWidgetLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                val pending = pendingWidgetBind
                if (pending != null) {
                    if (result.resultCode == Activity.RESULT_OK) {
                        val provider = runCatching { appWidgetManager.getAppWidgetInfo(pending.second) }.getOrNull()
                        if (provider?.configure != null) {
                            pendingWidgetConfigure = pending
                            val configureIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE).apply {
                                component = provider.configure
                                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, pending.second)
                            }
                            runCatching { widgetConfigLauncher.launch(configureIntent) }
                                .onFailure {
                                    saveWidgetForPanel(pending.first, pending.second)
                                    pendingWidgetConfigure = null
                                }
                        } else {
                            saveWidgetForPanel(pending.first, pending.second)
                        }
                    } else {
                        WidgetOperationPolicy.abandonedWidgetId(pending.second, operationAccepted = false)
                            ?.let { widgetId -> runCatching { widgetHost.deleteAppWidgetId(widgetId) } }
                        widgetDialog = AxiomDialogState(
                            "WIDGET PERMISSION REQUIRED",
                            "AXIOM needs permission to host widgets on home panels."
                        )
                    }
                    pendingWidgetBind = null
                }
            }

            fun handleWidgetSelect(panelIndex: Int, provider: AppWidgetProviderInfo) {
                val widgetId = widgetHost.allocateAppWidgetId()
                val bound = appWidgetManager.bindAppWidgetIdIfAllowed(widgetId, provider.provider)
                if (bound) {
                    if (provider.configure != null) {
                        pendingWidgetConfigure = panelIndex to widgetId
                        val configureIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE).apply {
                            component = provider.configure
                            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                        }
                        runCatching { widgetConfigLauncher.launch(configureIntent) }
                            .onFailure {
                                saveWidgetForPanel(panelIndex, widgetId)
                                pendingWidgetConfigure = null
                            }
                    } else {
                        saveWidgetForPanel(panelIndex, widgetId)
                    }
                } else {
                    pendingWidgetBind = panelIndex to widgetId
                    val bindIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND).apply {
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, provider.provider)
                    }
                    widgetDialog = AxiomDialogState(
                        "WIDGET PERMISSION REQUIRED",
                        "AXIOM needs permission to host widgets on home panels."
                    )
                    runCatching { bindWidgetLauncher.launch(bindIntent) }
                        .onFailure {
                            pendingWidgetBind = null
                            runCatching { widgetHost.deleteAppWidgetId(widgetId) }
                            widgetDialog = AxiomDialogState(
                                "WIDGET UNAVAILABLE",
                                "This device does not allow AXIOM to request widget binding directly."
                            )
                        }
                }
            }

            val calendarPermLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { /* permission result handled reactively in PermissionSetupScreen via lifecycle observer */ }

            val contactsPermLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { }

            val locationPermLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { }

            val notificationsPermLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { }

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
                var state by rememberSaveable { mutableStateOf(if (settings.ambientBootEnabled) LauncherState.Ambient else LauncherState.Home) }
                val lifecycle = LocalLifecycleOwner.current.lifecycle
                DisposableEffect(lifecycle, vm) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) vm.refreshAppsIfStale()
                    }
                    lifecycle.addObserver(observer)
                    onDispose { lifecycle.removeObserver(observer) }
                }
                LaunchedEffect(state) {
                    if (LauncherSearchPolicy.shouldResetQuery(state)) vm.setQuery("")
                }
                var now by remember { mutableStateOf(LocalDateTime.now()) }
                var pickingFavorite by rememberSaveable { mutableStateOf(false) }
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
                    emptyList()
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

                BackHandler {
                    pickingFavorite = false
                    LauncherNavigationPolicy.backTarget(state)?.let { state = it }
                }
                when (state) {
                    LauncherState.Ambient -> AmbientScreen(date, onComplete = { state = LauncherState.Home })
                    LauncherState.Home -> HomeScreen(
                        date = date,
                        time = time,
                        favoriteApps = favoriteApps,
                        settings = settings,
                        widgetHost = widgetHost,
                        onOpenAppDrawer = { state = LauncherState.AppDrawer },
                        feedEnabled = feedState.enabled,
                        onOpenAxiomFeed = { state = LauncherState.AxiomFeed },
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
                        onOpenWidgetPicker = { panelIndex ->
                            widgetPickerPanelIndex = panelIndex
                            state = LauncherState.WidgetPicker
                        },
                        onOpenClock = ::openClock,
                        onOpenCalendar = ::openCalendar,
                        onOpenNotificationAccess = {
                            openSettingsIntent(Intent(android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                        },
                        onPhone = { vm.openDialer(this@MainActivity) },
                        onMessages = { vm.openMessages(this@MainActivity) },
                        onOpenPhoneSettings = { SystemSettingsNavigator.openPhoneSettings(this@MainActivity) }
                    )
                    LauncherState.AxiomFeed -> AxiomFeedScreen(
                        feedState = feedState,
                        settings = settings,
                        apps = allApps,
                        context = this@MainActivity,
                        onReturnHome = { state = LauncherState.Home },
                        onLaunchApp = { app ->
                            vm.launchApp(this@MainActivity, app)
                            state = LauncherState.Home
                        },
                        onOpenSystemPanel = {
                            if (settings.systemPanelEnabled) {
                                state = LauncherState.SystemPanel
                            } else {
                                systemPanelDisabledVisible = true
                            }
                        },
                        onOpenAppDrawer = { state = LauncherState.AppDrawer },
                        onOpenTerminal = { state = LauncherState.TerminalPanel },
                        onUpdatePriorities = { values -> scope.launch { feedRepository.setPriorities(values) } },
                        onUpdateTasks = { values -> scope.launch { feedRepository.setTasks(values) } },
                        onUpdateNextAction = { value -> scope.launch { feedRepository.setNextAction(value) } },
                        onUpdateNotes = { values -> scope.launch { feedRepository.setNotes(values) } }
                    )
                    LauncherState.AppDrawer -> AppDrawerScreen(
                        query = query,
                        apps = filteredApps,
                        settings = settings,
                        inventoryLoading = appInventoryLoading,
                        inventoryError = appInventoryError,
                        onQueryChange = vm::setQuery,
                        onLaunch = { app ->
                            if (pickingFavorite) {
                                vm.setFavoriteAppKeys(
                                    (settings.favoriteAppKeys + appKey(app.packageName, app.activityName))
                                        .take(settings.favoritesCount).toSet()
                                )
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
                            val wallpaperManager = AxiomWallpaperManager(this@MainActivity)
                            when (wallpaperManager.setLockScreenWallpaper()) {
                                AxiomWallpaperManager.Result.Applied -> {
                                    vm.setLockScreenWallpaperEnabled(true)
                                    wallpaperDialog = AxiomDialogState("LOCK SCREEN WALLPAPER", "AXIOM lock-screen wallpaper applied.")
                                }
                                AxiomWallpaperManager.Result.Restricted -> {
                                    val fallbackOpened = wallpaperManager.createSystemSetterIntent()
                                        ?.let(::openWallpaperSetter)
                                        ?: false
                                    if (fallbackOpened) {
                                        wallpaperDialog = AxiomDialogState(
                                            "WALLPAPER SETUP OPENED",
                                            "Android blocked direct lock-screen install, so AXIOM opened the system wallpaper setter with the generated AXIOM wallpaper. Choose lock screen if your phone offers it."
                                        )
                                    } else {
                                        vm.setLockScreenWallpaperEnabled(false)
                                        wallpaperDialog = AxiomDialogState(
                                            "WALLPAPER UNAVAILABLE",
                                            "This device does not allow AXIOM to set the lock screen wallpaper directly, and no supported system wallpaper setup screen accepted the generated AXIOM wallpaper."
                                        )
                                    }
                                }
                            }
                        },
                        onHomeScreenWallpaper = {
                            val wallpaperManager = AxiomWallpaperManager(this@MainActivity)
                            when (wallpaperManager.setHomeScreenWallpaper()) {
                                AxiomWallpaperManager.Result.Applied -> {
                                    wallpaperDialog = AxiomDialogState("HOME SCREEN WALLPAPER", "AXIOM home-screen wallpaper applied.")
                                }
                                AxiomWallpaperManager.Result.Restricted -> {
                                    val fallbackOpened = wallpaperManager.createSystemSetterIntent()
                                        ?.let(::openWallpaperSetter)
                                        ?: false
                                    wallpaperDialog = if (fallbackOpened) {
                                        AxiomDialogState(
                                            "WALLPAPER SETUP OPENED",
                                            "Android blocked direct home-screen install, so AXIOM opened the system wallpaper setter with the AXIOM wallpaper."
                                        )
                                    } else {
                                        AxiomDialogState(
                                            "WALLPAPER UNAVAILABLE",
                                            "This device does not allow AXIOM to set the home-screen wallpaper directly, and no supported system wallpaper setup screen accepted the AXIOM wallpaper."
                                        )
                                    }
                                }
                            }
                        },
                        onBothWallpapers = {
                            val wallpaperManager = AxiomWallpaperManager(this@MainActivity)
                            when (wallpaperManager.setBothWallpapers()) {
                                AxiomWallpaperManager.Result.Applied -> {
                                    vm.setLockScreenWallpaperEnabled(true)
                                    wallpaperDialog = AxiomDialogState("AXIOM WALLPAPER", "AXIOM wallpaper applied to home and lock screens.")
                                }
                                AxiomWallpaperManager.Result.Restricted -> {
                                    val fallbackOpened = wallpaperManager.createSystemSetterIntent()
                                        ?.let(::openWallpaperSetter)
                                        ?: false
                                    if (fallbackOpened) {
                                        wallpaperDialog = AxiomDialogState(
                                            "WALLPAPER SETUP OPENED",
                                            "Android blocked direct wallpaper install, so AXIOM opened the system wallpaper setter. Choose home, lock, or both if your phone offers it."
                                        )
                                    } else {
                                        vm.setLockScreenWallpaperEnabled(false)
                                        wallpaperDialog = AxiomDialogState(
                                            "WALLPAPER UNAVAILABLE",
                                            "This device does not allow AXIOM to set wallpaper directly, and no supported system wallpaper setup screen accepted the AXIOM wallpaper."
                                        )
                                    }
                                }
                            }
                        },
                        onFavoritesCountSelected = vm::setFavoritesCount,
                        onFavoritesLockedChanged = vm::setFavoritesLocked,
                        onHomePanelCountSelected = vm::setHomePanelCount,
                        onOpenHiddenApps = { state = LauncherState.HiddenApps },
                        onOpenLeftFolderApps = { state = LauncherState.LeftFolderApps },
                        onOpenRightFolderApps = { state = LauncherState.RightFolderApps },
                        feedEnabled = feedState.enabled,
                        feedMinimalMode = feedState.minimalMode,
                        onFeedEnabledChanged = { enabled -> scope.launch { feedRepository.setEnabled(enabled) } },
                        onOpenFeedConfiguration = { state = LauncherState.FeedConfiguration },
                        onResetFeedLayout = { scope.launch { feedRepository.resetLayout() } },
                        onFeedMinimalModeChanged = { enabled -> scope.launch { feedRepository.setMinimalMode(enabled) } },
                        onFolderIconStyleSelected = vm::setFolderIconStyle,
                        onChooseDefaultLauncher = ::openDefaultLauncherSettings,
                        onOpenPhoneSettings = { SystemSettingsNavigator.openPhoneSettings(this@MainActivity) },
                        onLaunchShooter = { state = LauncherState.AxiomShooterTransition },
                        onOpenAbout = { state = LauncherState.About },
                        onOpenContact = { state = LauncherState.Contact },
                        onOpenPermissions = { state = LauncherState.PermissionSetup },
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
                        onReloadApps = vm::reloadApps,
                        onResetSettings = vm::resetSettings,
                        onOpenTerminal = { state = LauncherState.TerminalPanel }
                    )
                    LauncherState.FeedConfiguration -> FeedConfigurationScreen(
                        feedState = feedState,
                        onToggleEnabled = { module -> scope.launch { feedRepository.toggleModule(module.id, feedState.modules) } },
                        onToggleCollapsed = { module -> scope.launch { feedRepository.toggleCollapsed(module.id, feedState.modules) } },
                        onRemove = { module -> scope.launch { feedRepository.removeModule(module.id, feedState.modules) } },
                        onMoveUp = { module -> scope.launch { feedRepository.moveModule(module.id, -1, feedState.modules) } },
                        onMoveDown = { module -> scope.launch { feedRepository.moveModule(module.id, 1, feedState.modules) } },
                        onOpenLibrary = { state = LauncherState.FeedModuleLibrary }
                    )
                    LauncherState.FeedModuleLibrary -> FeedModuleLibraryScreen(
                        feedState = feedState,
                        onAddModule = { type ->
                            scope.launch { feedRepository.addModule(type, feedState.modules) }
                            state = LauncherState.FeedConfiguration
                        }
                    )
                    LauncherState.SystemPanel -> SystemPanelScreen(
                        settings = settings,
                        onOpenSettings = { state = LauncherState.Settings },
                        onOpenAppDrawer = { state = LauncherState.AppDrawer },
                        onOpenLeftFolder = { state = LauncherState.LeftFolder },
                        onOpenRightFolder = { state = LauncherState.RightFolder },
                        onReturnHome = { state = LauncherState.Home },
                        onPhone = { vm.openDialer(this@MainActivity) },
                        onMessages = { vm.openMessages(this@MainActivity) }
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
                    LauncherState.WidgetPicker -> WidgetPickerScreen(
                        settings = settings,
                        panelIndex = widgetPickerPanelIndex,
                        onSelectProvider = { provider ->
                            handleWidgetSelect(widgetPickerPanelIndex, provider)
                            state = LauncherState.Home
                        }
                    )
                    LauncherState.About -> AboutScreen(
                        settings = settings,
                        onOpenOuterVoid = { openUrl("https://github.com/Outer-Void") },
                        onOpenJustadude = { openUrl("https://github.com/Justadudeinspace") }
                    )
                    LauncherState.Contact -> ContactScreen(
                        settings = settings,
                        onOpenOuterVoid = { openUrl("https://github.com/Outer-Void") },
                        onOpenJustadude = { openUrl("https://github.com/Justadudeinspace") },
                        onEmail = ::openEmail
                    )
                    LauncherState.PermissionSetup -> PermissionSetupScreen(
                        settings = settings,
                        voidSysInstalled = voidSysInstalled,
                        onChooseDefaultLauncher = ::openDefaultLauncherSettings,
                        onOpenWidgetSettings = {
                            SystemSettingsNavigator.openDefaultLauncherSettings(this@MainActivity)
                        },
                        onRequestCalendar = { calendarPermLauncher.launch(android.Manifest.permission.READ_CALENDAR) },
                        onRequestContacts = { contactsPermLauncher.launch(android.Manifest.permission.READ_CONTACTS) },
                        onRequestLocation = {
                            locationPermLauncher.launch(
                                arrayOf(
                                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        },
                        onRequestNotifications = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                notificationsPermLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    )
                    LauncherState.TerminalPanel -> TerminalPanelScreen(
                        settings = settings,
                        onClose = { state = LauncherState.Home }
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
                widgetDialog?.let { dialog ->
                    AxiomMessageDialog(
                        title = dialog.title,
                        message = dialog.message,
                        accentColor = accent,
                        onDismiss = { widgetDialog = null }
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

    private fun openWallpaperSetter(intent: Intent): Boolean {
        return try {
            startActivity(intent)
            true
        } catch (_: ActivityNotFoundException) {
            false
        } catch (_: SecurityException) {
            false
        }
    }

    private fun openDefaultLauncherSettings() {
        SystemSettingsNavigator.openDefaultLauncherSettings(this)
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
