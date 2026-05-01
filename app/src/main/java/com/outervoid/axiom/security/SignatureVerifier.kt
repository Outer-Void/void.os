package com.outervoid.axiom.security

import android.content.Context
import android.os.Build
import org.json.JSONObject

class SignatureVerifier(private val context: Context) {
    private val packageSignatureVerifier = PackageSignatureVerifier(context)

    fun parse(raw: String): ProductSignature? = runCatching {
        val json = JSONObject(raw)
        ProductSignature(
            productId = json.getString("productId"),
            packageName = json.getString("packageName"),
            signingCertSha256 = json.getString("signingCertSha256"),
            versionCode = json.getLong("versionCode"),
            entitlementState = json.getString("entitlementState"),
            issuedAtMillis = json.getLong("issuedAtMillis"),
            expiresAtMillis = json.getLong("expiresAtMillis"),
            installNonce = json.getString("installNonce"),
            signature = json.getString("signature")
        )
    }.getOrNull()

    fun verify(productSignature: ProductSignature, entitlementState: EntitlementState): Boolean {
        val now = System.currentTimeMillis()
        if (productSignature.productId != ProductSignature.ProductId) return false
        if (productSignature.packageName != context.packageName) return false
        if (productSignature.expiresAtMillis <= now) return false
        if (productSignature.issuedAtMillis > now) return false
        if (productSignature.installNonce.isBlank()) return false
        if (productSignature.signature.isBlank()) return false
        if (!LicenseGate.unlocks(entitlementState)) return false
        if (productSignature.entitlementState != entitlementState.name) return false
        if (productSignature.versionCode != versionCode()) return false
        val cert = packageSignatureVerifier.signingCertSha256(context.packageName) ?: return false
        if (!cert.equals(productSignature.signingCertSha256, ignoreCase = true)) return false
        // TODO: Verify productSignature.signature with server-issued public verification material.
        return false
    }

    private fun versionCode(): Long {
        val info = context.packageManager.getPackageInfo(context.packageName, 0)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) info.longVersionCode else @Suppress("DEPRECATION") info.versionCode.toLong()
    }
}
