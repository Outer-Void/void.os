package com.outervoid.voidgame

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils

object ShooterVisuals {
    private val tint = Color()
    private val stars = Array(78) { index ->
        Star(
            x = ((index * 83 + 17) % 360).toFloat(),
            y = ((index * 197 + 61) % 640).toFloat(),
            size = if (index % 11 == 0) 2.2f else if (index % 3 == 0) 1.5f else 1f,
            speed = 5f + (index % 7) * 2.7f,
            phase = index * 13.7f,
            alpha = 0.08f + (index % 5) * 0.035f
        )
    }

    fun drawSpaceBackdrop(renderer: ShapeRenderer, elapsed: Float) {
        renderer.color = GameConfig.COLOR_BACKGROUND
        renderer.rect(0f, 0f, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT)

        for (index in stars.indices) {
            val star = stars[index]
            val y = GameConfig.WORLD_HEIGHT - ((star.y + elapsed * star.speed) % GameConfig.WORLD_HEIGHT)
            val shimmer = 0.78f + 0.22f * MathUtils.sin(elapsed * 1.3f + star.phase)
            setTint(renderer, 0.78f, 0.82f, 0.88f, star.alpha * shimmer)
            renderer.rect(star.x, y, star.size, star.size)
        }

        setTint(renderer, 1f, 0.82f, 0f, 0.025f)
        renderer.circle(55f, 510f, 136f, 48)
        setTint(renderer, 0.9f, 0.18f, 0.16f, 0.018f)
        renderer.circle(322f, 420f, 102f, 40)
    }

    fun setAlphaColor(renderer: ShapeRenderer, color: Color, alpha: Float) {
        tint.set(color)
        tint.a = alpha.coerceIn(0f, 1f)
        renderer.color = tint
    }

    fun drawSpaceLineDetails(renderer: ShapeRenderer, elapsed: Float) {
        drawAsteroid(renderer, 45f + MathUtils.sin(elapsed * 0.18f) * 8f, 424f - (elapsed * 9f % 520f), 18f, elapsed)
        drawAsteroid(renderer, 308f, 560f - (elapsed * 13f % 660f), 13f, elapsed + 1.3f)
        drawAsteroid(renderer, 248f, 332f - (elapsed * 7f % 480f), 24f, elapsed + 2.1f)

        setTint(renderer, 0.78f, 0.82f, 0.88f, 0.025f)
        var x = 24f
        while (x < GameConfig.WORLD_WIDTH) {
            renderer.line(x, 116f, x, GameConfig.WORLD_HEIGHT - 72f)
            x += 48f
        }
        setTint(renderer, 1f, 0.82f, 0f, 0.022f)
        var y = 140f
        while (y < GameConfig.WORLD_HEIGHT - 80f) {
            renderer.line(14f, y, GameConfig.WORLD_WIDTH - 14f, y)
            y += 48f
        }
    }

    fun drawPanelFrame(renderer: ShapeRenderer, x: Float, y: Float, w: Float, h: Float, active: Boolean = false) {
        setTint(renderer, 0.08f, 0.08f, 0.08f, 0.92f)
        renderer.rect(x, y, w, h)
        if (active) renderer.color = GameConfig.COLOR_AMBER else setTint(renderer, 0.28f, 0.28f, 0.28f, 0.74f)
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
        setTint(renderer, 0.07f, 0.07f, 0.07f, 0.82f)
        renderer.circle(cx, cy, r, 40)
        if (active) renderer.color = GameConfig.COLOR_AMBER else setTint(renderer, 0.32f, 0.32f, 0.32f, 0.78f)
        renderer.circle(cx, cy, r, 40)
        renderer.circle(cx, cy, r * 0.56f, 28)
    }

    private fun drawAsteroid(renderer: ShapeRenderer, cx: Float, cy: Float, radius: Float, phase: Float) {
        val y = if (cy < -40f) cy + 720f else cy
        setTint(renderer, 0.34f, 0.31f, 0.27f, 0.54f)
        var lastX = cx + radius
        var lastY = y
        for (i in 1..8) {
            val angle = MathUtils.PI2 * i / 8f
            val localRadius = radius * (0.76f + 0.2f * MathUtils.sin(phase + i * 1.7f))
            val nextX = cx + MathUtils.cos(angle) * localRadius
            val nextY = y + MathUtils.sin(angle) * localRadius
            renderer.line(lastX, lastY, nextX, nextY)
            lastX = nextX
            lastY = nextY
        }
        setTint(renderer, 0.32f, 0.32f, 0.32f, 0.28f)
        renderer.line(cx - radius * 0.35f, y + radius * 0.2f, cx + radius * 0.28f, y - radius * 0.24f)
        setTint(renderer, 1f, 0.82f, 0f, 0.36f)
        renderer.circle(cx - radius * 0.18f, y + radius * 0.18f, 1.4f, 8)
    }

    private fun setTint(renderer: ShapeRenderer, red: Float, green: Float, blue: Float, alpha: Float) {
        tint.set(red, green, blue, alpha)
        renderer.color = tint
    }

    private data class Star(val x: Float, val y: Float, val size: Float, val speed: Float, val phase: Float, val alpha: Float)
}
