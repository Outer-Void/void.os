package com.outervoid.axiom.security

import android.content.Context
import com.outervoid.axiom.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EntitlementManager(context: Context) {
    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val integrityGate = IntegrityGate(appContext)
    private val _state = MutableStateFlow(EntitlementState.Unknown)
    val state: StateFlow<EntitlementState> = _state

    fun initialize() {
        scope.launch {
            _state.value = evaluate()
        }
    }

    fun refresh() {
        initialize()
    }

    private suspend fun evaluate(): EntitlementState {
        if (BuildConfig.DEBUG) return EntitlementState.DebugBypass
        if (!integrityGate.verify()) return EntitlementState.IntegrityFailed
        // TODO: Add Google Play paid-app licensing and optional server entitlement.
        return EntitlementState.Unlicensed
    }
}
