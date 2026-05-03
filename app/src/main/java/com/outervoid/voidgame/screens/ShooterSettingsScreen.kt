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
import com.outervoid.voidgame.VoidGame

class ShooterSettingsScreen(private val game: VoidGame) : Screen {
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera)
    private val shapeRenderer = ShapeRenderer()
    private val batch = SpriteBatch()
    private val font = BitmapFont()
    private val layout = GlyphLayout()
    private val touch = Vector3()
    private lateinit var prefs: Preferences

    private var audioEnabled = true
    private var sensitivity = 1
    private var hudStyle = 0

    private val audioRow = Rectangle(36f, 440f, 288f, 48f)
    private val sensRow = Rectangle(36f, 368f, 288f, 48f)
    private val hudRow = Rectangle(36f, 296f, 288f, 48f)
    private val shipRow = Rectangle(36f, 200f, 288f, 48f)
    private val backBounds = Rectangle(36f, 40f, 136f, 42f)

    private val sensitivityLabels = listOf("LOW", "MEDIUM", "HIGH")
    private val hudLabels = listOf("FULL", "MINIMAL", "VOID")

    override fun show() {
        camera.setToOrtho(false, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT)
        prefs = Gdx.app.getPreferences("axiom_shooter_settings")
        audioEnabled = prefs.getBoolean("audio", true)
        sensitivity = prefs.getInteger("sensitivity", 1)
        hudStyle = prefs.getInteger("hud_style", 0)
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) { save(); game.setScreen(StartScreen(game)); return }
        if (!Gdx.input.justTouched()) return
        touch.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        viewport.unproject(touch)
        when {
            backBounds.contains(touch.x, touch.y) -> { save(); game.setScreen(StartScreen(game)) }
            audioRow.contains(touch.x, touch.y) -> audioEnabled = !audioEnabled
            sensRow.contains(touch.x, touch.y) -> sensitivity = (sensitivity + 1) % sensitivityLabels.size
            hudRow.contains(touch.x, touch.y) -> hudStyle = (hudStyle + 1) % hudLabels.size
            shipRow.contains(touch.x, touch.y) -> { save(); game.setScreen(ShipScreen(game)) }
        }
    }

    private fun save() {
        prefs.putBoolean("audio", audioEnabled)
        prefs.putInteger("sensitivity", sensitivity)
        prefs.putInteger("hud_style", hudStyle)
        prefs.flush()
    }

    private fun drawFrame() {
        val w = GameConfig.WORLD_WIDTH
        val h = GameConfig.WORLD_HEIGHT
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = GameConfig.COLOR_SURFACE
        shapeRenderer.rect(10f, 10f, w - 20f, h - 20f)
        shapeRenderer.color = GameConfig.COLOR_AMBER
        shapeRenderer.line(28f, h - 72f, w - 28f, h - 72f)
        for (row in listOf(audioRow, sensRow, hudRow, shipRow, backBounds)) {
            shapeRenderer.color = GameConfig.COLOR_SURFACE
            shapeRenderer.rect(row.x, row.y, row.width, row.height)
        }
        shapeRenderer.end()
    }

    private fun drawText() {
        val w = GameConfig.WORLD_WIDTH
        val h = GameConfig.WORLD_HEIGHT
        batch.begin()
        font.data.setScale(1.8f); font.color = GameConfig.COLOR_AMBER
        drawCentered("SETTINGS", w * 0.5f, h - 30f)
        font.data.setScale(0.72f); font.color = GameConfig.COLOR_DIM
        drawCentered("AXIOM SHOOTER CONFIGURATION", w * 0.5f, h - 55f)

        drawRow(audioRow, "AUDIO", if (audioEnabled) "ON" else "OFF", if (audioEnabled) GameConfig.COLOR_AMBER else GameConfig.COLOR_GRAY)
        drawRow(sensRow, "SENSITIVITY", sensitivityLabels[sensitivity], GameConfig.COLOR_GRAY)
        drawRow(hudRow, "HUD STYLE", hudLabels[hudStyle], GameConfig.COLOR_GRAY)
        drawRow(shipRow, "SHIP — AXIOM-01", "VIEW", GameConfig.COLOR_AMBER)

        font.data.setScale(0.9f); font.color = GameConfig.COLOR_GRAY
        drawCentered("BACK", backBounds.x + backBounds.width * 0.5f, backBounds.y + backBounds.height - 12f)
        batch.end()
    }

    private fun drawRow(bounds: Rectangle, label: String, value: String, valueColor: com.badlogic.gdx.graphics.Color) {
        val y = bounds.y + bounds.height - 14f
        font.data.setScale(0.9f); font.color = GameConfig.COLOR_GRAY
        font.draw(batch, label, bounds.x + 14f, y)
        font.data.setScale(0.9f); font.color = valueColor
        layout.setText(font, value); font.draw(batch, layout, bounds.x + bounds.width - layout.width - 14f, y)
    }

    private fun drawCentered(text: String, cx: Float, y: Float) {
        layout.setText(font, text); font.draw(batch, layout, cx - layout.width * 0.5f, y)
    }

    override fun resize(width: Int, height: Int) = viewport.update(width, height, true)
    override fun pause() = Unit
    override fun resume() = Unit
    override fun hide() = Unit
    override fun dispose() { shapeRenderer.dispose(); batch.dispose(); font.dispose() }
}
