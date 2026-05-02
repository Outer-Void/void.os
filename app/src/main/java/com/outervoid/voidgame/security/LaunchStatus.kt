package com.outervoid.voidgame.security

data class LaunchStatus(
    val unlocked: Boolean,
    val reason: String = ""
) {
    val title: String
        get() = when (reason) {
            "Expired token" -> "SESSION EXPIRED"
            "Invalid source", "Invalid target" -> "INVALID AXIOM SOURCE"
            "Cryptographic verification pending", "Module signature locked" -> "VERIFICATION REQUIRED"
            else -> "AXIOM MODULE LOCKED"
        }

    val message: String
        get() = when (reason) {
            "Expired token" -> "Return to AXIOM Launcher and launch Axiom Shooter again."
            "Invalid source", "Invalid target" -> "This module only accepts launches from AXIOM Launcher."
            "Cryptographic verification pending", "Module signature locked" -> "Release gameplay requires signed AXIOM verification."
            else -> "Axiom Shooter is available through AXIOM Launcher."
        }
}
