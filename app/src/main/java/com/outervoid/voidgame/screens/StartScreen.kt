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

class StartScreen(private val game: VoidGame) : Screen {
    private val camera = OrthographicCamera()
    private val viewport = FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT, camera)
    private val shapeRenderer = ShapeRenderer()
    private val batch = SpriteBatch()
    private val font = BitmapFont()
    private val layout = GlyphLayout()
    private val touch = Vector3()
    private var elapsed = 0f

    private data class MenuItem(
        val label: String,
        val subtitle: String,
        val y: Float,
        val bounds: Rectangle,
        val active: Boolean = false
    )

    private val menuItems = listOf(
        MenuItem("CAMPAIGN", "STORY MISSIONS", 344f, Rectangle(58f, 316f, 244f, 42f), active = true),
        MenuItem("SURVIVAL", "ENDLESS MODE", 296f, Rectangle(58f, 268f, 244f, 42f)),
        MenuItem("TRAINING", "PRACTICE RANGE", 248f, Rectangle(58f, 220f, 244f, 42f)),
        MenuItem("SETTINGS", "SHIP / EFFECTS", 200f, Rectangle(58f, 172f, 244f, 42f)),
        MenuItem("LEADERBOARDS", "LOCAL RANKING", 152f, Rectangle(58f, 124f, 244f, 42f)),
        MenuItem("EXIT TO AXIOM", "RETURN HOME", 104f, Rectangle(58f, 76f, 244f, 42f))
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
            menuItems[4].bounds.contains(touch.x, touch.y) -> game.setScreen(ScoreScreen(game))
            menuItems[5].bounds.contains(touch.x, touch.y) -> game.openAxiom()
        }
    }

    private fun drawFrame() {
        val w = GameConfig.WORLD_WIDTH
        val h = GameConfig.WORLD_HEIGHT
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        ShooterVisuals.drawSpaceBackdrop(shapeRenderer, elapsed)
        shapeRenderer.end()

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line)
        ShooterVisuals.drawSpaceLineDetails(shapeRenderer, elapsed)
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
        menuItems.forEach { item ->
            ShooterVisuals.drawPanelFrame(shapeRenderer, item.bounds.x, item.bounds.y, item.bounds.width, item.bounds.height, item.active)
        }
        drawShipPreview(w * 0.5f, h - 74f)
        shapeRenderer.end()
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        val pulse = (MathUtils.sin(elapsed * 2.8f) + 1f) * 0.5f
        ShooterVisuals.setAlphaColor(shapeRenderer, GameConfig.COLOR_AMBER, 0.04f + pulse * 0.04f)
        shapeRenderer.rect(menuItems.first().bounds.x, menuItems.first().bounds.y, menuItems.first().bounds.width, menuItems.first().bounds.height)
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
        drawRight("2.0 DEV", w - 26f, h - 18f)
        menuItems.forEach { item ->
            font.data.setScale(1.15f)
            font.color = if (item.active) GameConfig.COLOR_AMBER else GameConfig.COLOR_GRAY
            if (item.active) {
                font.data.setScale(0.85f); font.color = GameConfig.COLOR_AMBER
                font.draw(batch, ">", 70f, item.y - 3f)
            }
            font.data.setScale(1.15f)
            font.color = if (item.active) GameConfig.COLOR_AMBER else GameConfig.COLOR_GRAY
            drawCentered(item.label, w * 0.5f, item.y)
            font.data.setScale(0.55f)
            font.color = GameConfig.COLOR_DIM
            drawCentered(item.subtitle, w * 0.5f, item.y - 16f)
        }
        font.data.setScale(0.72f); font.color = GameConfig.COLOR_DIM
        drawCentered("VOID. FOCUS. DESTROY.", w * 0.5f, 28f)
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
