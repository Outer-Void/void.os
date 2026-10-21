package com.outervoid.axiom.ui.screens

/** Keep label columns readable as available width decreases or text grows. */
internal fun drawerColumnCount(widthDp: Int, fontScale: Float, requested: Int): Int {
    val cellWidth = 68f * fontScale.coerceIn(1f, 1.5f)
    return requested.coerceIn(3, 6).coerceAtMost(((widthDp - 36) / cellWidth).toInt().coerceAtLeast(2))
}
