package com.outervoid.axiom.ui.iconshape

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
fun iconShapeMask(iconShape: IconShape): Shape = when (iconShape) {
    IconShape.SYSTEM,
    IconShape.ROUNDED_SQUARE -> RoundedCornerShape(15.dp)
    IconShape.CIRCLE -> CircleShape
    IconShape.CYLINDER -> RoundedCornerShape(percent = 50)
    IconShape.EGG -> GenericShape { size, _ ->
        moveTo(size.width * 0.5f, 0f)
        cubicTo(size.width, 0f, size.width, size.height * 0.72f, size.width * 0.5f, size.height)
        cubicTo(0f, size.height * 0.72f, 0f, 0f, size.width * 0.5f, 0f)
        close()
    }
    IconShape.DIAMOND -> GenericShape { size, _ ->
        moveTo(size.width * 0.5f, 0f)
        lineTo(size.width, size.height * 0.5f)
        lineTo(size.width * 0.5f, size.height)
        lineTo(0f, size.height * 0.5f)
        close()
    }
    IconShape.IOS -> RoundedCornerShape(18.dp)
    IconShape.OCTAGON -> polygon(8)
    IconShape.ONE_UI -> RoundedCornerShape(22.dp)
    IconShape.SHARP_SQUARE,
    IconShape.SQUARE -> RoundedCornerShape(0.dp)
    IconShape.SQUIRCLE -> RoundedCornerShape(28.dp)
    IconShape.TEARDROP -> GenericShape { size, _ ->
        moveTo(size.width * 0.5f, 0f)
        cubicTo(size.width, 0f, size.width, size.height * 0.45f, size.width * 0.68f, size.height * 0.74f)
        lineTo(size.width * 0.5f, size.height)
        lineTo(size.width * 0.32f, size.height * 0.74f)
        cubicTo(0f, size.height * 0.45f, 0f, 0f, size.width * 0.5f, 0f)
        close()
    }
}

private fun polygon(sides: Int): Shape = GenericShape { size: Size, _ ->
    val radius = minOf(size.width, size.height) * 0.5f
    val cx = size.width * 0.5f
    val cy = size.height * 0.5f
    repeat(sides) { index ->
        val angle = (-90.0 + 360.0 * index / sides).toFloat()
        val radians = Math.toRadians(angle.toDouble())
        val x = cx + kotlin.math.cos(radians).toFloat() * radius
        val y = cy + kotlin.math.sin(radians).toFloat() * radius
        if (index == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}
