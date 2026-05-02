package com.outervoid.voidgame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Rectangle
import com.outervoid.voidgame.GameConfig
import com.outervoid.voidgame.VoidGame
import com.outervoid.voidgame.security.LaunchStatus

class LockedModuleScreen(
    private val game: VoidGame,
    private val launchStatus: LaunchStatus
) : Screen {
    private val batch = SpriteBatch()
    private val font = BitmapFont()
    private val layout = GlyphLayout()
    private val openBounds = Rectangle()
    private val exitBounds = Rectangle()

    override fun render(delta: Float) {
        handleInput()
        Gdx.gl.glClearColor(GameConfig.COLOR_BACKGROUND.r, GameConfig.COLOR_BACKGROUND.g, GameConfig.COLOR_BACKGROUND.b, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        val cx = Gdx.graphics.width * 0.5f
        val h = Gdx.graphics.height.toFloat()
        batch.begin()
        font.data.setScale(1.75f)
        font.color = GameConfig.COLOR_AMBER
        drawCentered(launchStatus.title, cx, h * 0.62f)
        font.data.setScale(1.0f)
        font.color = GameConfig.COLOR_GRAY
        drawWrappedMessage(launchStatus.message, cx, h)
        font.data.setScale(1.1f)
        font.color = GameConfig.COLOR_AMBER
        drawCentered("OPEN AXIOM", cx, h * 0.31f)
        font.color = GameConfig.COLOR_DIM
        drawCentered("EXIT", cx, h * 0.23f)
        batch.end()
        openBounds.set(cx - 170f, h * 0.31f - 38f, 340f, 56f)
        exitBounds.set(cx - 170f, h * 0.23f - 38f, 340f, 56f)
    }

    private fun handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) game.exit()
        if (!Gdx.input.justTouched()) return
        val x = Gdx.input.x.toFloat()
        val y = Gdx.graphics.height - Gdx.input.y.toFloat()
        when {
            openBounds.contains(x, y) -> openAxiomOrStayLocked()
            exitBounds.contains(x, y) -> game.exit()
        }
    }

    private fun openAxiomOrStayLocked() {
        game.openAxiom()
    }

    private fun drawWrappedMessage(message: String, centerX: Float, height: Float) {
        val words = message.split(" ")
        val first = words.take((words.size + 1) / 2).joinToString(" ")
        val second = words.drop((words.size + 1) / 2).joinToString(" ")
        drawCentered(first, centerX, height * 0.50f)
        if (second.isNotBlank()) drawCentered(second, centerX, height * 0.45f)
    }

    private fun drawCentered(text: String, centerX: Float, y: Float) {
        layout.setText(font, text)
        font.draw(batch, layout, centerX - layout.width * 0.5f, y)
    }

    override fun show() = Unit
    override fun resize(width: Int, height: Int) = Unit
    override fun pause() = Unit
    override fun resume() = Unit
    override fun hide() = Unit
    override fun dispose() {
        batch.dispose()
        font.dispose()
    }
}
