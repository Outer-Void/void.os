package com.outervoid.axiom.system

import android.annotation.SuppressLint
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SystemActionController(private val context: Context) {
    private val cameraManager = context.getSystemService(CameraManager::class.java)
    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
    private val _flashlightEnabled = MutableStateFlow(false)
    val flashlightEnabled: StateFlow<Boolean> = _flashlightEnabled.asStateFlow()
    private val _activeStates = MutableStateFlow(readActiveStates())
    val activeStates: StateFlow<Map<SystemAction, Boolean>> = _activeStates.asStateFlow()

    fun perform(action: SystemAction) {
        when (action) {
            SystemAction.Flashlight -> toggleFlashlight()
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
        refreshActiveStates()
    }

    fun refreshActiveStates() {
        _activeStates.value = readActiveStates()
    }

    private fun toggleFlashlight() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            open(Settings.ACTION_SETTINGS)
            return
        }

        try {
            val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
                cameraManager.getCameraCharacteristics(id)
                    .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            } ?: run {
                open(Settings.ACTION_SETTINGS)
                return
            }
            val nextState = !_flashlightEnabled.value
            cameraManager.setTorchMode(cameraId, nextState)
            _flashlightEnabled.value = nextState
        } catch (_: SecurityException) {
            open(Settings.ACTION_SETTINGS)
        } catch (_: Exception) {
            open(Settings.ACTION_SETTINGS)
        }
    }

    private fun readActiveStates(): Map<SystemAction, Boolean> = mapOf(
        SystemAction.Wifi to isWifiActive(),
        SystemAction.Bluetooth to isBluetoothEnabled()
    )

    private fun isWifiActive(): Boolean {
        return try {
            val network = connectivityManager.activeNetwork ?: return false
            connectivityManager.getNetworkCapabilities(network)
                ?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        } catch (_: SecurityException) {
            false
        }
    }

    @SuppressLint("MissingPermission")
    private fun isBluetoothEnabled(): Boolean = try {
        BluetoothAdapter.getDefaultAdapter()?.isEnabled == true
    } catch (_: SecurityException) {
        false
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
