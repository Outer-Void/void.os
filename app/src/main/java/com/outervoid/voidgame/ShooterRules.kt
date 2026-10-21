package com.outervoid.voidgame

/** Deterministic game rules kept independent of rendering and Android state. */
internal object ShooterRules {
    fun frameStep(deltaSeconds: Float): Float = deltaSeconds.coerceAtLeast(0f).coerceAtMost(1f / 30f)

    fun overlaps(
        leftA: Float,
        bottomA: Float,
        widthA: Float,
        heightA: Float,
        leftB: Float,
        bottomB: Float,
        widthB: Float,
        heightB: Float
    ): Boolean =
        leftA < leftB + widthB && leftA + widthA > leftB &&
            bottomA < bottomB + heightB && bottomA + heightA > bottomB

    fun topScores(existing: List<Long>, score: Long, limit: Int = 5): List<Long> =
        (existing + score).sortedDescending().take(limit.coerceAtLeast(0))

    fun campaignMissionComplete(cycle: Int, targetWaves: Int): Boolean =
        targetWaves > 0 && cycle >= targetWaves

    fun useWeaponCharge(charges: Int): Int? = charges.takeIf { it > 0 }?.minus(1)
}
