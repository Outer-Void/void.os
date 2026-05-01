package com.outervoid.axiom.security

import android.content.Context
import com.outervoid.axiom.BuildConfig

class IntegrityGate(private val context: Context) {
    suspend fun verify(): Boolean {
        if (BuildConfig.DEBUG) return true
        // TODO: Connect Google Play Integrity API and optional server verification.
        // Release builds fail closed until real attestation is implemented.
        return false
    }
}
