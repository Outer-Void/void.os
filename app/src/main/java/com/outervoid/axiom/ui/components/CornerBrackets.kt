package com.outervoid.axiom.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.ui.theme.Amber
import com.outervoid.axiom.ui.theme.BorderDim

@Composable
fun CornerBrackets(
    modifier: Modifier = Modifier,
    insetDp: Dp = 18.dp,
    topPadding: Dp = 56.dp,
    bottomPadding: Dp = 110.dp,
    alpha: Float = 0.82f
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val inset = insetDp.toPx()
        val top = topPadding.toPx()
        val length = 30.dp.toPx()
        val stroke = 1.dp.toPx()
        val right = size.width - inset
        val bottom = size.height - bottomPadding.toPx()

        fun bracket(origin: Offset, xSign: Float, ySign: Float, color: Color) {
            drawLine(
                color = color.copy(alpha = alpha),
                start = origin,
                end = origin + Offset(length * xSign, 0f),
                strokeWidth = stroke,
                cap = StrokeCap.Square
            )
            drawLine(
                color = color.copy(alpha = alpha),
                start = origin,
                end = origin + Offset(0f, length * ySign),
                strokeWidth = stroke,
                cap = StrokeCap.Square
            )
        }

        bracket(Offset(inset, top), 1f, 1f, Amber)
        bracket(Offset(right, top), -1f, 1f, Amber)
        bracket(Offset(inset, bottom), 1f, -1f, BorderDim)
        bracket(Offset(right, bottom), -1f, -1f, BorderDim)
    }
}
