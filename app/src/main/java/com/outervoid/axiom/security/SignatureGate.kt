package com.outervoid.axiom.security

import android.content.Context
import com.outervoid.axiom.BuildConfig

class SignatureGate(context: Context) {
    private val store = SignatureStore(context)
    private val verifier = SignatureVerifier(context)

    fun unlocks(entitlementState: EntitlementState): Boolean {
        if (BuildConfig.DEBUG) return true
        val raw = store.read() ?: return false
        val productSignature = verifier.parse(raw) ?: return false
        return verifier.verify(productSignature, entitlementState)
    }
}
