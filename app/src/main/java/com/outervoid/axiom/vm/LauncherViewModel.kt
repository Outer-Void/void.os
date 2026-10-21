package com.outervoid.axiom.vm

import android.app.Application
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.SystemClock
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.outervoid.axiom.data.AppInfo
import com.outervoid.axiom.icon.BundledVoidSysIconProvider
import com.outervoid.axiom.settings.AxiomSettings
import com.outervoid.axiom.settings.ClockFormat
import com.outervoid.axiom.settings.SettingsRepository
import com.outervoid.axiom.settings.appKey
import com.outervoid.axiom.ui.iconshape.IconShape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

class LauncherViewModel(app: Application) : AndroidViewModel(app) {
    private val settingsRepository = SettingsRepository(app)

    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()
    private val _appInventoryLoading = MutableStateFlow(true)
    val appInventoryLoading: StateFlow<Boolean> = _appInventoryLoading.asStateFlow()
    private val _appInventoryError = MutableStateFlow(false)
    val appInventoryError: StateFlow<Boolean> = _appInventoryError.asStateFlow()
    private val _lastInventoryRefreshDurationMillis = MutableStateFlow<Long?>(null)
    val lastInventoryRefreshDurationMillis: StateFlow<Long?> = _lastInventoryRefreshDurationMillis.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()
    val settings: StateFlow<AxiomSettings> = settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AxiomSettings.Defaults)
    val drawerApps: StateFlow<List<AppInfo>> = combine(_apps, settings) { apps, settings ->
        apps.filterNot { app -> appKey(app.packageName, app.activityName) in settings.hiddenApps }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val filteredApps: StateFlow<List<AppInfo>> = combine(drawerApps, _query) { apps, query ->
        val normalized = query.trim()
        if (normalized.isEmpty()) apps else apps.filter { app ->
            app.label.contains(normalized, ignoreCase = true) ||
                app.packageName.contains(normalized, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    private val _voidSysInstalled = MutableStateFlow(settingsRepository.isVoidSysInstalled())
    val voidSysInstalled: StateFlow<Boolean> = _voidSysInstalled.asStateFlow()

    private var receiverRegistered = false
    private var lastSuccessfulInventoryRefresh = 0L
    private var requestedIconPackPackage = settings.value.selectedIconPackPackage
    private val inventoryRefresher = CoalescingRefreshRunner(
        scope = viewModelScope,
        dispatcher = Dispatchers.IO,
        traceName = "AxiomAppInventoryRefresh",
        load = ::queryAppInventory,
        onLoadingChanged = { _appInventoryLoading.value = it },
        onResult = { result ->
            result.onSuccess { discovered ->
                _apps.value = discovered
                _appInventoryError.value = false
                lastSuccessfulInventoryRefresh = SystemClock.elapsedRealtime()
            }.onFailure {
                // Keep a previously loaded inventory usable if package queries temporarily fail.
                _appInventoryError.value = true
            }
        },
        onDurationMillis = { _lastInventoryRefreshDurationMillis.value = it }
    )

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            _voidSysInstalled.value = settingsRepository.isVoidSysInstalled()
            refreshApps()
        }
    }

    init {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addDataScheme("package")
        }
        ContextCompat.registerReceiver(app, receiver, filter, ContextCompat.RECEIVER_EXPORTED)
        receiverRegistered = true
        refreshApps()
        viewModelScope.launch {
            settings
                .map { it.selectedIconPackPackage }
                .distinctUntilChanged()
                .collect { packageName ->
                    if (packageName != requestedIconPackPackage) {
                        requestedIconPackPackage = packageName
                        refreshApps()
                    }
                }
        }
    }

    override fun onCleared() {
        if (receiverRegistered) {
            runCatching { getApplication<Application>().unregisterReceiver(receiver) }
            receiverRegistered = false
        }
        super.onCleared()
    }

    fun refreshAppsIfStale() {
        if (AppInventoryRefreshPolicy.isDue(lastSuccessfulInventoryRefresh, SystemClock.elapsedRealtime())) {
            refreshApps()
        }
    }

    fun reloadApps() {
        refreshApps()
    }

    private fun refreshApps() {
        inventoryRefresher.refresh()
    }

    private suspend fun queryAppInventory(): List<AppInfo> {
        val application = getApplication<Application>()
        val pm = application.packageManager
        val iconProvider = BundledVoidSysIconProvider(application, settings.value.selectedIconPackPackage)
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val discovered = ArrayList<AppInfo>()
        for (resolveInfo in pm.queryIntentActivities(intent, 0)) {
            currentCoroutineContext().ensureActive()
            val activityInfo = resolveInfo.activityInfo ?: continue
            if (activityInfo.packageName != application.packageName) {
                discovered += AppInfo(
                    packageName = activityInfo.packageName,
                    activityName = activityInfo.name,
                    label = resolveInfo.loadLabel(pm).toString(),
                    icon = iconProvider.iconFor(
                        packageName = activityInfo.packageName,
                        activityName = activityInfo.name,
                        fallback = resolveInfo.loadIcon(pm)
                    )
                )
            }
        }
        currentCoroutineContext().ensureActive()
        return AppInventoryOrdering.sort(discovered) { it.label }
    }

    fun setQuery(q: String) {
        _query.value = q
    }

    fun setFont(key: String) = viewModelScope.launch { settingsRepository.updateFont(key) }
    fun setIconPack(packageName: String) = viewModelScope.launch { settingsRepository.updateIconPack(packageName) }
    fun setIconShape(shape: IconShape) = viewModelScope.launch { settingsRepository.updateIconShape(shape) }
    fun setFavoritesCount(count: Int) = viewModelScope.launch { settingsRepository.updateFavoritesCount(count) }
    fun setAccentColor(key: String) = viewModelScope.launch { settingsRepository.updateAccentColor(key) }
    fun setFavoritesLocked(locked: Boolean) = viewModelScope.launch { settingsRepository.updateFavoritesLocked(locked) }
    fun setHomePanelCount(count: Int) = viewModelScope.launch { settingsRepository.updateHomePanelCount(count) }
    fun setHiddenApps(keys: Set<String>) = viewModelScope.launch { settingsRepository.updateHiddenApps(keys) }
    fun setClockFormat(format: ClockFormat) = viewModelScope.launch { settingsRepository.updateClockFormat(format) }
    fun setLockScreenWallpaperEnabled(enabled: Boolean) =
        viewModelScope.launch { settingsRepository.updateLockScreenWallpaperEnabled(enabled) }
    fun setLeftFolderName(name: String) = viewModelScope.launch { settingsRepository.updateLeftFolderName(name) }
    fun setRightFolderName(name: String) = viewModelScope.launch { settingsRepository.updateRightFolderName(name) }
    fun setLeftFolderApps(keys: Set<String>) = viewModelScope.launch { settingsRepository.updateLeftFolderApps(keys) }
    fun setRightFolderApps(keys: Set<String>) = viewModelScope.launch { settingsRepository.updateRightFolderApps(keys) }
    fun setFolderIconStyle(style: String) = viewModelScope.launch { settingsRepository.updateFolderIconStyle(style) }
    fun setFavoriteAppKeys(keys: Set<String>) = viewModelScope.launch { settingsRepository.updateFavoriteAppKeys(keys) }
    fun setShowAppLabels(enabled: Boolean) = viewModelScope.launch { settingsRepository.updateShowAppLabels(enabled) }
    fun setGridColumns(columns: Int) = viewModelScope.launch { settingsRepository.updateGridColumns(columns) }
    fun setIconScale(scale: Float) = viewModelScope.launch { settingsRepository.updateIconScale(scale) }
    fun setShowSystemOnline(enabled: Boolean) = viewModelScope.launch { settingsRepository.updateShowSystemOnline(enabled) }
    fun setAmbientBoot(enabled: Boolean) = viewModelScope.launch { settingsRepository.updateAmbientBoot(enabled) }
    fun setAmberAccent(enabled: Boolean) = viewModelScope.launch { settingsRepository.updateAmberAccent(enabled) }
    fun setCompactMode(enabled: Boolean) = viewModelScope.launch { settingsRepository.updateCompactMode(enabled) }
    fun setHideStatusBar(enabled: Boolean) = viewModelScope.launch { settingsRepository.updateHideStatusBar(enabled) }
    fun setHideNavigationBar(enabled: Boolean) = viewModelScope.launch { settingsRepository.updateHideNavigationBar(enabled) }
    fun setSearchOnSwipeUp(enabled: Boolean) = viewModelScope.launch { settingsRepository.updateSearchOnSwipeUp(enabled) }
    fun setSystemPanel(enabled: Boolean) = viewModelScope.launch { settingsRepository.updateSystemPanel(enabled) }
    fun setTerminalPanel(enabled: Boolean) = viewModelScope.launch { settingsRepository.updateTerminalPanel(enabled) }
    fun setHapticFeedback(enabled: Boolean) = viewModelScope.launch { settingsRepository.updateHapticFeedback(enabled) }
    fun resetSettings() = viewModelScope.launch { settingsRepository.reset() }

    fun launchApp(context: Context, app: AppInfo) {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            component = ComponentName(app.packageName, app.activityName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        }
        safeLaunch { context.startActivity(intent) }
    }

    fun openAppInfo(context: Context, app: AppInfo) {
        val intent = Intent(
            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:${app.packageName}")
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        safeLaunch { context.startActivity(intent) }
    }

    fun setWidgetForPanel(panelIndex: Int, widgetId: Int?) =
        viewModelScope.launch { settingsRepository.updatePanelWidget(panelIndex, widgetId) }

    fun openDialer(context: Context) {
        val intent = Intent(Intent.ACTION_DIAL).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        safeLaunch { context.startActivity(intent) }
    }

    fun openMessages(context: Context) {
        val candidates = buildList {
            add(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_MESSAGING)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            add(Intent(Intent.ACTION_VIEW, android.net.Uri.parse("smsto:"))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            listOf(
                "com.google.android.apps.messaging",
                "com.samsung.android.messaging",
                "com.android.mms"
            ).forEach { pkg ->
                context.packageManager.getLaunchIntentForPackage(pkg)?.let(::add)
            }
        }
        for (intent in candidates) {
            if (safeLaunch { context.startActivity(intent) }) return
        }
    }

    fun uninstallApp(context: Context, app: AppInfo) {
        val intent = Intent(Intent.ACTION_DELETE, Uri.parse("package:${app.packageName}"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        if (!safeLaunch { context.startActivity(intent) }) openAppInfo(context, app)
    }
}

internal object AppInventoryRefreshPolicy {
    private const val REFRESH_INTERVAL_MS = 30_000L

    fun isDue(lastSuccessfulRefreshMillis: Long, nowMillis: Long): Boolean =
        nowMillis - lastSuccessfulRefreshMillis >= REFRESH_INTERVAL_MS
}

internal object AppInventoryOrdering {
    fun <T> sort(apps: List<T>, label: (T) -> String): List<T> =
        apps.sortedBy { label(it).lowercase() }
}

internal fun safeLaunch(launch: () -> Unit): Boolean =
    try {
        launch()
        true
    } catch (_: Exception) {
        false
    }
