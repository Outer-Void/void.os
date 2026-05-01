package com.outervoid.axiom.security

data class ProductSignature(
    val productId: String,
    val packageName: String,
    val signingCertSha256: String,
    val versionCode: Long,
    val entitlementState: String,
    val issuedAtMillis: Long,
    val expiresAtMillis: Long,
    val installNonce: String,
    val signature: String
) {
    companion object {
        const val ProductId = "AXIOM_LAUNCHER"
    }
}
