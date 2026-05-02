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

@Deprecated("GameScreen owns the pause overlay inline. This class is unused and kept only as a reference.")
class PauseScreen(private val game: VoidGame) : Screen {
    private val batch = SpriteBatch()
    private val font = BitmapFont()
    private val layout = GlyphLayout()
    private val resumeBounds = Rectangle()
    private val restartBounds = Rectangle()
    private val exitBounds = Rectangle()

    override fun render(delta: Float) {
        handleInput()
        Gdx.gl.glClearColor(GameConfig.COLOR_BACKGROUND.r, GameConfig.COLOR_BACKGROUND.g, GameConfig.COLOR_BACKGROUND.b, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        val cx = Gdx.graphics.width * 0.5f
        val h = Gdx.graphics.height.toFloat()
        batch.begin()
        font.data.setScale(2.0f)
        font.color = GameConfig.COLOR_AMBER
        drawCentered("PAUSED", cx, h * 0.64f)
        font.data.setScale(1.2f)
        drawCentered("RESUME", cx, h * 0.48f)
        font.color = GameConfig.COLOR_GRAY
        drawCentered("RESTART", cx, h * 0.39f)
        drawCentered("EXIT TO AXIOM", cx, h * 0.30f)
        batch.end()
        resumeBounds.set(cx - 180f, h * 0.48f - 40f, 360f, 56f)
        restartBounds.set(cx - 180f, h * 0.39f - 40f, 360f, 56f)
        exitBounds.set(cx - 180f, h * 0.30f - 40f, 360f, 56f)
    }

    private fun handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) game.setScreen(GameScreen(game))
        if (!Gdx.input.justTouched()) return
        val x = Gdx.input.x.toFloat()
        val y = Gdx.graphics.height - Gdx.input.y.toFloat()
        when {
            resumeBounds.contains(x, y) -> game.setScreen(GameScreen(game))
            restartBounds.contains(x, y) -> game.setScreen(GameScreen(game))
            exitBounds.contains(x, y) -> game.exit()
        }
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
