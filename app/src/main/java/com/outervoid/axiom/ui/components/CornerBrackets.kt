package com.outervoid.axiom.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.ui.theme.Amber
import com.outervoid.axiom.ui.theme.BorderDim

@Composable
fun CornerBrackets(modifier: Modifier = Modifier, insetDp: androidx.compose.ui.unit.Dp = 18.dp) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val inset = insetDp.toPx()
        val length = 30.dp.toPx()
        val stroke = 1.dp.toPx()
        val right = size.width - inset
        val bottom = size.height - inset

        fun bracket(origin: Offset, xSign: Float, ySign: Float, color: androidx.compose.ui.graphics.Color) {
            drawLine(
                color = color,
                start = origin,
                end = origin + Offset(length * xSign, 0f),
                strokeWidth = stroke,
                cap = StrokeCap.Square
            )
            drawLine(
                color = color,
                start = origin,
                end = origin + Offset(0f, length * ySign),
                strokeWidth = stroke,
                cap = StrokeCap.Square
            )
        }

        bracket(Offset(inset, inset), 1f, 1f, Amber)
        bracket(Offset(right, inset), -1f, 1f, Amber)
        bracket(Offset(inset, bottom), 1f, -1f, BorderDim)
        bracket(Offset(right, bottom), -1f, -1f, BorderDim)
    }
}
