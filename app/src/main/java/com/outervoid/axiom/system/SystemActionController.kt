package com.outervoid.axiom.system

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.provider.Settings
import androidx.core.content.ContextCompat

class SystemActionController(private val context: Context) {
    fun perform(action: SystemAction) {
        when (action) {
            SystemAction.Flashlight -> toggleFlashlightOrOpenSettings()
            SystemAction.Wifi -> open(Settings.ACTION_WIFI_SETTINGS)
            SystemAction.Bluetooth -> open(Settings.ACTION_BLUETOOTH_SETTINGS)
            SystemAction.Battery -> open(Settings.ACTION_BATTERY_SAVER_SETTINGS)
            SystemAction.Data -> open(Settings.ACTION_DATA_ROAMING_SETTINGS)
            SystemAction.DarkMode -> open(Settings.ACTION_DISPLAY_SETTINGS)
            SystemAction.Location -> open(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            SystemAction.Hotspot -> open(Settings.ACTION_WIRELESS_SETTINGS)
            SystemAction.Nfc -> open(Settings.ACTION_NFC_SETTINGS)
            SystemAction.Sync -> open(Settings.ACTION_SYNC_SETTINGS)
            SystemAction.Airplane -> open(Settings.ACTION_AIRPLANE_MODE_SETTINGS)
            SystemAction.DoNotDisturb -> open(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            SystemAction.Security -> open(Settings.ACTION_SECURITY_SETTINGS)
            SystemAction.Storage -> open(Settings.ACTION_INTERNAL_STORAGE_SETTINGS)
            SystemAction.Users -> open(ACTION_USER_SETTINGS)
            SystemAction.Developer -> open(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
        }
    }

    private fun toggleFlashlightOrOpenSettings() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            open(Settings.ACTION_SETTINGS)
            return
        }

        runCatching {
            val cameraManager = context.getSystemService(CameraManager::class.java)
            val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
                cameraManager.getCameraCharacteristics(id)
                    .get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            } ?: return@runCatching
            cameraManager.setTorchMode(cameraId, true)
        }.getOrElse {
            open(Settings.ACTION_SETTINGS)
        }
    }

    private fun open(action: String) {
        val intent = Intent(action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            runCatching {
                context.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }
        }
    }

    private companion object {
        const val ACTION_USER_SETTINGS = "android.settings.USER_SETTINGS"
    }
}
