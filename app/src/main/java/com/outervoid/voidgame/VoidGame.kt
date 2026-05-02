package com.outervoid.voidgame

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.outervoid.voidgame.screens.LockedModuleScreen
import com.outervoid.voidgame.screens.StartScreen
import com.outervoid.voidgame.security.LaunchStatus

class VoidGame(
    private val launchStatus: LaunchStatus = LaunchStatus(unlocked = true),
    private val onOpenAxiom: () -> Unit = {}
) : ApplicationAdapter() {
    private var currentScreen: Screen? = null

    override fun create() {
        Gdx.input.setCatchKey(com.badlogic.gdx.Input.Keys.BACK, true)
        setScreen(if (launchStatus.unlocked) StartScreen(this) else LockedModuleScreen(this, launchStatus))
    }

    override fun render() {
        currentScreen?.render(Gdx.graphics.deltaTime)
    }

    override fun resize(width: Int, height: Int) {
        currentScreen?.resize(width, height)
    }

    override fun dispose() {
        currentScreen?.dispose()
    }

    fun setScreen(nextScreen: Screen) {
        currentScreen?.hide()
        currentScreen?.dispose()
        currentScreen = nextScreen
        nextScreen.show()
        nextScreen.resize(Gdx.graphics.width, Gdx.graphics.height)
    }

    fun openAxiom() {
        onOpenAxiom()
    }

    fun exit() {
        Gdx.app.exit()
    }
}
