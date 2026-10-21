package com.outervoid.axiom.ui.screens

/** Budget both cell width and actual grid gaps; two-line labels keep a uniform row height. */
internal fun drawerColumnCount(widthDp: Int, fontScale: Float, requested: Int, compact: Boolean = false): Int {
    val scale = if (fontScale.isFinite()) fontScale.coerceIn(1f, 2f) else 1f
    val cellWidth = 68f * scale
    val padding = if (compact) 24 else 36
    val gap = if (compact) 6 else 10
    val fitting = ((widthDp - padding + gap) / (cellWidth + gap)).toInt().coerceAtLeast(2)
    return requested.coerceIn(3, 6).coerceAtMost(fitting)
}
