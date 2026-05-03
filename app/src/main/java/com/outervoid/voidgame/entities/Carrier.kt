package com.outervoid.voidgame.entities

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.outervoid.voidgame.GameConfig

class Carrier {
    val width = 42f
    val height = 36f
    var x = (GameConfig.WORLD_WIDTH - width) * 0.5f
    var y = 44f
    var active = true
    var fireTimer = 0f
    var thrusterTimer = 0f

    fun update(delta: Float, targetCenterX: Float?) {
        if (!active) return
        thrusterTimer += delta
        if (targetCenterX != null) {
            val targetX = targetCenterX - width * 0.5f
            x = MathUtils.clamp(targetX, 0f, GameConfig.WORLD_WIDTH - width)
        }
        fireTimer -= delta
    }

    fun tryFire(pool: Array<Bullet>): Boolean {
        if (!active || fireTimer > 0f) return false
        val bullet = pool.firstOrNull { !it.active } ?: return false
        bullet.spawn(x + width * 0.5f, y + height + 2f)
        fireTimer = GameConfig.BULLET_COOLDOWN
        return true
    }

    fun bounds(out: Rectangle): Rectangle = out.set(x + 5f, y, width - 10f, height - 4f)

    fun draw(renderer: ShapeRenderer) {
        if (!active) return
        val cx = x + width * 0.5f
        renderer.color = GameConfig.COLOR_GRAY
        renderer.rectLine(cx, y + height + 4f, x + 8f, y + 5f, 2.4f)
        renderer.rectLine(cx, y + height + 4f, x + width - 8f, y + 5f, 2.4f)
        renderer.rectLine(x + 4f, y + 6f, x + width * 0.34f, y + height * 0.42f, 2.2f)
        renderer.rectLine(x + width - 4f, y + 6f, x + width * 0.66f, y + height * 0.42f, 2.2f)
        renderer.rectLine(cx, y + height + 2f, cx, y + 8f, 2.8f)
        renderer.color = GameConfig.COLOR_DIM
        renderer.line(x + 12f, y + 15f, x + width - 12f, y + 15f)
        renderer.line(x + 16f, y + 4f, x + width - 16f, y + 4f)
        renderer.color = GameConfig.COLOR_AMBER
        renderer.circle(cx, y + height * 0.72f, 3.5f, 16)
        renderer.color = GameConfig.COLOR_ORANGE
        val flare = 4f + (MathUtils.sin(thrusterTimer * 18f) + 1f) * 2.5f
        renderer.line(cx - 6f, y + 3f, cx - 2f, y - flare)
        renderer.line(cx + 6f, y + 3f, cx + 2f, y - flare)
    }
}
