package com.outervoid.axiom.launch

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.outervoid.axiom.security.EntitlementState
import com.outervoid.axiom.security.LicenseGate
import com.outervoid.axiom.security.SignatureGate
import java.security.MessageDigest
import java.security.SecureRandom

class SignedGameLaunchManager(private val context: Context) {
    private val signatureGate = SignatureGate(context)

    fun canLaunch(entitlementState: EntitlementState): Boolean =
        LicenseGate.unlocks(entitlementState) && signatureGate.unlocks(entitlementState)

    fun isGameInstalled(): Boolean = runCatching {
        context.packageManager.getPackageInfo(TargetPackage, 0)
        true
    }.getOrDefault(false)

    fun launch(entitlementState: EntitlementState): LaunchResult {
        if (!canLaunch(entitlementState)) return LaunchResult.Locked
        if (!isGameInstalled()) return LaunchResult.Missing
        val token = createToken(entitlementState)
        val intent = Intent().apply {
            setClassName(TargetPackage, TargetActivity)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra("axiom_source_package", SourcePackage)
            putExtra("axiom_target_package", token.targetPackage)
            putExtra("axiom_issued_at", token.issuedAtMillis)
            putExtra("axiom_expires_at", token.expiresAtMillis)
            putExtra("axiom_nonce", token.nonce)
            putExtra("axiom_entitlement_state", token.entitlementState.name)
            putExtra("axiom_signature", token.signature)
        }
        return runCatching {
            context.startActivity(intent)
            LaunchResult.Started
        }.getOrDefault(LaunchResult.Unavailable)
    }

    private fun createToken(entitlementState: EntitlementState): SignedLaunchToken {
        val issuedAt = System.currentTimeMillis()
        val nonce = ByteArray(16).also { SecureRandom().nextBytes(it) }.joinToString("") { "%02x".format(it) }
        val payload = "$SourcePackage|$TargetPackage|$issuedAt|${issuedAt + TokenTtlMillis}|$nonce|${entitlementState.name}"
        return SignedLaunchToken(
            sourcePackage = SourcePackage,
            targetPackage = TargetPackage,
            issuedAtMillis = issuedAt,
            expiresAtMillis = issuedAt + TokenTtlMillis,
            nonce = nonce,
            entitlementState = entitlementState,
            signature = sha256(payload)
        )
    }

    private fun sha256(value: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(value.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    sealed interface LaunchResult {
        data object Started : LaunchResult
        data object Missing : LaunchResult
        data object Locked : LaunchResult
        data object Unavailable : LaunchResult
    }

    companion object {
        const val SourcePackage = "com.outervoid.axiom"
        const val TargetPackage = "com.outervoid.voidgame"
        const val TargetActivity = "com.outervoid.voidgame.VoidGameActivity"
        private const val TokenTtlMillis = 30_000L
    }
}
