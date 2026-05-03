package com.outervoid.voidgame.entities

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.outervoid.voidgame.GameConfig

class VoidCore {
    var x = GameConfig.WORLD_WIDTH * 0.5f
    var y = GameConfig.WORLD_HEIGHT - 96f
    var hp = 12
    var active = true
    var phase = 1
    var direction = 1f
    var fireTimer = 1.1f
    var dropTimer = 2.8f
    val size = 58f

    fun update(delta: Float): Boolean {
        if (!active) return false
        x += direction * 72f * delta
        if (x < 48f || x > GameConfig.WORLD_WIDTH - 48f) {
            direction *= -1f
            x = MathUtils.clamp(x, 48f, GameConfig.WORLD_WIDTH - 48f)
        }
        dropTimer -= delta
        if (dropTimer <= 0f) {
            y -= 18f
            if (y < GameConfig.WORLD_HEIGHT - 170f) y = GameConfig.WORLD_HEIGHT - 96f
            dropTimer = 2.8f
        }
        fireTimer -= delta
        if (fireTimer <= 0f) {
            fireTimer = 1.35f.coerceAtLeast(1.9f - phase * 0.2f)
            return true
        }
        return false
    }

    fun bounds(out: Rectangle): Rectangle = out.set(x - size * 0.5f, y - size * 0.5f, size, size)

    fun draw(renderer: ShapeRenderer) {
        if (!active) return
        renderer.color = GameConfig.COLOR_DIM.cpy().also { it.a = 0.42f }
        renderer.line(x - 40f, y - 12f, x - 18f, y + 18f)
        renderer.line(x + 40f, y - 12f, x + 18f, y + 18f)
        renderer.line(x - 40f, y - 12f, x + 40f, y - 12f)
        renderer.color = GameConfig.COLOR_SURFACE
        renderer.circle(x, y, size * 0.44f, 36)
        renderer.color = GameConfig.COLOR_AMBER
        for (i in 0 until 6) {
            renderer.arc(x, y, size * 0.5f, i * 60f + 8f, 38f, 12)
        }
        renderer.color = GameConfig.COLOR_GRAY
        renderer.circle(x, y, size * 0.21f, 24)
        renderer.line(x - 12f, y, x + 12f, y)
        renderer.line(x, y - 12f, x, y + 12f)
        renderer.color = GameConfig.COLOR_ORANGE
        renderer.circle(x, y, size * 0.08f, 16)
    }
}
