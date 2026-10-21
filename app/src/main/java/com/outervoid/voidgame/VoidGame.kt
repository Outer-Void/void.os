package com.outervoid.voidgame

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.outervoid.voidgame.screens.LockedModuleScreen
import com.outervoid.voidgame.screens.StartScreen
import com.outervoid.voidgame.screens.GameScreen
import com.outervoid.voidgame.security.LaunchStatus

class VoidGame(
    private val launchStatus: LaunchStatus = LaunchStatus(unlocked = true),
    private val onOpenAxiom: () -> Unit = {}
) : ApplicationAdapter() {
    private var currentScreen: Screen? = null
    private var pendingScreen: Screen? = null

    override fun create() {
        Gdx.input.setCatchKey(com.badlogic.gdx.Input.Keys.BACK, true)
        setScreen(if (launchStatus.unlocked) StartScreen(this) else LockedModuleScreen(this, launchStatus))
        applyPendingScreen()
    }

    override fun render() {
        currentScreen?.render(Gdx.graphics.deltaTime)
        applyPendingScreen()
    }

    override fun resize(width: Int, height: Int) {
        currentScreen?.resize(width, height)
    }

    override fun dispose() {
        pendingScreen?.dispose()
        pendingScreen = null
        currentScreen?.dispose()
        currentScreen = null
    }

    override fun pause() { currentScreen?.pause() }
    override fun resume() { currentScreen?.resume() }

    fun setScreen(nextScreen: Screen) {
        // Commit after render returns: input handlers may still draw the outgoing frame.
        if (pendingScreen != null) {
            nextScreen.dispose()
            return
        }
        pendingScreen = nextScreen
    }

    private fun applyPendingScreen() {
        val nextScreen = pendingScreen ?: return
        pendingScreen = null
        currentScreen?.hide()
        currentScreen?.dispose()
        currentScreen = nextScreen
        nextScreen.show()
        nextScreen.resize(Gdx.graphics.width, Gdx.graphics.height)
    }

    fun handleSystemBack() {
        when (val screen = currentScreen) {
            is GameScreen -> screen.pause()
            is StartScreen -> openAxiom()
            else -> setScreen(StartScreen(this))
        }
    }

    fun openAxiom() {
        onOpenAxiom()
    }

    fun exit() {
        Gdx.app.exit()
    }
}
