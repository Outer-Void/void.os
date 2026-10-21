package com.outervoid.voidgame.entities

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.outervoid.voidgame.GameConfig

class Carrier {
    val width = 42f
    val height = 36f
    var x = (GameConfig.WORLD_WIDTH - width) * 0.5f
    var y = 124f
    var active = true
    var fireTimer = 0f
    var thrusterTimer = 0f
    var damageTimer = 0f

    fun update(delta: Float, targetCenterX: Float?) {
        if (!active) return
        thrusterTimer += delta
        damageTimer = (damageTimer - delta).coerceAtLeast(0f)
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

    fun registerHit() {
        damageTimer = 0.65f
    }

    /** Filled pass gives the interceptor a readable silhouette against tactical linework. */
    fun drawHull(renderer: ShapeRenderer) {
        if (!active) return
        val cx = x + width * 0.5f
        renderer.setColor(0.16f, 0.18f, 0.21f, 1f)
        renderer.triangle(cx, y + height + 5f, x + 3f, y + 7f, cx, y + 14f)
        renderer.triangle(cx, y + height + 5f, x + width - 3f, y + 7f, cx, y + 14f)
        renderer.setColor(0.34f, 0.39f, 0.44f, 1f)
        renderer.triangle(cx, y + 31f, cx - 4f, y + 23f, cx + 4f, y + 23f)
        renderer.setColor(0.77f, 0.65f, 0.42f, 1f)
        renderer.triangle(cx - 7f, y + 5f, cx - 10f, y - 6f, cx - 4f, y + 5f)
        renderer.triangle(cx + 7f, y + 5f, cx + 10f, y - 6f, cx + 4f, y + 5f)
    }

    fun draw(renderer: ShapeRenderer) {
        if (!active) return
        val cx = x + width * 0.5f
        renderer.color = if (damageTimer > 0f) GameConfig.COLOR_DANGER else GameConfig.COLOR_GRAY
        renderer.rectLine(cx, y + height + 5f, cx - 10f, y + height * 0.52f, 2.8f)
        renderer.rectLine(cx, y + height + 5f, cx + 10f, y + height * 0.52f, 2.8f)
        renderer.rectLine(cx - 10f, y + height * 0.52f, x + 3f, y + 7f, 2.6f)
        renderer.rectLine(cx + 10f, y + height * 0.52f, x + width - 3f, y + 7f, 2.6f)
        renderer.rectLine(x + 3f, y + 7f, cx - 9f, y + 11f, 1.8f)
        renderer.rectLine(x + width - 3f, y + 7f, cx + 9f, y + 11f, 1.8f)
        renderer.rectLine(cx - 7f, y + 12f, cx, y + height + 2f, 3f)
        renderer.rectLine(cx + 7f, y + 12f, cx, y + height + 2f, 3f)
        renderer.color = GameConfig.COLOR_DIM
        renderer.line(x + 9f, y + 14f, cx - 7f, y + 18f)
        renderer.line(x + width - 9f, y + 14f, cx + 7f, y + 18f)
        renderer.line(cx - 8f, y + 7f, cx + 8f, y + 7f)
        renderer.color = if (damageTimer > 0f) GameConfig.COLOR_DANGER else GameConfig.COLOR_AMBER
        renderer.line(cx, y + 31f, cx - 4f, y + 23f)
        renderer.line(cx - 4f, y + 23f, cx, y + 15f)
        renderer.line(cx, y + 15f, cx + 4f, y + 23f)
        renderer.line(cx + 4f, y + 23f, cx, y + 31f)
        renderer.color = GameConfig.COLOR_ORANGE
        val flare = 4f + (MathUtils.sin(thrusterTimer * 18f) + 1f) * 2.5f
        renderer.rectLine(cx - 7f, y + 5f, cx - 8f, y - flare, 2.6f)
        renderer.rectLine(cx + 7f, y + 5f, cx + 8f, y - flare, 2.6f)
        renderer.color = GameConfig.COLOR_AMBER
        renderer.line(cx - 7f, y + 3f, cx - 8f, y - flare * 0.72f)
        renderer.line(cx + 7f, y + 3f, cx + 8f, y - flare * 0.72f)
    }
}
