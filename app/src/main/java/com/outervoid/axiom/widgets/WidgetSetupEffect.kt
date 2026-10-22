package com.outervoid.axiom.widgets

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.repeatOnLifecycle
import com.outervoid.axiom.vm.LauncherViewModel
import kotlinx.coroutines.awaitCancellation

/** Stable operation/stage registry keys fence stale results across Activity and process recreation. */
@Composable
fun WidgetSetupEffect(activity: ComponentActivity, vm: LauncherViewModel, widgetHost: AxiomWidgetHost) {
    val snapshot by vm.widgetSnapshots.collectAsState(initial = WidgetSnapshot())
    val widgetError by vm.widgetError.collectAsState()
    val op = snapshot.pending
    val manager = remember(activity) { AppWidgetManager.getInstance(activity) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    var launcher by remember { mutableStateOf<ActivityResultLauncher<Intent>?>(null) }
    var inFlight by remember(op?.id, op?.stage) { mutableStateOf(false) }

    LaunchedEffect(lifecycle, vm, widgetHost) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            try {
                widgetHost.startListening()
                vm.widgetAction { vm.widgetTransactions.recover() }
                awaitCancellation()
            } finally { widgetHost.stopListening() }
        }
    }

    DisposableEffect(lifecycle, op?.id, op?.stage) {
        val observer = LifecycleEventObserver { _, event ->
            // HOME can return here without completing an external provider Activity.
            if (event == Lifecycle.Event.ON_RESUME) inFlight = false
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    fun failLaunch(message: String) {
        inFlight = false
        vm.widgetAction { error(message) }
    }

    DisposableEffect(op?.id, op?.stage, activity) {
        if (op == null || op.stage == WidgetStage.Prepared) {
            onDispose { }
        } else {
            val registration = activity.activityResultRegistry.register(
                "axiom.widget.${op.id}.${op.stage}", ActivityResultContracts.StartActivityForResult()
            ) { result ->
                inFlight = false
                vm.widgetAction {
                    if (result.resultCode != Activity.RESULT_OK) vm.widgetTransactions.cancel(op.id)
                    else if (op.stage == WidgetStage.Binding) {
                        val info = manager.getAppWidgetInfo(op.candidate)
                        if (info?.provider?.flattenToString() != op.provider) {
                            vm.widgetTransactions.cancel(op.id)
                            error("Widget provider is no longer available.")
                        } else if (info.configure != null) {
                            vm.widgetTransactions.advance(op.id, WidgetStage.Binding, WidgetStage.Configuring)
                        } else vm.widgetTransactions.commit(op.id, WidgetStage.Binding)
                    } else vm.widgetTransactions.commit(op.id, WidgetStage.Configuring)
                }
            }
            launcher = registration
            onDispose { launcher = null; registration.unregister() }
        }
    }

    fun launchExternal() {
        if (op == null || inFlight) return
        vm.widgetAction {
            if (op.stage == WidgetStage.Prepared) {
                // A durable Binding stage exists before bind/permission side effects.
                vm.widgetTransactions.advance(op.id, WidgetStage.Prepared, WidgetStage.Binding)
                return@widgetAction
            }
            val intent = if (op.stage == WidgetStage.Binding) {
                val bound = manager.getAppWidgetInfo(op.candidate)?.provider?.flattenToString() == op.provider ||
                    manager.bindAppWidgetIdIfAllowed(op.candidate, ComponentName.unflattenFromString(op.provider))
                if (bound) {
                    val info = manager.getAppWidgetInfo(op.candidate)
                    if (info?.configure != null) vm.widgetTransactions.advance(op.id, WidgetStage.Binding, WidgetStage.Configuring)
                    else vm.widgetTransactions.commit(op.id, WidgetStage.Binding)
                    return@widgetAction
                }
                Intent(AppWidgetManager.ACTION_APPWIDGET_BIND).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, op.candidate)
                    .putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, ComponentName.unflattenFromString(op.provider))
            } else {
                val info = manager.getAppWidgetInfo(op.candidate)
                check(info?.provider?.flattenToString() == op.provider && info.configure != null) { "Widget configuration is unavailable; cancel this setup." }
                Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE).setComponent(info!!.configure)
                    .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, op.candidate)
            }
            try {
                val ready = launcher ?: return@widgetAction
                inFlight = true
                ready.launch(intent)
            } catch (e: ActivityNotFoundException) { failLaunch("Widget setup activity is unavailable: ${e.message}. Cancel or retry setup.") }
            catch (e: SecurityException) { failLaunch("Widget setup was denied: ${e.message}. Cancel or retry setup.") }
        }
    }

    LaunchedEffect(op?.id, op?.stage) {
        if (op?.stage == WidgetStage.Prepared) {
            vm.widgetAction { vm.widgetTransactions.advance(op.id, WidgetStage.Prepared, WidgetStage.Binding) }
        }
    }

    // Explicit continuation also closes the crash window between durable stage write and external launch.
    if (op != null && op.stage != WidgetStage.Prepared && widgetError == null && !inFlight) {
        AlertDialog(onDismissRequest = {}, title = { Text("WIDGET SETUP") },
            text = { Text("Your current widget stays in place until setup is saved. Continue setup or cancel this replacement.") },
            confirmButton = { TextButton(onClick = { launchExternal() }) { Text("CONTINUE") } },
            dismissButton = { TextButton(onClick = { vm.widgetAction { vm.widgetTransactions.cancel(op.id) } }) { Text("CANCEL") } })
    }
    if (widgetError != null) {
        AlertDialog(onDismissRequest = vm::dismissWidgetError, title = { Text("WIDGET SETUP FAILED") },
            text = { Text(widgetError!!) }, confirmButton = { TextButton(onClick = vm::dismissWidgetError) { Text("CLOSE") } },
            dismissButton = { TextButton(onClick = vm::retryWidgetStorage) { Text("RETRY") } })
    }
}
