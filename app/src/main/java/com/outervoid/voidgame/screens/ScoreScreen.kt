package com.outervoid.voidgame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.FitViewport
import com.outervoid.voidgame.GameConfig
import com.outervoid.voidgame.ShooterRules
import com.outervoid.voidgame.VoidGame

class ScoreScreen(
    private val game: VoidGame,
    private val finalSignal: Long = 0L,
    private val cyclesReached: Int = 0,
    private val survival: Boolean = true,
    private val campaignMission: Int = 0,
    private val campaignWaves: Int = 0,
    private val missionComplete: Boolean = false
) : Screen {
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera)
    private val batch = SpriteBatch()
    private val font = BitmapFont()
    private val renderer = ShapeRenderer()
    private val layout = GlyphLayout()
    private val touch = Vector3()
    private val transmitBounds = Rectangle(72f, 86f, 216f, 42f)
    private val exitBounds = Rectangle(72f, 34f, 216f, 42f)
    private lateinit var preferences: Preferences
    private val signalLogKey = if (survival) "signal_log_survival" else "signal_log_mission_$campaignMission"
    private var scores = emptyList<Long>()
    private var newRank = -1
    private var elapsed = 0f

    override fun show() {
        camera.setToOrtho(false, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT)
        preferences = Gdx.app.getPreferences("axiom_shooter")
        val existing = loadScores().toMutableList()
        if (finalSignal > 0L) {
            val sorted = ShooterRules.topScores(existing, finalSignal)
            newRank = sorted.indexOfFirst { it == finalSignal }
            scores = sorted
            saveScores(sorted)
        } else {
            scores = existing.sortedDescending().take(5)
        }
    }

    override fun render(delta: Float) {
        elapsed += delta
        handleInput()
        Gdx.gl.glClearColor(GameConfig.COLOR_BACKGROUND.r, GameConfig.COLOR_BACKGROUND.g, GameConfig.COLOR_BACKGROUND.b, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        renderer.projectionMatrix = camera.combined
        batch.projectionMatrix = camera.combined
        drawFrame()
        drawText()
    }

    private fun handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            game.setScreen(StartScreen(game))
            return
        }
        if (!Gdx.input.justTouched()) return
        touch.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        viewport.unproject(touch)
        when {
            transmitBounds.contains(touch.x, touch.y) -> game.setScreen(GameScreen(game, survival, campaignMission, campaignWaves))
            exitBounds.contains(touch.x, touch.y) -> game.exit()
        }
    }

    private fun drawFrame() {
        renderer.begin(ShapeRenderer.ShapeType.Line)
        renderer.color = GameConfig.COLOR_SURFACE
        renderer.rect(14f, 14f, GameConfig.WORLD_WIDTH - 28f, GameConfig.WORLD_HEIGHT - 28f)
        renderer.color = GameConfig.COLOR_AMBER
        renderer.line(44f, 356f, GameConfig.WORLD_WIDTH - 44f, 356f)
        renderer.end()
    }

    private fun drawText() {
        batch.begin()
        font.data.setScale(if (missionComplete) 1.6f else 2.0f)
        font.color = GameConfig.COLOR_AMBER
        drawCentered(if (missionComplete) "MISSION CLEAR" else "RUN ENDED", 500f)
        font.data.setScale(1.0f)
        font.color = GameConfig.COLOR_GRAY
        drawCentered("SIGNAL: $finalSignal", 456f)
        drawCentered("CYCLES: $cyclesReached", 432f)
        drawCentered("HIGH SCORE: ${scores.firstOrNull() ?: 0L}", 408f)

        if (newRank >= 0 && (elapsed % 0.8f) < 0.42f) {
            font.color = GameConfig.COLOR_AMBER
            drawCentered("NEW HIGH SIGNAL", 376f)
        }

        font.color = GameConfig.COLOR_AMBER
        font.data.setScale(1.15f)
        drawCentered("SIGNAL LOG", 326f)
        font.data.setScale(0.95f)
        if (scores.isEmpty()) {
            font.color = GameConfig.COLOR_DIM
            drawCentered("NO TRANSMISSIONS", 284f)
        } else {
            scores.forEachIndexed { index, score ->
                font.color = if (index == newRank && (elapsed % 0.8f) < 0.42f) GameConfig.COLOR_AMBER else GameConfig.COLOR_GRAY
                drawCentered("${index + 1}. $score", 286f - index * 28f)
            }
        }

        font.color = GameConfig.COLOR_AMBER
        font.data.setScale(1.05f)
        drawCentered("START AGAIN", 116f)
        font.color = GameConfig.COLOR_GRAY
        drawCentered("EXIT TO AXIOM", 64f)
        batch.end()
    }

    private fun loadScores(): List<Long> = preferences.getString(signalLogKey, if (survival) preferences.getString(SIGNAL_LOG_KEY, "") else "")
        .split(',')
        .mapNotNull { it.trim().takeIf(String::isNotEmpty)?.toLongOrNull() }

    private fun saveScores(values: List<Long>) {
        preferences.putString(signalLogKey, values.joinToString(","))
        preferences.flush()
    }

    private fun drawCentered(text: String, y: Float) {
        layout.setText(font, text)
        font.draw(batch, layout, (GameConfig.WORLD_WIDTH - layout.width) * 0.5f, y)
    }

    override fun resize(width: Int, height: Int) {
        viewport.update(width, height, true)
    }

    override fun pause() = Unit
    override fun resume() = Unit
    override fun hide() = Unit

    override fun dispose() {
        batch.dispose()
        font.dispose()
        renderer.dispose()
    }

    companion object {
        private const val SIGNAL_LOG_KEY = "signal_log"
    }
}
