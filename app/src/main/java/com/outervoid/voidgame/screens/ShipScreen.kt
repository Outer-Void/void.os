package com.outervoid.voidgame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.viewport.FitViewport
import com.outervoid.voidgame.GameConfig
import com.outervoid.voidgame.ShooterVisuals
import com.outervoid.voidgame.VoidGame
import com.outervoid.voidgame.entities.Carrier

class ShipScreen(private val game: VoidGame) : Screen {
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera)
    private val shapeRenderer = ShapeRenderer()
    private val batch = SpriteBatch()
    private val font = BitmapFont()
    private val layout = GlyphLayout()
    private val carrier = Carrier()
    private var elapsed = 0f
    private val backBounds = Rectangle(36f, 40f, 136f, 42f)
    private val touch = Vector3()

    private val stats = listOf(
        "HULL" to 0.88f,
        "SHIELD" to 0.62f,
        "SPEED" to 0.76f,
        "FIREPOWER" to 0.80f,
        "MANEUVER" to 0.72f
    )

    override fun show() {
        camera.setToOrtho(false, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT)
    }

    override fun render(delta: Float) {
        elapsed += delta
        carrier.thrusterTimer += delta
        handleInput()
        Gdx.gl.glClearColor(GameConfig.COLOR_BACKGROUND.r, GameConfig.COLOR_BACKGROUND.g, GameConfig.COLOR_BACKGROUND.b, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        shapeRenderer.projectionMatrix = camera.combined
        batch.projectionMatrix = camera.combined
        Gdx.gl.glEnable(GL20.GL_BLEND)
        draw()
        Gdx.gl.glDisable(GL20.GL_BLEND)
    }

    private fun handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) { game.setScreen(ShooterSettingsScreen(game)); return }
        if (!Gdx.input.justTouched()) return
        touch.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        viewport.unproject(touch)
        if (backBounds.contains(touch.x, touch.y)) game.setScreen(ShooterSettingsScreen(game))
    }

    private fun draw() {
        val w = GameConfig.WORLD_WIDTH
        val h = GameConfig.WORLD_HEIGHT

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = GameConfig.COLOR_SURFACE
        shapeRenderer.rect(10f, 10f, w - 20f, h - 20f)
        shapeRenderer.color = GameConfig.COLOR_AMBER
        shapeRenderer.line(28f, h - 72f, w - 28f, h - 72f)
        shapeRenderer.line(28f, 380f, w - 28f, 380f)

        // Stats bars background
        stats.forEachIndexed { index, (_, value) ->
            val y = 350f - index * 44f
            shapeRenderer.color = GameConfig.COLOR_SURFACE
            shapeRenderer.rect(36f, y, 288f, 20f)
        }
        shapeRenderer.color = GameConfig.COLOR_SURFACE
        shapeRenderer.rect(backBounds.x, backBounds.y, backBounds.width, backBounds.height)
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        stats.forEachIndexed { index, (_, statValue) ->
            val y = 350f - index * 44f
            val pulse = if (index == 0) (MathUtils.sin(elapsed * 1.8f) + 1f) * 0.5f else 0f
            ShooterVisuals.setAlphaColor(shapeRenderer, GameConfig.COLOR_AMBER, 0.7f + pulse * 0.3f)
            shapeRenderer.rect(36f, y, 288f * statValue, 20f)
        }
        shapeRenderer.end()

        // Ship preview in center
        carrier.x = w * 0.5f - carrier.width * 0.5f
        carrier.y = h * 0.72f - carrier.height
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        val scale = 1.8f
        val cx = carrier.x + carrier.width * 0.5f
        val cy = carrier.y
        shapeRenderer.color = GameConfig.COLOR_DIM
        shapeRenderer.line(cx - 22f * scale, cy + 6f * scale, cx - 8f * scale, cy + 18f * scale)
        shapeRenderer.line(cx + 22f * scale, cy + 6f * scale, cx + 8f * scale, cy + 18f * scale)
        shapeRenderer.line(cx - 22f * scale, cy + 6f * scale, cx - 18f * scale, cy)
        shapeRenderer.line(cx + 22f * scale, cy + 6f * scale, cx + 18f * scale, cy)
        shapeRenderer.line(cx - 18f * scale, cy, cx - 10f * scale, cy + 2f * scale)
        shapeRenderer.line(cx + 18f * scale, cy, cx + 10f * scale, cy + 2f * scale)
        shapeRenderer.color = GameConfig.COLOR_GRAY
        shapeRenderer.line(cx, cy + carrier.height * scale, cx - 8f * scale, cy + 18f * scale)
        shapeRenderer.line(cx, cy + carrier.height * scale, cx + 8f * scale, cy + 18f * scale)
        shapeRenderer.line(cx - 8f * scale, cy + 18f * scale, cx - 10f * scale, cy + 2f * scale)
        shapeRenderer.line(cx + 8f * scale, cy + 18f * scale, cx + 10f * scale, cy + 2f * scale)
        shapeRenderer.line(cx - 10f * scale, cy + 2f * scale, cx - 5f * scale, cy)
        shapeRenderer.line(cx + 10f * scale, cy + 2f * scale, cx + 5f * scale, cy)
        shapeRenderer.line(cx - 5f * scale, cy, cx + 5f * scale, cy)
        val thrustAlpha = 0.5f + (MathUtils.sin(carrier.thrusterTimer * 18f) + 1f) * 0.25f
        ShooterVisuals.setAlphaColor(shapeRenderer, GameConfig.COLOR_AMBER, thrustAlpha)
        shapeRenderer.line(cx - 4f * scale, cy, cx - 2f * scale, cy - 7f * scale)
        shapeRenderer.line(cx + 4f * scale, cy, cx + 2f * scale, cy - 7f * scale)
        shapeRenderer.end()
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        shapeRenderer.color = GameConfig.COLOR_AMBER
        shapeRenderer.circle(cx, cy + 28f * scale, 3.5f * scale, 12)
        shapeRenderer.end()

        batch.begin()
        font.data.setScale(1.8f); font.color = GameConfig.COLOR_AMBER
        drawCentered("SHIP", w * 0.5f, h - 30f)
        font.data.setScale(0.85f); font.color = GameConfig.COLOR_GRAY
        drawCentered("AXIOM-01  /  PRIMARY CARRIER", w * 0.5f, h - 56f)
        stats.forEachIndexed { index, (label, statValue) ->
            val y = 370f - index * 44f
            font.data.setScale(0.72f); font.color = GameConfig.COLOR_DIM
            font.draw(batch, label, 42f, y)
            font.data.setScale(0.72f); font.color = GameConfig.COLOR_AMBER
            layout.setText(font, "${(statValue * 100).toInt()}%")
            font.draw(batch, layout, 36f + 288f - layout.width - 6f, y)
        }
        font.data.setScale(0.9f); font.color = GameConfig.COLOR_GRAY
        drawCentered("BACK", backBounds.x + backBounds.width * 0.5f, backBounds.y + backBounds.height - 12f)
        batch.end()
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
