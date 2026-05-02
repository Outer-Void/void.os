package com.outervoid.axiom.security

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

class PermissionManager(private val context: Context) {
    fun hasWidgetBindPermission(): Boolean =
        context.packageManager.checkPermission(
            "android.permission.BIND_APPWIDGET",
            context.packageName
        ) == PackageManager.PERMISSION_GRANTED

    fun isDefaultLauncher(): Boolean {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        val resolved = context.packageManager.resolveActivity(
            intent, PackageManager.MATCH_DEFAULT_ONLY
        )
        return resolved?.activityInfo?.packageName == context.packageName
    }
}
