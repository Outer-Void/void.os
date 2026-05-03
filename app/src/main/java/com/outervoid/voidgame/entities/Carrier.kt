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
        renderer.color = GameConfig.COLOR_GRAY
        renderer.rectLine(x, y, x + width * 0.5f, y + height, 2.5f)
        renderer.rectLine(x + width, y, x + width * 0.5f, y + height, 2.5f)
        renderer.rectLine(x + width * 0.28f, y + height * 0.45f, x + width * 0.72f, y + height * 0.45f, 2.5f)
        renderer.color = GameConfig.COLOR_AMBER
        renderer.circle(x + width * 0.5f, y + height, 3f, 16)
    }
}
