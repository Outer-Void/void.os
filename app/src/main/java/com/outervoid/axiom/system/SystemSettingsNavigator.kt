package com.outervoid.axiom.system

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.Settings

object SystemSettingsNavigator {
    fun openDefaultLauncherSettings(context: Context) {
        openFirst(
            context, listOf(
                Intent(Settings.ACTION_HOME_SETTINGS),
                Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS),
                Intent(Settings.ACTION_SETTINGS)
            )
        )
    }

    fun openPhoneSettings(context: Context) {
        open(context, Intent(Settings.ACTION_SETTINGS))
    }

    fun openSpecificSettings(context: Context, action: String) {
        openFirst(context, listOf(Intent(action), Intent(Settings.ACTION_SETTINGS)))
    }

    private fun openFirst(context: Context, intents: List<Intent>) {
        for (intent in intents) {
            if (open(context, intent)) return
        }
    }

    private fun open(context: Context, intent: Intent): Boolean = try {
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        true
    } catch (_: ActivityNotFoundException) {
        false
    } catch (_: SecurityException) {
        false
    }
}
