package com.outervoid.axiom.vm

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.outervoid.axiom.data.AppInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LauncherViewModel(app: Application) : AndroidViewModel(app) {
    private val _apps = MutableStateFlow<List<AppInfo>>(emptyList())
    val apps: StateFlow<List<AppInfo>> = _apps.asStateFlow()
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) { loadApps() }
    }

    init {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED); addAction(Intent.ACTION_PACKAGE_REMOVED); addAction(Intent.ACTION_PACKAGE_CHANGED)
            addDataScheme("package")
        }
        app.registerReceiver(receiver, filter)
        loadApps()
    }

    override fun onCleared() {
        getApplication<Application>().unregisterReceiver(receiver)
        super.onCleared()
    }

    fun loadApps() = viewModelScope.launch {
        val pm = getApplication<Application>().packageManager
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        _apps.value = pm.queryIntentActivities(intent, PackageManager.MATCH_ALL)
            .map {
                AppInfo(it.activityInfo.packageName, it.loadLabel(pm).toString(), it.loadIcon(pm), pm.getLaunchIntentForPackage(it.activityInfo.packageName))
            }
            .filter { it.packageName != getApplication<Application>().packageName }
            .sortedBy { it.label.lowercase() }
    }

    fun setQuery(q: String) { _query.value = q }
    fun filteredApps(): List<AppInfo> = apps.value.filter { it.label.contains(query.value, true) }
    fun launchApp(context: Context, app: AppInfo) { app.launchIntent?.let { context.startActivity(it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) } }
    fun openAppInfo(context: Context, app: AppInfo) = context.startActivity(Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${app.packageName}")))
    fun uninstallApp(context: Context, app: AppInfo) = context.startActivity(Intent(Intent.ACTION_DELETE, Uri.parse("package:${app.packageName}")))
}
