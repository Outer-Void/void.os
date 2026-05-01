package com.outervoid.axiom.launch

import com.outervoid.axiom.security.EntitlementState

data class SignedLaunchToken(
    val sourcePackage: String,
    val targetPackage: String,
    val issuedAtMillis: Long,
    val expiresAtMillis: Long,
    val nonce: String,
    val entitlementState: EntitlementState,
    val signature: String
)
