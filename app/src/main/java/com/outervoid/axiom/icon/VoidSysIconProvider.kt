package com.outervoid.axiom.icon

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.outervoid.axiom.settings.IconPackOptions

class VoidSysIconProvider(
    private val context: Context,
    private val selectedIconPackPackage: String,
    private val fallbackProvider: IconProvider = SystemIconProvider()
) : IconProvider {
    private val packageManager = context.packageManager

    override fun iconFor(packageName: String, fallback: Drawable): Drawable {
        if (selectedIconPackPackage != IconPackOptions.VoidSysPackage || !isVoidSysInstalled()) {
            return fallbackProvider.iconFor(packageName, fallback)
        }

        return runCatching {
            if (packageName == IconPackOptions.VoidSysPackage) {
                packageManager.getApplicationIcon(IconPackOptions.VoidSysPackage)
            } else {
                fallbackProvider.iconFor(packageName, fallback)
            }
        }.getOrElse { fallbackProvider.iconFor(packageName, fallback) }
    }

    private fun isVoidSysInstalled(): Boolean = try {
        packageManager.getPackageInfo(IconPackOptions.VoidSysPackage, 0)
        true
    } catch (_: PackageManager.NameNotFoundException) {
        false
    }
}
