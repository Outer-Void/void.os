package com.outervoid.voidgame.entities

import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.outervoid.voidgame.GameConfig

enum class FragmentType(val maxHp: Int, val signal: Int) {
    SHARD(1, 20),
    PULSE(2, 45),
    GLITCH(1, 35),
    CLUSTER(2, 60)
}

class Fragment(
    var x: Float,
    var y: Float,
    val type: FragmentType
) {
    var hp = type.maxHp
    var active = true
    var diving = false
    var diveTargetX = x
    var diveTargetY = -20f
    var pulseTimer = MathUtils.random(0.4f, 2.5f)
    var glitchTimer = 0.15f
    var glitchOffset = 0f
    val size = 24f

    fun update(delta: Float, cycle: Int, carrierX: Float, carrierY: Float): Boolean {
        if (!active) return false
        var fired = false
        val descend = GameConfig.ENEMY_DESCEND_SPEED_BASE + cycle * GameConfig.ENEMY_DESCEND_SPEED_SCALE

        if (!diving && MathUtils.random() < GameConfig.DIVE_CHANCE * cycle.coerceAtMost(8)) {
            diving = true
            diveTargetX = carrierX
            diveTargetY = carrierY
        }

        if (diving) {
            val angle = MathUtils.atan2(diveTargetY - y, diveTargetX - x)
            x += MathUtils.cos(angle) * GameConfig.DIVE_SPEED * delta
            y += MathUtils.sin(angle) * GameConfig.DIVE_SPEED * delta
            if (y < 0f) active = false
        } else {
            y -= descend * delta
        }

        if (type == FragmentType.GLITCH) {
            glitchTimer -= delta
            if (glitchTimer <= 0f) {
                glitchOffset = MathUtils.random(-30f, 30f)
                x = MathUtils.clamp(x + glitchOffset, 18f, GameConfig.WORLD_WIDTH - 18f)
                glitchTimer = 0.15f
            }
        }

        if (type == FragmentType.PULSE) {
            pulseTimer -= delta
            if (pulseTimer <= 0f) {
                fired = true
                pulseTimer = 2.5f
            }
        }

        if (y < -size) active = false
        return fired
    }

    fun bounds(out: Rectangle): Rectangle = out.set(x - size * 0.5f, y - size * 0.5f, size, size)

    fun draw(renderer: ShapeRenderer, elapsed: Float) {
        if (!active) return
        when (type) {
            FragmentType.SHARD -> drawShard(renderer)
            FragmentType.PULSE -> drawPulse(renderer, elapsed)
            FragmentType.GLITCH -> drawGlitch(renderer, elapsed)
            FragmentType.CLUSTER -> drawCluster(renderer)
        }
    }

    private fun drawShard(renderer: ShapeRenderer) {
        renderer.color = GameConfig.COLOR_GRAY
        val x1 = x - 12f
        val y1 = y - 8f
        val x2 = x + 10f
        val y2 = y - 12f
        val x3 = x + 4f
        val y3 = y + 14f
        renderer.line(x1, y1, x2, y2)
        renderer.line(x2, y2, x3, y3)
        renderer.line(x3, y3, x1, y1)
    }

    private fun drawPulse(renderer: ShapeRenderer, elapsed: Float) {
        renderer.color = GameConfig.COLOR_GRAY
        val r = 13f
        var lastX = x + r
        var lastY = y
        for (i in 1..6) {
            val a = MathUtils.PI2 * i / 6f
            val nx = x + MathUtils.cos(a) * r
            val ny = y + MathUtils.sin(a) * r
            renderer.line(lastX, lastY, nx, ny)
            lastX = nx
            lastY = ny
        }
        renderer.color = GameConfig.COLOR_AMBER
        renderer.circle(x, y, 2f + (MathUtils.sin(elapsed * 6f) + 1f) * 0.9f, 12)
    }

    private fun drawGlitch(renderer: ShapeRenderer, elapsed: Float) {
        renderer.color = GameConfig.COLOR_DIM
        val flicker = if ((elapsed * 10f).toInt() % 2 == 0) 2f else -2f
        renderer.rect(x - 10f + flicker, y - 8f, 20f, 16f)
        renderer.color = GameConfig.COLOR_BACKGROUND
        renderer.rect(x - 7f + flicker, y - 5f, 14f, 10f)
    }

    private fun drawCluster(renderer: ShapeRenderer) {
        renderer.color = GameConfig.COLOR_GRAY
        renderer.line(x - 8f, y - 4f, x + 8f, y - 4f)
        renderer.line(x, y + 9f, x - 8f, y - 4f)
        renderer.line(x, y + 9f, x + 8f, y - 4f)
        renderer.circle(x - 8f, y - 4f, 5f, 12)
        renderer.circle(x + 8f, y - 4f, 5f, 12)
        renderer.circle(x, y + 9f, 5f, 12)
    }
}
