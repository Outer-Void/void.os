package com.outervoid.axiom.security

enum class EntitlementState {
    Unknown,
    Licensed,
    Unlicensed,
    IntegrityFailed,
    OfflineGrace,
    DebugBypass
}
