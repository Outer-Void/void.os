package com.outervoid.axiom.icon

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.LruCache
import java.util.Locale

class AxiomFallbackIconProvider(private val context: Context) : IconProvider {
    override fun iconFor(packageName: String, activityName: String?, fallback: Drawable): Drawable {
        return iconForIdentity(packageName, activityName, identity = null)
    }

    internal fun iconForMappedDrawable(
        packageName: String,
        activityName: String?,
        drawableName: String
    ): Drawable = iconForIdentity(packageName, activityName, drawableName)

    private fun iconForIdentity(
        packageName: String,
        activityName: String?,
        identity: String?
    ): Drawable {
        val category = classifyIcon("$packageName $identity", activityName)
        val monogram = identity?.let(::monogramFor)
            ?: if (category == IconCategory.Generic) monogramFor(packageName) else ""
        val key = "${category.name}:$monogram:${identity != null}"
        val bitmap = iconCache.get(key) ?: renderIcon(category, monogram, identity != null).also { iconCache.put(key, it) }
        return BitmapDrawable(context.resources, bitmap)
    }

    private fun renderIcon(category: IconCategory, monogram: String, mapped: Boolean): Bitmap {
        val bitmap = Bitmap.createBitmap(SIZE, SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }
        drawTile(canvas, paint)
        if (mapped) {
            canvas.save()
            canvas.translate(54f, 34f)
            canvas.scale(0.42f, 0.42f)
            canvas.translate(-54f, -54f)
            drawGlyph(canvas, paint, category, "")
            canvas.restore()
            drawMonogram(canvas, paint, monogram, large = true)
        } else {
            drawGlyph(canvas, paint, category, monogram)
        }
        return bitmap
    }

    private fun drawTile(canvas: Canvas, paint: Paint) {
        paint.style = Paint.Style.FILL
        paint.color = COLOR_BLACK
        canvas.drawRoundRect(1f, 1f, 107f, 107f, 19f, 19f, paint)
        paint.color = COLOR_SURFACE
        canvas.drawRoundRect(5f, 5f, 103f, 103f, 16f, 16f, paint)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.5f
        paint.color = COLOR_FRAME
        canvas.drawRoundRect(2f, 2f, 106f, 106f, 18f, 18f, paint)
        paint.strokeWidth = 1f
        paint.color = COLOR_RECESS
        canvas.drawRoundRect(8f, 8f, 100f, 100f, 13f, 13f, paint)
        paint.style = Paint.Style.FILL
        paint.color = COLOR_HIGHLIGHT
        canvas.drawRect(18f, 12f, 72f, 13.5f, paint)
        canvas.drawRect(18f, 94.5f, 90f, 96f, paint)
        paint.color = COLOR_AMBER
        canvas.drawRect(82f, 12f, 90f, 13.5f, paint)
    }

    private fun drawGlyph(canvas: Canvas, paint: Paint, category: IconCategory, monogram: String) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3.2f
        paint.color = COLOR_WHITE
        when (category) {
            IconCategory.Ai -> drawAi(canvas, paint)
            IconCategory.Browser -> drawBrowser(canvas, paint)
            IconCategory.Developer -> drawDeveloper(canvas, paint)
            IconCategory.Email -> drawEmail(canvas, paint)
            IconCategory.Files -> drawFiles(canvas, paint)
            IconCategory.Finance -> drawFinance(canvas, paint)
            IconCategory.Media -> drawMedia(canvas, paint)
            IconCategory.Messaging -> drawMessaging(canvas, paint)
            IconCategory.Navigation -> drawNavigation(canvas, paint)
            IconCategory.Productivity -> drawProductivity(canvas, paint)
            IconCategory.Security -> drawSecurity(canvas, paint)
            IconCategory.Shopping -> drawShopping(canvas, paint)
            IconCategory.System -> drawSystem(canvas, paint)
            IconCategory.Utility -> drawUtility(canvas, paint)
            IconCategory.Generic -> drawMonogram(canvas, paint, monogram)
        }
    }

    private fun drawMessaging(canvas: Canvas, paint: Paint) {
        canvas.drawRoundRect(25f, 30f, 83f, 69f, 9f, 9f, paint)
        canvas.drawLine(42f, 69f, 33f, 80f, paint)
        accentLine(canvas, paint, 39f, 48f, 69f, 48f)
        accentLine(canvas, paint, 39f, 56f, 59f, 56f)
    }

    private fun drawMedia(canvas: Canvas, paint: Paint) {
        canvas.drawRoundRect(27f, 31f, 81f, 77f, 9f, 9f, paint)
        val play = Path().apply { moveTo(47f, 42f); lineTo(67f, 54f); lineTo(47f, 66f); close() }
        paint.color = COLOR_AMBER
        canvas.drawPath(play, paint)
        paint.color = COLOR_WHITE
        canvas.drawLine(35f, 84f, 73f, 84f, paint)
    }

    private fun drawBrowser(canvas: Canvas, paint: Paint) {
        canvas.drawCircle(54f, 54f, 27f, paint)
        canvas.drawOval(43f, 27f, 65f, 81f, paint)
        canvas.drawLine(28f, 46f, 80f, 46f, paint)
        canvas.drawLine(28f, 62f, 80f, 62f, paint)
        accentLine(canvas, paint, 62f, 34f, 74f, 43f)
    }

    private fun drawFinance(canvas: Canvas, paint: Paint) {
        canvas.drawRoundRect(26f, 36f, 82f, 72f, 7f, 7f, paint)
        canvas.drawLine(28f, 47f, 80f, 47f, paint)
        accentLine(canvas, paint, 38f, 62f, 53f, 62f)
        canvas.drawCircle(70f, 61f, 3f, paint)
    }

    private fun drawShopping(canvas: Canvas, paint: Paint) {
        val bag = Path().apply { moveTo(32f, 41f); lineTo(76f, 41f); lineTo(72f, 80f); lineTo(36f, 80f); close() }
        canvas.drawPath(bag, paint)
        canvas.drawArc(RectF(42f, 27f, 66f, 53f), 180f, 180f, false, paint)
        accentLine(canvas, paint, 44f, 59f, 64f, 59f)
    }

    private fun drawProductivity(canvas: Canvas, paint: Paint) {
        val page = Path().apply { moveTo(34f, 27f); lineTo(65f, 27f); lineTo(77f, 39f); lineTo(77f, 81f); lineTo(34f, 81f); close() }
        canvas.drawPath(page, paint)
        canvas.drawLine(65f, 27f, 65f, 39f, paint)
        canvas.drawLine(65f, 39f, 77f, 39f, paint)
        accentLine(canvas, paint, 44f, 54f, 67f, 54f)
        accentLine(canvas, paint, 44f, 64f, 61f, 64f)
    }

    private fun drawSystem(canvas: Canvas, paint: Paint) {
        canvas.drawLine(28f, 37f, 80f, 37f, paint)
        canvas.drawLine(28f, 54f, 80f, 54f, paint)
        canvas.drawLine(28f, 71f, 80f, 71f, paint)
        paint.color = COLOR_SURFACE
        canvas.drawCircle(64f, 37f, 5f, paint)
        canvas.drawCircle(43f, 54f, 5f, paint)
        canvas.drawCircle(68f, 71f, 5f, paint)
        paint.color = COLOR_AMBER
        canvas.drawCircle(64f, 37f, 4f, paint)
        canvas.drawCircle(43f, 54f, 4f, paint)
        canvas.drawCircle(68f, 71f, 4f, paint)
    }

    private fun drawEmail(canvas: Canvas, paint: Paint) {
        canvas.drawRoundRect(25f, 34f, 83f, 74f, 5f, 5f, paint)
        canvas.drawLine(27f, 37f, 54f, 57f, paint)
        canvas.drawLine(81f, 37f, 54f, 57f, paint)
        accentLine(canvas, paint, 31f, 68f, 43f, 59f)
        accentLine(canvas, paint, 77f, 68f, 65f, 59f)
    }

    private fun drawDeveloper(canvas: Canvas, paint: Paint) {
        canvas.drawRoundRect(25f, 31f, 83f, 77f, 6f, 6f, paint)
        canvas.drawLine(26f, 41f, 82f, 41f, paint)
        val bracket = Path().apply { moveTo(47f, 49f); lineTo(39f, 56f); lineTo(47f, 63f) }
        canvas.drawPath(bracket, paint)
        val slash = Path().apply { moveTo(61f, 48f); lineTo(54f, 64f) }
        canvas.drawPath(slash, paint)
        accentLine(canvas, paint, 65f, 63f, 72f, 63f)
    }

    private fun drawAi(canvas: Canvas, paint: Paint) {
        canvas.drawCircle(54f, 54f, 20f, paint)
        canvas.drawCircle(54f, 54f, 7f, paint)
        paint.color = COLOR_AMBER
        canvas.drawLine(54f, 23f, 54f, 38f, paint)
        canvas.drawLine(54f, 70f, 54f, 85f, paint)
        canvas.drawLine(23f, 54f, 38f, 54f, paint)
        canvas.drawLine(70f, 54f, 85f, 54f, paint)
        canvas.drawLine(33f, 33f, 43f, 43f, paint)
        canvas.drawLine(65f, 65f, 75f, 75f, paint)
        canvas.drawLine(75f, 33f, 65f, 43f, paint)
        canvas.drawLine(43f, 65f, 33f, 75f, paint)
    }

    private fun drawSecurity(canvas: Canvas, paint: Paint) {
        val shield = Path().apply {
            moveTo(54f, 25f); lineTo(79f, 35f); lineTo(75f, 61f)
            lineTo(54f, 81f); lineTo(33f, 61f); lineTo(29f, 35f); close()
        }
        canvas.drawPath(shield, paint)
        paint.color = COLOR_AMBER
        canvas.drawLine(42f, 53f, 51f, 62f, paint)
        canvas.drawLine(51f, 62f, 68f, 43f, paint)
    }

    private fun drawNavigation(canvas: Canvas, paint: Paint) {
        val pin = Path().apply {
            moveTo(54f, 81f); lineTo(34f, 56f); lineTo(32f, 49f)
            cubicTo(32f, 21f, 76f, 21f, 76f, 49f); lineTo(74f, 56f); close()
        }
        canvas.drawPath(pin, paint)
        canvas.drawCircle(54f, 48f, 8f, paint)
        paint.color = COLOR_AMBER
        canvas.drawCircle(54f, 48f, 3.5f, paint)
    }

    private fun drawFiles(canvas: Canvas, paint: Paint) {
        val back = Path().apply { moveTo(27f, 39f); lineTo(46f, 39f); lineTo(51f, 45f); lineTo(80f, 45f); lineTo(77f, 76f); lineTo(29f, 76f); close() }
        canvas.drawPath(back, paint)
        val front = Path().apply { moveTo(31f, 48f); lineTo(76f, 48f); lineTo(72f, 80f); lineTo(29f, 80f); close() }
        canvas.drawPath(front, paint)
        accentLine(canvas, paint, 42f, 61f, 63f, 61f)
    }

    private fun drawUtility(canvas: Canvas, paint: Paint) {
        canvas.drawCircle(54f, 54f, 21f, paint)
        canvas.drawCircle(54f, 54f, 8f, paint)
        for (index in 0 until 8) {
            val angle = Math.PI * index / 4.0
            val sx = 54f + kotlin.math.cos(angle).toFloat() * 25f
            val sy = 54f + kotlin.math.sin(angle).toFloat() * 25f
            val ex = 54f + kotlin.math.cos(angle).toFloat() * 31f
            val ey = 54f + kotlin.math.sin(angle).toFloat() * 31f
            canvas.drawLine(sx, sy, ex, ey, paint)
        }
        paint.color = COLOR_AMBER
        canvas.drawCircle(54f, 54f, 3f, paint)
    }

    private fun drawMonogram(canvas: Canvas, paint: Paint, monogram: String, large: Boolean = false) {
        if (!large) {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 2.2f
            paint.color = COLOR_WHITE
            canvas.drawRoundRect(29f, 27f, 79f, 81f, 8f, 8f, paint)
        }
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
        paint.textSize = if (large) {
            if (monogram.length > 2) 26f else 36f
        } else if (monogram.length > 1) 24f else 29f
        paint.color = if (large) COLOR_AMBER else COLOR_WHITE
        canvas.drawText(monogram, 54f, if (large) 76f else 63f, paint)
        accentLine(canvas, paint, if (large) 39f else 43f, if (large) 84f else 72f, if (large) 69f else 65f, if (large) 84f else 72f)
    }

    private fun accentLine(canvas: Canvas, paint: Paint, x1: Float, y1: Float, x2: Float, y2: Float) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2.2f
        paint.color = COLOR_AMBER
        canvas.drawLine(x1, y1, x2, y2, paint)
    }

    private fun monogramFor(packageName: String): String {
        return iconMonogramFor(packageName)
    }

    companion object {
        private const val SIZE = 108
        private const val COLOR_BLACK = 0xFF0B0B0B.toInt()
        private const val COLOR_SURFACE = 0xFF111111.toInt()
        private const val COLOR_FRAME = 0xFF303030.toInt()
        private const val COLOR_RECESS = 0xFF1B1B1B.toInt()
        private const val COLOR_HIGHLIGHT = 0xFF535353.toInt()
        private const val COLOR_WHITE = 0xFFD8D8D8.toInt()
        private const val COLOR_AMBER = 0xFFFFD000.toInt()
        private val iconCache = object : LruCache<String, Bitmap>(2 * 1024) {
            override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount / 1024
        }
    }
}

internal fun iconMonogramFor(identity: String): String {
    val source = identity.lowercase(Locale.US).substringAfterLast('.')
    val tokens = source.split(Regex("[^a-z0-9]+"))
        .filter { it.isNotBlank() }
    val letters = if (tokens.size > 1) tokens.mapNotNull { it.firstOrNull() }.take(2).joinToString("")
    else tokens.lastOrNull().orEmpty().filter { it.isLetterOrDigit() }.take(2)
    return letters.uppercase(Locale.US).ifBlank { "A" }
}
