package com.outervoid.voidgame

import android.content.Intent
import android.os.Bundle
import com.outervoid.axiom.BuildConfig
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.outervoid.axiom.MainActivity
import com.outervoid.voidgame.security.LaunchStatus

class VoidGameActivity : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = AndroidApplicationConfiguration().apply {
            useImmersiveMode = true
            useAccelerometer = false
            useCompass = false
        }
        initialize(VoidGame(launchStatus(), onOpenAxiom = { openAxiomLauncher() }), config)
    }

    private fun launchStatus(): LaunchStatus {
        if (BuildConfig.DEBUG) return LaunchStatus(unlocked = true)
        val source = intent?.getStringExtra("axiom_source_package")
        val target = intent?.getStringExtra("axiom_target_package")
        return if (source == packageName && target == "com.outervoid.voidgame") {
            LaunchStatus(unlocked = true)
        } else {
            LaunchStatus(unlocked = false, reason = "Invalid source")
        }
    }

    private fun openAxiomLauncher() {
        val intent = packageManager.getLaunchIntentForPackage(packageName) ?: Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        runCatching { startActivity(intent) }
    }
}
