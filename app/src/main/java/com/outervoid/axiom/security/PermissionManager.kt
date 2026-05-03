package com.outervoid.axiom.security

import android.Manifest
import android.app.AppOpsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat

class PermissionManager(private val context: Context) {

    fun isDefaultLauncher(): Boolean {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        val resolved = context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolved?.activityInfo?.packageName == context.packageName
    }

    fun hasNotificationListenerAccess(): Boolean {
        val flat = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners") ?: return false
        return flat.split(":").any { it.startsWith(context.packageName) }
    }

    fun hasUsageStatsAccess(): Boolean {
        return try {
            val ops = context.getSystemService(AppOpsManager::class.java)
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ops.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
            } else {
                @Suppress("DEPRECATION")
                ops.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName)
            }
            mode == AppOpsManager.MODE_ALLOWED
        } catch (_: Exception) {
            false
        }
    }

    fun hasCalendarPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED

    fun hasContactsPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED

    fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    fun hasNotificationsPermission(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else true

    fun hasWidgetSetupPath(): Boolean = true

    fun isTermuxInstalled(): Boolean =
        runCatching { context.packageManager.getPackageInfo("com.termux", 0); true }.getOrDefault(false)

    fun openNotificationListenerSettings() {
        openFirst(listOf(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"), Intent(Settings.ACTION_SETTINGS)))
    }

    fun openUsageAccessSettings() {
        openFirst(listOf(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), Intent(Settings.ACTION_SETTINGS)))
    }

    fun openDefaultLauncherSettings() {
        openFirst(listOf(Intent(Settings.ACTION_HOME_SETTINGS), Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS), Intent(Settings.ACTION_SETTINGS)))
    }

    fun openLocationSettings() {
        openFirst(listOf(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), Intent(Settings.ACTION_SETTINGS)))
    }

    private fun openFirst(intents: List<Intent>) {
        for (intent in intents) {
            try {
                context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                return
            } catch (_: Exception) {}
        }
    }
}
