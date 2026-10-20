package com.outervoid.axiom.ui.screens

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.awaitEachGesture
import androidx.compose.ui.input.pointer.awaitFirstDown
import androidx.compose.ui.input.pointer.awaitPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import kotlin.math.abs

internal enum class SwipeAxis { Horizontal, Vertical }

/** Locks a gesture to its dominant axis once cumulative motion passes touch slop. */
internal fun lockedSwipeAxis(dx: Float, dy: Float, touchSlop: Float): SwipeAxis? {
    if (maxOf(abs(dx), abs(dy)) <= touchSlop) return null
    return if (abs(dx) > abs(dy)) SwipeAxis.Horizontal else SwipeAxis.Vertical
}

internal fun cumulativeSwipeDistance(dx: Float, dy: Float, axis: SwipeAxis): Float =
    if (axis == SwipeAxis.Horizontal) dx else dy

internal fun Modifier.detectLauncherSwipe(
    key: Any?,
    triggerDistancePx: Float,
    onSwipe: (SwipeAxis, Float, Float) -> Unit
): Modifier = pointerInput(key) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        var pointerId = down.id
        var totalX = 0f
        var totalY = 0f
        var axis: SwipeAxis? = null
        var claimed = false
        var dispatched = false
        var active = true
        while (active) {
            val event = awaitPointerEvent(PointerEventPass.Main)
            val change = event.changes.firstOrNull { it.id == pointerId }
            if (change == null || !change.pressed) {
                active = false
            } else if (change.isConsumed && !claimed) {
                // A scrollable child or Android widget owns this gesture.
                active = false
            } else {
                val delta = change.positionChange()
                totalX += delta.x
                totalY += delta.y
                if (axis == null) axis = lockedSwipeAxis(totalX, totalY, viewConfiguration.touchSlop)
                if (axis != null) {
                    claimed = true
                    change.consume()
                    val distance = cumulativeSwipeDistance(totalX, totalY, axis!!)
                    if (!dispatched && abs(distance) >= triggerDistancePx) {
                        dispatched = true
                        onSwipe(axis!!, distance, down.position.y)
                    }
                }
            }
        }
    }
}
