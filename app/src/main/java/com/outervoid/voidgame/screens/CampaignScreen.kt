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
import com.outervoid.voidgame.ShooterVisuals
import com.outervoid.voidgame.VoidGame

class CampaignScreen(private val game: VoidGame) : Screen {
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera)
    private val shapeRenderer = ShapeRenderer()
    private val batch = SpriteBatch()
    private val font = BitmapFont()
    private val layout = GlyphLayout()
    private val touch = Vector3()
    private lateinit var prefs: Preferences
    private var missionsComplete = 0

    data class Mission(
        val id: Int,
        val label: String,
        val description: String,
        val waves: Int,
        val bounds: Rectangle
    )

    private val missions = listOf(
        Mission(1, "M-01  FIRST CONTACT",    "3 waves - first contact", 3,  Rectangle(36f, 430f, 288f, 52f)),
        Mission(2, "M-02  SIGNAL BREACH",    "5 waves — faster hostiles",  5,  Rectangle(36f, 358f, 288f, 52f)),
        Mission(3, "M-03  CORE COLLAPSE",    "7 waves — elite threat",     7,  Rectangle(36f, 286f, 288f, 52f))
    )

    private val backBounds = Rectangle(36f, 40f, 136f, 42f)

    override fun show() {
        camera.setToOrtho(false, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT)
        prefs = Gdx.app.getPreferences("axiom_shooter")
        missionsComplete = prefs.getInteger("missions_complete", 0)
    }

    override fun render(delta: Float) {
        handleInput()
        Gdx.gl.glClearColor(GameConfig.COLOR_BACKGROUND.r, GameConfig.COLOR_BACKGROUND.g, GameConfig.COLOR_BACKGROUND.b, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        shapeRenderer.projectionMatrix = camera.combined
        batch.projectionMatrix = camera.combined
        drawFrame()
        drawText()
    }

    private fun handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) game.setScreen(StartScreen(game))
        if (!Gdx.input.justTouched()) return
        touch.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        viewport.unproject(touch)
        if (backBounds.contains(touch.x, touch.y)) { game.setScreen(StartScreen(game)); return }
        missions.forEachIndexed { index, mission ->
            if (mission.bounds.contains(touch.x, touch.y) && index <= missionsComplete) {
                game.setScreen(GameScreen(game, campaignMission = mission.id, campaignWaves = mission.waves))
            }
        }
    }

    private fun drawFrame() {
        val w = GameConfig.WORLD_WIDTH
        val h = GameConfig.WORLD_HEIGHT
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = GameConfig.COLOR_SURFACE
        shapeRenderer.rect(10f, 10f, w - 20f, h - 20f)
        shapeRenderer.color = GameConfig.COLOR_AMBER
        shapeRenderer.line(28f, h - 72f, w - 28f, h - 72f)
        missions.forEachIndexed { index, mission ->
            val unlocked = index <= missionsComplete
            if (unlocked) shapeRenderer.color = GameConfig.COLOR_SURFACE
            else ShooterVisuals.setAlphaColor(shapeRenderer, GameConfig.COLOR_SURFACE, 0.4f)
            shapeRenderer.rect(mission.bounds.x, mission.bounds.y, mission.bounds.width, mission.bounds.height)
            if (index < missions.lastIndex) {
                val mid = w * 0.5f
                val y0 = mission.bounds.y
                shapeRenderer.color = GameConfig.COLOR_DIM
                shapeRenderer.line(mid, y0, mid, y0 - 20f)
            }
        }
        shapeRenderer.color = GameConfig.COLOR_SURFACE
        shapeRenderer.rect(backBounds.x, backBounds.y, backBounds.width, backBounds.height)
        shapeRenderer.end()
    }

    private fun drawText() {
        val w = GameConfig.WORLD_WIDTH
        val h = GameConfig.WORLD_HEIGHT
        batch.begin()
        font.data.setScale(1.8f); font.color = GameConfig.COLOR_AMBER
        drawCentered("CAMPAIGN", w * 0.5f, h - 30f)
        font.data.setScale(0.72f); font.color = GameConfig.COLOR_DIM
        drawCentered("AXIOM-01 DEPLOYED", w * 0.5f, h - 55f)
        missions.forEachIndexed { index, mission ->
            val unlocked = index <= missionsComplete
            val complete = index < missionsComplete
            font.data.setScale(0.95f)
            font.color = when {
                complete -> GameConfig.COLOR_AMBER
                unlocked -> GameConfig.COLOR_GRAY
                else -> GameConfig.COLOR_DIM
            }
            drawCentered(mission.label, w * 0.5f, mission.bounds.y + mission.bounds.height - 14f)
            font.data.setScale(0.72f)
            font.color = if (unlocked) GameConfig.COLOR_DIM else GameConfig.COLOR_SURFACE
            drawCentered(mission.description, w * 0.5f, mission.bounds.y + 14f)
            if (complete) {
                font.data.setScale(0.62f); font.color = GameConfig.COLOR_AMBER
                drawRight("COMPLETE", mission.bounds.x + mission.bounds.width - 8f, mission.bounds.y + mission.bounds.height - 12f)
            } else if (!unlocked) {
                font.data.setScale(0.62f); font.color = GameConfig.COLOR_DIM
                drawRight("LOCKED", mission.bounds.x + mission.bounds.width - 8f, mission.bounds.y + mission.bounds.height - 12f)
            }
        }
        font.data.setScale(0.9f); font.color = GameConfig.COLOR_GRAY
        drawCentered("BACK", backBounds.x + backBounds.width * 0.5f, backBounds.y + backBounds.height - 12f)
        batch.end()
    }

    private fun drawCentered(text: String, cx: Float, y: Float) {
        layout.setText(font, text); font.draw(batch, layout, cx - layout.width * 0.5f, y)
    }

    private fun drawRight(text: String, rightX: Float, y: Float) {
        layout.setText(font, text); font.draw(batch, layout, rightX - layout.width, y)
    }

    override fun resize(width: Int, height: Int) = viewport.update(width, height, true)
    override fun pause() = Unit
    override fun resume() = Unit
    override fun hide() = Unit
    override fun dispose() { shapeRenderer.dispose(); batch.dispose(); font.dispose() }
}
