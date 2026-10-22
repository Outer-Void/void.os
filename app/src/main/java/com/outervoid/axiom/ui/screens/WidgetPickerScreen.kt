package com.outervoid.axiom.ui.screens

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.outervoid.axiom.settings.AxiomSettings
import com.outervoid.axiom.settings.accentColor
import com.outervoid.axiom.ui.components.SettingsRow
import com.outervoid.axiom.ui.theme.Background
import com.outervoid.axiom.ui.theme.TextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicInteger

internal data class WidgetProviderEntry(
    val provider: AppWidgetProviderInfo,
    val label: String
)

internal data class WidgetPickerState(
    val providers: List<WidgetProviderEntry> = emptyList(),
    val loading: Boolean = true,
    val error: Boolean = false
)

internal fun <T> sortWidgetProviders(providers: List<T>, label: (T) -> String): List<T> =
    providers.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, label))

internal fun shouldPublishWidgetProviders(queryVersion: Int, currentVersion: Int): Boolean =
    queryVersion == currentVersion

@Composable
fun WidgetPickerScreen(
    settings: AxiomSettings,
    panelIndex: Int,
    onRemoveWidget: () -> Unit,
    onResetWidgets: () -> Unit,
    onSelectProvider: (AppWidgetProviderInfo) -> Unit
) {
    var resetConfirmation by remember { mutableStateOf(false) }
    val context = LocalContext.current.applicationContext
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val accent = settings.accentColor()
    var refreshKey by remember(context) { mutableIntStateOf(0) }
    val pickerState = produceState(WidgetPickerState(), context, lifecycle, refreshKey) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            val refreshRequests = Channel<Int>(Channel.CONFLATED)
            val version = AtomicInteger(0)
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent?.action in PACKAGE_CHANGE_ACTIONS) {
                        refreshRequests.trySend(version.incrementAndGet())
                    }
                }
            }
            val filter = IntentFilter().apply {
                PACKAGE_CHANGE_ACTIONS.forEach(::addAction)
                addDataScheme("package")
            }

            ContextCompat.registerReceiver(
                context,
                receiver,
                filter,
                ContextCompat.RECEIVER_EXPORTED
            )
            refreshRequests.trySend(version.incrementAndGet())
            try {
                for (requestVersion in refreshRequests) {
                    value = value.copy(loading = true, error = false)
                    val result = runCatching { withContext(Dispatchers.IO) { queryWidgetProviders(context) } }
                    if (!currentCoroutineContext().isActive) break
                    if (!shouldPublishWidgetProviders(requestVersion, version.get())) continue
                    result.fold(
                        onSuccess = { value = WidgetPickerState(providers = it, loading = false) },
                        onFailure = {
                            value = value.copy(loading = false, error = true)
                        }
                    )
                }
            } finally {
                refreshRequests.close()
                runCatching { context.unregisterReceiver(receiver) }
            }
        }
    }.value

    if (resetConfirmation) {
        AlertDialog(onDismissRequest = { resetConfirmation = false }, title = { Text("RESET WIDGETS?") },
            text = { Text("Remove all AXIOM widget assignments and unfinished setups? Other settings stay unchanged.") },
            confirmButton = { TextButton(onClick = { resetConfirmation = false; onResetWidgets() }) { Text("RESET") } },
            dismissButton = { TextButton(onClick = { resetConfirmation = false }) { Text("CANCEL") } })
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 18.dp, vertical = 22.dp)
    ) {
        Text("WIDGETS", color = accent, style = MaterialTheme.typography.titleLarge)
        Text("PANEL ${panelIndex + 1}", color = TextSecondary, style = MaterialTheme.typography.labelLarge)
        Spacer(
            Modifier
                .padding(top = 10.dp, bottom = 8.dp)
                .width(104.dp)
                .height(1.dp)
                .background(accent)
        )
        Text(
            "Widget binding requires device permission. AXIOM will request it if not yet granted.",
            color = TextSecondary,
            style = MaterialTheme.typography.bodySmall
        )
        if (settings.widgetForPanel(panelIndex) != null) {
            TextButton(onClick = onRemoveWidget) { Text("REMOVE WIDGET", color = accent) }
        }
        TextButton(onClick = { resetConfirmation = true }) { Text("RESET WIDGETS", color = TextSecondary) }
        Spacer(Modifier.height(12.dp))
        when {
            pickerState.loading && pickerState.providers.isEmpty() ->
                Text("SCANNING WIDGETS…", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
            pickerState.error && pickerState.providers.isEmpty() ->
                Text(
                    "WIDGET LIST UNAVAILABLE · RETRY",
                    modifier = Modifier.clickable { refreshKey++ },
                    color = accent,
                    style = MaterialTheme.typography.bodyMedium
                )
            pickerState.providers.isEmpty() ->
                Text("No widgets found on this device.", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
            else -> {
                if (pickerState.error) {
                    Text(
                        "WIDGET LIST MAY BE OUT OF DATE · RETRY",
                        modifier = Modifier.clickable { refreshKey++ },
                        color = TextSecondary,
                        style = MaterialTheme.typography.labelSmall
                    )
                } else if (pickerState.loading) {
                    Text("UPDATING WIDGETS…", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                }
                LazyColumn {
                    items(pickerState.providers, key = { it.provider.provider.flattenToShortString() }) { entry ->
                        SettingsRow(
                            title = entry.label,
                            subtitle = entry.provider.provider.flattenToShortString(),
                            value = "SELECT",
                            onClick = { onSelectProvider(entry.provider) }
                        )
                    }
                }
            }
        }
    }
}

private suspend fun queryWidgetProviders(context: Context): List<WidgetProviderEntry> =
    sortWidgetProviders(
        AppWidgetManager.getInstance(context).installedProviders.map { provider ->
            WidgetProviderEntry(provider, provider.loadLabel(context.packageManager).toString())
        },
        WidgetProviderEntry::label
    )

private val PACKAGE_CHANGE_ACTIONS = setOf(
    Intent.ACTION_PACKAGE_ADDED,
    Intent.ACTION_PACKAGE_REMOVED,
    Intent.ACTION_PACKAGE_CHANGED,
    Intent.ACTION_PACKAGE_REPLACED
)
