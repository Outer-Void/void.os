package com.outervoid.axiom.module

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ModuleStatusManager(private val context: Context) {
    private val appContext = context.applicationContext
    private val _voidSysInstalled = MutableStateFlow(isInstalled(VoidSysPackage))
    val voidSysInstalled: StateFlow<Boolean> = _voidSysInstalled

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val packageName = intent?.data?.schemeSpecificPart ?: return
            if (packageName == VoidSysPackage) {
                refresh()
            }
        }
    }

    fun register() {
        refresh()
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_CHANGED)
            addAction(Intent.ACTION_PACKAGE_REPLACED)
            addDataScheme("package")
        }
        appContext.registerReceiver(receiver, filter)
    }

    fun unregister() {
        runCatching { appContext.unregisterReceiver(receiver) }
    }

    fun refresh() {
        _voidSysInstalled.value = isInstalled(VoidSysPackage)
    }

    private fun isInstalled(packageName: String): Boolean = runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            appContext.packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            appContext.packageManager.getPackageInfo(packageName, 0)
        }
        true
    }.getOrDefault(false)

    companion object {
        const val VoidSysPackage = "com.outervoid.voidsys"
    }
}
