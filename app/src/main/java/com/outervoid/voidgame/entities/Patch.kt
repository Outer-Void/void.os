package com.outervoid.voidgame.entities

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Rectangle
import com.outervoid.voidgame.GameConfig

enum class PatchType { FIREWALL, OVERCLOCK, DEFRAG }

class Patch(var x: Float, var y: Float, val type: PatchType) {
    var active = true
    val size = 18f

    fun update(delta: Float) {
        if (!active) return
        y -= 60f * delta
        if (y < -size) active = false
    }

    fun bounds(out: Rectangle): Rectangle = out.set(x - size * 0.5f, y - size * 0.5f, size, size)

    fun draw(renderer: ShapeRenderer) {
        if (!active) return
        renderer.color = GameConfig.COLOR_SURFACE
        renderer.rect(x - size * 0.5f, y - size * 0.5f, size, size)
        when (type) {
            PatchType.FIREWALL -> {
                renderer.color = GameConfig.COLOR_GRAY
                renderer.line(x, y + 7f, x + 7f, y + 1f)
                renderer.line(x + 7f, y + 1f, x + 4f, y - 8f)
                renderer.line(x + 4f, y - 8f, x - 4f, y - 8f)
                renderer.line(x - 4f, y - 8f, x - 7f, y + 1f)
                renderer.line(x - 7f, y + 1f, x, y + 7f)
            }
            PatchType.OVERCLOCK -> {
                renderer.color = GameConfig.COLOR_AMBER
                renderer.line(x + 2f, y + 8f, x - 5f, y - 1f)
                renderer.line(x - 5f, y - 1f, x + 1f, y - 1f)
                renderer.line(x + 1f, y - 1f, x - 2f, y - 9f)
                renderer.line(x - 2f, y - 9f, x + 7f, y + 2f)
                renderer.line(x + 7f, y + 2f, x + 1f, y + 2f)
                renderer.line(x + 1f, y + 2f, x + 2f, y + 8f)
            }
            PatchType.DEFRAG -> {
                renderer.color = GameConfig.COLOR_AMBER
                renderer.circle(x - 4f, y + 4f, 2f, 10)
                renderer.circle(x + 4f, y - 4f, 2f, 10)
                renderer.line(x + 6f, y + 6f, x - 6f, y - 6f)
            }
        }
    }
}
