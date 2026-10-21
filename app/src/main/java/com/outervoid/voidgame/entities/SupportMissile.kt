package com.outervoid.voidgame.entities

import kotlin.math.sqrt

/** A single bounded homing projectile; damage is applied only on arrival. */
internal class SupportMissile {
    var active = false
        private set
    var x = 0f
        private set
    var y = 0f
        private set

    fun launch(startX: Float, startY: Float): Boolean {
        if (active) return false
        x = startX
        y = startY
        active = true
        return true
    }

    fun cancel() { active = false }

    fun advance(delta: Float, targetX: Float, targetY: Float): Boolean {
        if (!active || !delta.isFinite() || delta <= 0f) return false
        val dx = targetX - x
        val dy = targetY - y
        val distance = sqrt(dx * dx + dy * dy)
        val travel = 420f * delta
        if (distance <= travel) {
            x = targetX
            y = targetY
            active = false
            return true
        }
        x += dx / distance * travel
        y += dy / distance * travel
        return false
    }
}
