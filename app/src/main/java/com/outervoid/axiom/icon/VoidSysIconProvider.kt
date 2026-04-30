package com.outervoid.axiom.icon

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable

class VoidSysIconProvider(
    private val context: Context,
    private val fallbackProvider: IconProvider = SystemIconProvider()
) : IconProvider {
    private val packageManager = context.packageManager

    override fun iconFor(packageName: String, fallback: Drawable): Drawable {
        if (!isVoidSysInstalled()) return fallbackProvider.iconFor(packageName, fallback)

        return runCatching {
            if (packageName == VOID_SYS_PACKAGE) {
                packageManager.getApplicationIcon(VOID_SYS_PACKAGE)
            } else {
                fallbackProvider.iconFor(packageName, fallback)
            }
        }.getOrElse { fallbackProvider.iconFor(packageName, fallback) }
    }

    private fun isVoidSysInstalled(): Boolean = try {
        packageManager.getPackageInfo(VOID_SYS_PACKAGE, 0)
        true
    } catch (_: PackageManager.NameNotFoundException) {
        false
    }

    private companion object {
        const val VOID_SYS_PACKAGE = "com.outervoid.voidsys"
    }
}
