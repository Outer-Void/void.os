package com.outervoid.voidgame.entities

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.outervoid.voidgame.GameConfig

class EnemyBullet {
    var x = 0f
    var y = 0f
    var vx = 0f
    var active = false
    val width = 2f
    val height = 12f

    fun spawn(startX: Float, startY: Float, velocityX: Float = 0f) {
        x = startX
        y = startY
        vx = velocityX
        active = true
    }

    fun update(delta: Float) {
        if (!active) return
        x += vx * delta
        y -= GameConfig.BULLET_SPEED * 0.55f * delta
        if (y < -height || x < -height || x > GameConfig.WORLD_WIDTH + height) active = false
    }

    fun bounds(out: Rectangle): Rectangle = out.set(x - width * 0.5f, y, width, height)

    fun draw(renderer: ShapeRenderer) {
        if (!active) return
        renderer.color = GameConfig.COLOR_DANGER
        renderer.rectLine(x, y - 2f, x, y + height + 3f, 4f)
        renderer.color = GameConfig.COLOR_ORANGE
        renderer.rectLine(x, y, x, y + height, 2f)
        renderer.line(x - 3f, y + height - 3f, x, y + height + 4f)
        renderer.line(x + 3f, y + height - 3f, x, y + height + 4f)
    }
}
