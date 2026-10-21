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
import com.outervoid.voidgame.VoidGame
import com.outervoid.voidgame.ShooterRules
import com.outervoid.voidgame.entities.Bullet
import com.outervoid.voidgame.entities.Carrier

class TrainingScreen(private val game: VoidGame) : Screen {
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera)
    private val shapeRenderer = ShapeRenderer()
    private val batch = SpriteBatch()
    private val font = BitmapFont()
    private val layout = GlyphLayout()
    private val touch = Vector3()
    private val carrier = Carrier()
    private val bullets = Array(12) { Bullet() }
    private var elapsed = 0f
    private var shotsFired = 0
    private var touchX: Float? = null
    private val exitBounds = Rectangle(92f, 34f, 176f, 42f)

    override fun show() {
        camera.setToOrtho(false, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT)
        font.color = GameConfig.COLOR_GRAY
    }

    override fun render(delta: Float) {
        val step = ShooterRules.frameStep(delta)
        elapsed += step
        handleInput(step)
        update(step)
        Gdx.gl.glClearColor(GameConfig.COLOR_BACKGROUND.r, GameConfig.COLOR_BACKGROUND.g, GameConfig.COLOR_BACKGROUND.b, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        shapeRenderer.projectionMatrix = camera.combined
        batch.projectionMatrix = camera.combined
        Gdx.gl.glEnable(GL20.GL_BLEND)
        draw()
        Gdx.gl.glDisable(GL20.GL_BLEND)
    }

    private fun handleInput(@Suppress("UNUSED_PARAMETER") delta: Float) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) { game.setScreen(StartScreen(game)); return }
        if (Gdx.input.isTouched) {
            touch.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            viewport.unproject(touch)
            if (exitBounds.contains(touch.x, touch.y)) { game.setScreen(StartScreen(game)); return }
            touchX = touch.x
        } else {
            touchX = null
        }
        if (Gdx.input.justTouched()) {
            if (carrier.tryFire(bullets)) shotsFired++
        }
    }

    private fun update(delta: Float) {
        carrier.update(delta, touchX)
        bullets.forEach { if (it.active) { it.y += GameConfig.BULLET_SPEED * delta; if (it.y > GameConfig.WORLD_HEIGHT) it.active = false } }
    }

    private fun draw() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = GameConfig.COLOR_SURFACE
        shapeRenderer.rect(10f, 10f, GameConfig.WORLD_WIDTH - 20f, GameConfig.WORLD_HEIGHT - 20f)
        shapeRenderer.color = GameConfig.COLOR_AMBER
        shapeRenderer.line(28f, GameConfig.WORLD_HEIGHT - 72f, GameConfig.WORLD_WIDTH - 28f, GameConfig.WORLD_HEIGHT - 72f)
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        carrier.drawHull(shapeRenderer)
        bullets.forEach { b ->
            if (!b.active) return@forEach
            shapeRenderer.color = GameConfig.COLOR_AMBER
            shapeRenderer.rect(b.x - 1.5f, b.y, 3f, 12f)
        }
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        carrier.draw(shapeRenderer)
        shapeRenderer.color = GameConfig.COLOR_SURFACE
        shapeRenderer.rect(exitBounds.x, exitBounds.y, exitBounds.width, exitBounds.height)
        shapeRenderer.end()

        batch.begin()
        font.data.setScale(1.6f); font.color = GameConfig.COLOR_AMBER
        drawCentered("TRAINING", GameConfig.WORLD_WIDTH * 0.5f, GameConfig.WORLD_HEIGHT - 30f)
        font.data.setScale(0.72f); font.color = GameConfig.COLOR_DIM
        drawCentered("SAFE PRACTICE — NO ENEMIES", GameConfig.WORLD_WIDTH * 0.5f, GameConfig.WORLD_HEIGHT - 56f)
        font.data.setScale(0.9f); font.color = GameConfig.COLOR_GRAY
        drawCentered("SHOTS: $shotsFired", GameConfig.WORLD_WIDTH * 0.5f, GameConfig.WORLD_HEIGHT - 88f)
        font.data.setScale(0.62f); font.color = GameConfig.COLOR_DIM
        drawCentered("TOUCH LEFT/RIGHT TO MOVE  •  TAP TO FIRE", GameConfig.WORLD_WIDTH * 0.5f, 90f)
        font.data.setScale(0.9f); font.color = GameConfig.COLOR_GRAY
        drawCentered("EXIT", exitBounds.x + exitBounds.width * 0.5f, exitBounds.y + exitBounds.height - 12f)
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
