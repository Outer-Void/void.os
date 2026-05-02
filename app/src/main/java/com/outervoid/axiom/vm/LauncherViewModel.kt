package com.outervoid.axiom.vm

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.outervoid.axiom.data.AppInfo
import com.outervoid.axiom.icon.BundledVoidSysIconProvider
import com.outervoid.axiom.settings.AxiomSettings
import com.outervoid.axiom.settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LauncherViewModel(app: Application) : AndroidViewModel(app) {
    private val settingsRepository = SettingsRepository(app)

    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()
    val filteredApps: StateFlow<List<AppInfo>> = combine(_apps, _query) { apps, query ->
        val normalized = query.trim()
        if (normalized.isEmpty()) apps else apps.filter { app ->
            app.label.contains(normalized, ignoreCase = true) ||
                app.packageName.contains(normalized, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val settings: StateFlow<AxiomSettings> = settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AxiomSettings.Defaults)
    private val _voidSysInstalled = MutableStateFlow(settingsRepository.isVoidSysInstalled())
    val voidSysInstalled: StateFlow<Boolean> = _voidSysInstalled.asStateFlow()

    private var receiverRegistered = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            _voidSysInstalled.value = settingsRepository.isVoidSysInstalled()
            loadApps()
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
        loadApps()
        viewModelScope.launch {
            settings
                .map { it.selectedIconPackPackage }
                .distinctUntilChanged()
                .collect { loadApps() }
        }
    }

    override fun onCleared() {
        if (receiverRegistered) {
            runCatching { getApplication<Application>().unregisterReceiver(receiver) }
            receiverRegistered = false
        }
        super.onCleared()
    }

    fun loadApps() = viewModelScope.launch {
        val application = getApplication<Application>()
        val pm = application.packageManager
        val iconProvider = BundledVoidSysIconProvider(application, settings.value.selectedIconPackPackage)
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        _apps.value = pm.queryIntentActivities(intent, 0)
            .mapNotNull { resolveInfo ->
                val activityInfo = resolveInfo.activityInfo ?: return@mapNotNull null
                AppInfo(
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
            .filter { it.packageName != application.packageName }
            .sortedBy { it.label.lowercase() }
    }

    fun setQuery(q: String) {
        _query.value = q
    }

    fun setFont(key: String) = viewModelScope.launch { settingsRepository.updateFont(key) }
    fun setIconPack(packageName: String) = viewModelScope.launch { settingsRepository.updateIconPack(packageName) }
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
        runCatching { context.startActivity(intent) }
    }

    fun openAppInfo(context: Context, app: AppInfo) {
        val intent = Intent(
            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:${app.packageName}")
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun uninstallApp(context: Context, app: AppInfo) {
        val intent = Intent(Intent.ACTION_DELETE, Uri.parse("package:${app.packageName}"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            openAppInfo(context, app)
        }
    }
}
