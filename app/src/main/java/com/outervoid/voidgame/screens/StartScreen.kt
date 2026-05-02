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

class StartScreen(private val game: VoidGame) : Screen {
    private val batch = SpriteBatch()
    private val font = BitmapFont()
    private val layout = GlyphLayout()
    private var elapsed = 0f
    private val startBounds = Rectangle()
    private val scoreBounds = Rectangle()
    private val exitBounds = Rectangle()

    override fun render(delta: Float) {
        elapsed += delta
        handleInput()
        Gdx.gl.glClearColor(GameConfig.COLOR_BACKGROUND.r, GameConfig.COLOR_BACKGROUND.g, GameConfig.COLOR_BACKGROUND.b, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        val width = Gdx.graphics.width.toFloat()
        val height = Gdx.graphics.height.toFloat()
        val centerX = width * 0.5f
        batch.begin()
        font.color = GameConfig.COLOR_AMBER
        font.data.setScale(3.0f)
        drawCentered("AXIOM SHOOTER", centerX, height * 0.68f)
        font.data.setScale(1.55f)
        if ((elapsed % 1f) < 0.55f) drawCentered("START AXIOM", centerX, height * 0.47f)
        font.color = GameConfig.COLOR_GRAY
        drawCentered("HIGH SCORE", centerX, height * 0.37f)
        drawCentered("EXIT", centerX, height * 0.27f)
        batch.end()
        startBounds.set(centerX - 190f, height * 0.47f - 42f, 380f, 62f)
        scoreBounds.set(centerX - 190f, height * 0.37f - 42f, 380f, 62f)
        exitBounds.set(centerX - 190f, height * 0.27f - 42f, 380f, 62f)
    }

    private fun handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) game.exit()
        if (!Gdx.input.justTouched()) return
        val x = Gdx.input.x.toFloat()
        val y = Gdx.graphics.height - Gdx.input.y.toFloat()
        when {
            startBounds.contains(x, y) -> game.setScreen(GameScreen(game))
            scoreBounds.contains(x, y) -> game.setScreen(ScoreScreen(game))
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
