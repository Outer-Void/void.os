package com.outervoid.voidgame

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils

object ShooterVisuals {
    fun drawSpaceBackdrop(renderer: ShapeRenderer, elapsed: Float) {
        renderer.color = GameConfig.COLOR_BACKGROUND
        renderer.rect(0f, 0f, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT)

        for (i in 0 until 34) {
            val x = ((i * 47) % 360).toFloat()
            val drift = (elapsed * (5f + i % 5) + i * 19f) % 700f
            val y = GameConfig.WORLD_HEIGHT - drift + 36f
            val alpha = 0.10f + ((i % 4) * 0.025f)
            renderer.color = Color.WHITE.cpy().also { it.a = alpha }
            renderer.rect(x, y, if (i % 6 == 0) 2f else 1f, if (i % 6 == 0) 2f else 1f)
        }

        renderer.color = GameConfig.COLOR_AMBER.cpy().also { it.a = 0.035f }
        renderer.circle(58f, 552f, 142f, 48)
        renderer.color = GameConfig.COLOR_DANGER.cpy().also { it.a = 0.03f }
        renderer.circle(322f, 468f, 96f, 40)

    }

    fun drawSpaceLineDetails(renderer: ShapeRenderer, elapsed: Float) {
        drawAsteroid(renderer, 45f + MathUtils.sin(elapsed * 0.18f) * 8f, 424f - (elapsed * 9f % 520f), 18f, elapsed)
        drawAsteroid(renderer, 308f, 560f - (elapsed * 13f % 660f), 13f, elapsed + 1.3f)
        drawAsteroid(renderer, 248f, 332f - (elapsed * 7f % 480f), 24f, elapsed + 2.1f)
    }

    fun drawPanelFrame(renderer: ShapeRenderer, x: Float, y: Float, w: Float, h: Float, active: Boolean = false) {
        renderer.color = GameConfig.COLOR_SURFACE.cpy().also { it.a = 0.82f }
        renderer.rect(x, y, w, h)
        renderer.color = if (active) GameConfig.COLOR_AMBER else GameConfig.COLOR_DIM.cpy().also { it.a = 0.42f }
        renderer.rect(x, y, w, h)
        drawCornerBrackets(renderer, x, y, w, h, 11f)
    }

    fun drawCornerBrackets(renderer: ShapeRenderer, x: Float, y: Float, w: Float, h: Float, len: Float = 14f) {
        renderer.line(x, y + h, x + len, y + h)
        renderer.line(x, y + h, x, y + h - len)
        renderer.line(x + w, y + h, x + w - len, y + h)
        renderer.line(x + w, y + h, x + w, y + h - len)
        renderer.line(x, y, x + len, y)
        renderer.line(x, y, x, y + len)
        renderer.line(x + w, y, x + w - len, y)
        renderer.line(x + w, y, x + w, y + len)
    }

    fun drawControlRing(renderer: ShapeRenderer, cx: Float, cy: Float, r: Float, active: Boolean = false) {
        renderer.color = GameConfig.COLOR_SURFACE.cpy().also { it.a = 0.76f }
        renderer.circle(cx, cy, r, 40)
        renderer.color = if (active) GameConfig.COLOR_AMBER else GameConfig.COLOR_DIM.cpy().also { it.a = 0.46f }
        renderer.circle(cx, cy, r, 40)
        renderer.circle(cx, cy, r * 0.56f, 28)
    }

    private fun drawAsteroid(renderer: ShapeRenderer, cx: Float, cy: Float, radius: Float, phase: Float) {
        val y = if (cy < -40f) cy + 720f else cy
        renderer.color = GameConfig.COLOR_ASTEROID.cpy().also { it.a = 0.34f }
        var lastX = cx + radius
        var lastY = y
        for (i in 1..8) {
            val a = MathUtils.PI2 * i / 8f
            val localR = radius * (0.72f + 0.24f * MathUtils.sin(phase + i * 1.7f))
            val nextX = cx + MathUtils.cos(a) * localR
            val nextY = y + MathUtils.sin(a) * localR
            renderer.line(lastX, lastY, nextX, nextY)
            lastX = nextX
            lastY = nextY
        }
        renderer.color = GameConfig.COLOR_DIM.cpy().also { it.a = 0.16f }
        renderer.line(cx - radius * 0.35f, y + radius * 0.2f, cx + radius * 0.28f, y - radius * 0.24f)
    }
}
