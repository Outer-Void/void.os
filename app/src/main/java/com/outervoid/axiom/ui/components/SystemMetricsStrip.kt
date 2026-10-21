package com.outervoid.axiom.ui.components

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.outervoid.axiom.ui.theme.Amber
import com.outervoid.axiom.ui.theme.Border
import com.outervoid.axiom.ui.theme.BorderDim
import com.outervoid.axiom.ui.theme.Surface
import com.outervoid.axiom.ui.theme.SurfaceAlt
import com.outervoid.axiom.ui.theme.TextDim
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

internal data class SystemMetrics(
    val batteryPercent: Int? = null,
    val storagePercent: Int? = null,
    val ramPercent: Int? = null
)

internal fun batteryPercent(level: Int, scale: Int): Int? =
    if (level < 0 || scale <= 0) null else ((level * 100) / scale).coerceIn(0, 100)

internal fun formatMetricPercent(value: Int?): String = value?.let { "$it%" } ?: "--"

private const val METRICS_REFRESH_INTERVAL_MS = 15_000L

@Composable
internal fun SystemMetricsStrip(
    accentColor: androidx.compose.ui.graphics.Color = Amber,
    compactMode: Boolean
) {
    val context = LocalContext.current.applicationContext
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val metrics = produceState(initialValue = SystemMetrics(), context, lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent?.action != Intent.ACTION_BATTERY_CHANGED) return
                    val percent = batteryPercent(
                        intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1),
                        intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                    )
                    value = value.copy(batteryPercent = percent)
                }
            }
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            try {
                val sticky = ContextCompat.registerReceiver(
                    context,
                    receiver,
                    filter,
                    ContextCompat.RECEIVER_NOT_EXPORTED
                )
                sticky?.let { receiver.onReceive(context, it) }

                while (true) {
                    val storageAndRam = withContext(Dispatchers.IO) { readStorageAndRam() }
                    value = value.copy(
                        storagePercent = storageAndRam.storagePercent,
                        ramPercent = storageAndRam.ramPercent
                    )
                    delay(METRICS_REFRESH_INTERVAL_MS)
                }
            } finally {
                runCatching { context.unregisterReceiver(receiver) }
            }
        }
    }.value

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(7.dp))
            .border(1.dp, Border, RoundedCornerShape(7.dp))
            .padding(horizontal = if (compactMode) 8.dp else 10.dp, vertical = if (compactMode) 7.dp else 9.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        listOf(
            "BAT" to formatMetricPercent(metrics.batteryPercent),
            "NET" to "SYS",
            "STOR" to formatMetricPercent(metrics.storagePercent),
            "RAM" to formatMetricPercent(metrics.ramPercent)
        ).forEach { (label, value) ->
            Column(
                modifier = Modifier
                    .weight(1f)
                    .background(SurfaceAlt, RoundedCornerShape(5.dp))
                    .border(1.dp, BorderDim, RoundedCornerShape(5.dp))
                    .padding(vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(label, color = TextDim, style = MaterialTheme.typography.labelSmall)
                Text(
                    value,
                    color = accentColor,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

private fun readStorageAndRam(): SystemMetrics {
    val storage = runCatching {
        val stat = StatFs(Environment.getDataDirectory().path)
        val total = stat.blockCountLong * stat.blockSizeLong
        val free = stat.availableBlocksLong * stat.blockSizeLong
        if (total > 0L) (((total - free) * 100) / total).toInt().coerceIn(0, 100) else null
    }.getOrNull()

    val ram = runCatching {
        val runtime = Runtime.getRuntime()
        val maxMb = runtime.maxMemory() / (1024 * 1024)
        if (maxMb > 0) {
            val usedMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024)
            ((usedMb * 100) / maxMb).toInt().coerceIn(0, 100)
        } else {
            null
        }
    }.getOrNull()

    return SystemMetrics(storagePercent = storage, ramPercent = ram)
}
