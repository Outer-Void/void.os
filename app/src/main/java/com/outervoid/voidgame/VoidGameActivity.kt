package com.outervoid.voidgame

import android.content.Intent
import android.os.Bundle
import android.os.Build
import android.util.Log
import android.view.WindowManager
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import com.outervoid.axiom.BuildConfig
import com.outervoid.axiom.security.EntitlementState
import com.outervoid.axiom.security.SignatureGate
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import com.outervoid.axiom.MainActivity
import com.outervoid.voidgame.security.LaunchStatus

class VoidGameActivity : AndroidApplication() {
    private lateinit var activeGame: VoidGame
    private var backInvokedCallback: OnBackInvokedCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(Tag, "VoidGameActivity created")
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val config = AndroidApplicationConfiguration().apply {
            useImmersiveMode = true
            useAccelerometer = false
            useCompass = false
        }
        activeGame = VoidGame(launchStatus(), onOpenAxiom = { openAxiomLauncher() })
        initialize(activeGame, config)
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val callback = OnBackInvokedCallback {
                com.badlogic.gdx.Gdx.app.postRunnable { activeGame.handleSystemBack() }
            }
            backInvokedCallback = callback
            onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                callback
            )
        }
    }

    override fun onStop() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            backInvokedCallback?.let(onBackInvokedDispatcher::unregisterOnBackInvokedCallback)
            backInvokedCallback = null
        }
        super.onStop()
    }

    private fun launchStatus(): LaunchStatus {
        if (BuildConfig.DEBUG) {
            Log.i(Tag, "Debug build: bundled Shooter unlocked")
            return LaunchStatus(unlocked = true)
        }
        val source = intent?.getStringExtra("axiom_source_package")
        val target = intent?.getStringExtra("axiom_target_package")
        val now = System.currentTimeMillis()
        val expiresAt = intent?.getLongExtra("axiom_expires_at", 0L) ?: 0L
        val entitlement = intent?.getStringExtra("axiom_entitlement_state")
            ?.let { runCatching { EntitlementState.valueOf(it) }.getOrNull() }
        val authorized = entitlement in setOf(EntitlementState.Licensed, EntitlementState.OfflineGrace) &&
            entitlement != null && SignatureGate(this).unlocks(entitlement)
        return if (authorized && source == BuildConfig.APPLICATION_ID && target == "com.outervoid.voidgame" && expiresAt > now) {
            LaunchStatus(unlocked = true)
        } else {
            Log.w(Tag, "Release launch rejected: source=$source target=$target expiresAt=$expiresAt")
            LaunchStatus(unlocked = false, reason = "Invalid source")
        }
    }

    private fun openAxiomLauncher() {
        runOnUiThread {
            val intent = Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            runCatching { startActivity(intent) }
            finish()
        }
    }

    companion object {
        private const val Tag = "VoidGameActivity"
    }
}
