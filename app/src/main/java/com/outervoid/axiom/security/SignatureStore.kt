package com.outervoid.axiom.security

import android.content.Context
import java.io.File

class SignatureStore(context: Context) {
    private val signatureFile = File(File(context.filesDir, "signatures"), "axiom.sig")

    fun read(): String? = runCatching {
        if (!signatureFile.exists()) return null
        signatureFile.readText(Charsets.UTF_8)
    }.getOrNull()
}
