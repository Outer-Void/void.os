package com.outervoid.axiom.security

object LicenseGate {
    fun unlocks(state: EntitlementState): Boolean = when (state) {
        EntitlementState.Licensed,
        EntitlementState.OfflineGrace,
        EntitlementState.DebugBypass -> true
        EntitlementState.Unknown,
        EntitlementState.Unlicensed,
        EntitlementState.IntegrityFailed -> false
    }
}
