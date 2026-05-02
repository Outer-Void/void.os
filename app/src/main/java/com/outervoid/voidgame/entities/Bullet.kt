package com.outervoid.voidgame.entities

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.outervoid.voidgame.GameConfig

class Bullet {
    var x = 0f
    var y = 0f
    var active = false
    val width = 2f
    val height = 18f

    fun spawn(startX: Float, startY: Float) {
        x = startX
        y = startY
        active = true
    }

    fun update(delta: Float) {
        if (!active) return
        y += GameConfig.BULLET_SPEED * delta
        if (y > GameConfig.WORLD_HEIGHT + height) active = false
    }

    fun bounds(out: Rectangle): Rectangle = out.set(x - width * 0.5f, y, width, height)

    fun draw(renderer: ShapeRenderer) {
        if (!active) return
        renderer.color = GameConfig.COLOR_AMBER
        renderer.rectLine(x, y, x, y + height, width)
    }
}
