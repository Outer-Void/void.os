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

class StartScreen(private val game: VoidGame) : Screen {
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera)
    private val shapeRenderer = ShapeRenderer()
    private val batch = SpriteBatch()
    private val font = BitmapFont()
    private val layout = GlyphLayout()
    private val touch = Vector3()
    private var elapsed = 0f

    private data class MenuItem(val label: String, val y: Float, val bounds: Rectangle, val active: Boolean = false)

    private val menuItems = listOf(
        MenuItem("CAMPAIGN",     317f, Rectangle(62f, 295f, 236f, 38f), active = true),
        MenuItem("SURVIVAL",     267f, Rectangle(62f, 245f, 236f, 38f)),
        MenuItem("TRAINING",     217f, Rectangle(62f, 195f, 236f, 38f)),
        MenuItem("SETTINGS",     167f, Rectangle(62f, 145f, 236f, 38f)),
        MenuItem("EXIT TO AXIOM",117f, Rectangle(62f, 95f,  236f, 38f))
    )

    override fun show() {
        camera.setToOrtho(false, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT)
        font.color = GameConfig.COLOR_GRAY
    }

    override fun render(delta: Float) {
        elapsed += delta
        handleInput()
        Gdx.gl.glClearColor(GameConfig.COLOR_BACKGROUND.r, GameConfig.COLOR_BACKGROUND.g, GameConfig.COLOR_BACKGROUND.b, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        shapeRenderer.projectionMatrix = camera.combined
        batch.projectionMatrix = camera.combined
        Gdx.gl.glEnable(GL20.GL_BLEND)
        drawFrame()
        Gdx.gl.glDisable(GL20.GL_BLEND)
        drawText()
    }

    private fun handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) game.openAxiom()
        if (!Gdx.input.justTouched()) return
        touch.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
        viewport.unproject(touch)
        when {
            menuItems[0].bounds.contains(touch.x, touch.y) -> game.setScreen(CampaignScreen(game))
            menuItems[1].bounds.contains(touch.x, touch.y) -> game.setScreen(GameScreen(game, survival = true))
            menuItems[2].bounds.contains(touch.x, touch.y) -> game.setScreen(TrainingScreen(game))
            menuItems[3].bounds.contains(touch.x, touch.y) -> game.setScreen(ShooterSettingsScreen(game))
            menuItems[4].bounds.contains(touch.x, touch.y) -> game.openAxiom()
        }
    }

    private fun drawFrame() {
        val w = GameConfig.WORLD_WIDTH
        val h = GameConfig.WORLD_HEIGHT
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        shapeRenderer.color = GameConfig.COLOR_SURFACE
        shapeRenderer.rect(10f, 10f, w - 20f, h - 20f)
        shapeRenderer.color = GameConfig.COLOR_DIM
        shapeRenderer.line(10f, h - 10f, 30f, h - 10f); shapeRenderer.line(10f, h - 10f, 10f, h - 30f)
        shapeRenderer.line(w - 10f, h - 10f, w - 30f, h - 10f); shapeRenderer.line(w - 10f, h - 10f, w - 10f, h - 30f)
        shapeRenderer.line(10f, 10f, 30f, 10f); shapeRenderer.line(10f, 10f, 10f, 30f)
        shapeRenderer.line(w - 10f, 10f, w - 30f, 10f); shapeRenderer.line(w - 10f, 10f, w - 10f, 30f)
        shapeRenderer.color = GameConfig.COLOR_AMBER
        shapeRenderer.line(28f, h - 118f, w - 28f, h - 118f)
        shapeRenderer.color = GameConfig.COLOR_SURFACE
        shapeRenderer.line(28f, 80f, w - 28f, 80f)
        shapeRenderer.color = GameConfig.COLOR_AMBER.cpy().also { it.a = 0.22f }
        shapeRenderer.rect(62f, 295f, 236f, 36f)
        shapeRenderer.color = GameConfig.COLOR_SURFACE
        for (sep in listOf(283f, 233f, 183f, 133f)) {
            shapeRenderer.line(72f, sep, w - 72f, sep)
        }
        drawShipPreview(w * 0.5f, h - 74f)
        shapeRenderer.end()
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        val pulse = (MathUtils.sin(elapsed * 2.8f) + 1f) * 0.5f
        shapeRenderer.color = GameConfig.COLOR_AMBER.cpy().also { it.a = 0.04f + pulse * 0.04f }
        shapeRenderer.rect(62f, 295f, 236f, 36f)
        shapeRenderer.end()
    }

    private fun drawShipPreview(cx: Float, cy: Float) {
        val s = 1.4f
        shapeRenderer.color = GameConfig.COLOR_DIM
        shapeRenderer.line(cx - 38f * s, cy - 18f * s, cx - 8f * s, cy - 6f * s)
        shapeRenderer.line(cx + 38f * s, cy - 18f * s, cx + 8f * s, cy - 6f * s)
        shapeRenderer.line(cx - 38f * s, cy - 18f * s, cx - 28f * s, cy - 28f * s)
        shapeRenderer.line(cx + 38f * s, cy - 18f * s, cx + 28f * s, cy - 28f * s)
        shapeRenderer.color = GameConfig.COLOR_GRAY
        shapeRenderer.line(cx, cy + 22f * s, cx - 8f * s, cy - 6f * s)
        shapeRenderer.line(cx, cy + 22f * s, cx + 8f * s, cy - 6f * s)
        shapeRenderer.line(cx - 8f * s, cy - 6f * s, cx - 4f * s, cy - 22f * s)
        shapeRenderer.line(cx + 8f * s, cy - 6f * s, cx + 4f * s, cy - 22f * s)
        shapeRenderer.line(cx - 4f * s, cy - 22f * s, cx + 4f * s, cy - 22f * s)
        shapeRenderer.color = GameConfig.COLOR_AMBER
        shapeRenderer.circle(cx, cy + 8f * s, 3.5f * s, 12)
    }

    private fun drawText() {
        val w = GameConfig.WORLD_WIDTH
        val h = GameConfig.WORLD_HEIGHT
        batch.begin()
        font.data.setScale(2.8f); font.color = GameConfig.COLOR_AMBER
        drawCentered("AXIOM", w * 0.5f, h - 30f)
        font.data.setScale(1.4f); font.color = GameConfig.COLOR_GRAY
        drawCentered("SHOOTER", w * 0.5f, h - 60f)
        font.data.setScale(0.72f); font.color = GameConfig.COLOR_DIM
        drawRight("v1.0", w - 26f, h - 18f)
        menuItems.forEach { item ->
            font.data.setScale(1.15f)
            font.color = if (item.active) GameConfig.COLOR_AMBER else GameConfig.COLOR_GRAY
            if (item.active) {
                font.data.setScale(0.85f); font.color = GameConfig.COLOR_AMBER
                font.draw(batch, ">", 68f, item.y)
            }
            font.data.setScale(1.15f)
            font.color = if (item.active) GameConfig.COLOR_AMBER else GameConfig.COLOR_GRAY
            drawCentered(item.label, w * 0.5f, item.y)
        }
        font.data.setScale(0.72f); font.color = GameConfig.COLOR_DIM
        drawCentered("VOID  /  AXIOM  /  SYSTEM", w * 0.5f, 28f)
        if ((elapsed % 1.1f) < 0.55f) {
            font.color = GameConfig.COLOR_AMBER; font.data.setScale(0.62f)
            font.draw(batch, "[ ]", 28f, 28f)
        }
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
