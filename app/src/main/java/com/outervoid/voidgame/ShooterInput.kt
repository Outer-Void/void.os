package com.outervoid.voidgame

/** World-space zones: 360x640, separate 72-unit support weapon targets. */
internal enum class ShooterTouchRole { Move, Missile, Bomb, Pause, None, Ignored }

internal fun shooterTouchRole(x: Float, y: Float): ShooterTouchRole = when {
    x !in 0f..360f || y !in 0f..640f -> ShooterTouchRole.None
    y >= 572f -> if (x in 126f..234f) ShooterTouchRole.Pause else ShooterTouchRole.None
    y <= 72f && x >= 288f -> ShooterTouchRole.Bomb
    y <= 72f && x >= 216f -> ShooterTouchRole.Missile
    else -> ShooterTouchRole.Move
}

/** Bounded fixed steps; discard stalls rather than replaying seconds of unseen combat. */
internal class ShooterClock {
    private var accumulator = 0f
    fun advance(delta: Float, update: (Float) -> Boolean) {
        if (!delta.isFinite() || delta <= 0f) return
        accumulator += delta.coerceAtMost(0.1f)
        var steps = 0
        while (accumulator >= STEP && steps < 6) {
            accumulator -= STEP
            steps++
            if (!update(STEP)) { reset(); return }
        }
    }
    fun reset() { accumulator = 0f }
    companion object { const val STEP = 1f / 60f }
}

/** Pointer IDs own one role until release. Pause quarantines held touches until lifted. */
internal class ShooterControlState {
    private val roles = Array(20) { ShooterTouchRole.None }
    var movementPointer = -1
        private set
    val pointerCount: Int get() = roles.size
    fun isPressed(role: ShooterTouchRole): Boolean = roles.any { it == role }

    fun update(pointer: Int, down: Boolean, x: Float, y: Float): ShooterTouchRole {
        if (!down) {
            roles[pointer] = ShooterTouchRole.None
            if (movementPointer == pointer) movementPointer = -1
            return ShooterTouchRole.None
        }
        val fresh = roles[pointer] == ShooterTouchRole.None
        if (fresh) roles[pointer] = shooterTouchRole(x, y).let {
            if (it == ShooterTouchRole.None) ShooterTouchRole.Ignored else it
        }
        if (roles[pointer] == ShooterTouchRole.Move && movementPointer == -1) movementPointer = pointer
        return if (fresh) roles[pointer] else ShooterTouchRole.None
    }

    fun quarantine() {
        roles.fill(ShooterTouchRole.Ignored)
        movementPointer = -1
    }
}
