package com.outervoid.axiom.icon

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.util.Locale

class AxiomFallbackIconProvider(private val context: Context) : IconProvider {
    override fun iconFor(packageName: String, activityName: String?, fallback: Drawable): Drawable {
        val bitmap = Bitmap.createBitmap(SizePx, SizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        drawTile(canvas, paint)
        drawGlyph(canvas, paint, categoryFor(packageName), packageName)
        return BitmapDrawable(context.resources, bitmap)
    }

    private fun drawTile(canvas: Canvas, paint: Paint) {
        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(11, 11, 11)
        canvas.drawRoundRect(RectF(0f, 0f, SizePx.toFloat(), SizePx.toFloat()), 18f, 18f, paint)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.5f
        paint.color = Color.rgb(30, 30, 30)
        canvas.drawRoundRect(RectF(1f, 1f, SizePx - 1f, SizePx - 1f), 18f, 18f, paint)

        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(17, 17, 17)
        canvas.drawRoundRect(RectF(4f, 4f, 104f, 104f), 15f, 15f, paint)

        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.color = Color.rgb(34, 34, 34)
        paint.strokeWidth = 1.2f
        canvas.drawLine(10f, 18f, 98f, 18f, paint)
        paint.color = Color.rgb(51, 51, 51)
        paint.strokeWidth = 0.8f
        canvas.drawLine(19f, 24f, 89f, 24f, paint)
        paint.color = Color.rgb(11, 11, 11)
        paint.strokeWidth = 3f
        canvas.drawLine(14f, 92f, 94f, 92f, paint)
    }

    private fun drawGlyph(canvas: Canvas, paint: Paint, category: Category, packageName: String) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeJoin = Paint.Join.ROUND
        paint.color = Color.rgb(216, 216, 216)

        when (category) {
            Category.Messaging -> {
                canvas.drawRoundRect(RectF(25f, 33f, 83f, 69f), 8f, 8f, paint)
                canvas.drawLine(42f, 69f, 34f, 80f, paint)
                canvas.drawLine(42f, 69f, 54f, 69f, paint)
                drawAccentLine(canvas, paint, 38f, 49f, 69f, 49f)
            }
            Category.Media -> {
                canvas.drawRoundRect(RectF(28f, 32f, 80f, 76f), 8f, 8f, paint)
                val path = Path().apply {
                    moveTo(48f, 44f)
                    lineTo(66f, 54f)
                    lineTo(48f, 64f)
                    close()
                }
                drawAccentPath(canvas, paint, path)
            }
            Category.Browser -> {
                canvas.drawCircle(54f, 54f, 26f, paint)
                paint.color = Color.rgb(119, 119, 119)
                canvas.drawArc(RectF(27f, 36f, 81f, 76f), 196f, 120f, false, paint)
                drawAccentDot(canvas, paint, 73f, 43f)
            }
            Category.Finance -> {
                canvas.drawRoundRect(RectF(27f, 38f, 82f, 74f), 7f, 7f, paint)
                canvas.drawRoundRect(RectF(60f, 48f, 84f, 64f), 6f, 6f, paint)
                drawAccentDot(canvas, paint, 69f, 56f)
            }
            Category.Shopping -> {
                val path = Path().apply {
                    moveTo(32f, 42f)
                    lineTo(76f, 42f)
                    lineTo(72f, 80f)
                    lineTo(36f, 80f)
                    close()
                }
                canvas.drawPath(path, paint)
                canvas.drawArc(RectF(43f, 28f, 65f, 52f), 180f, 180f, false, paint)
                drawAccentLine(canvas, paint, 45f, 58f, 63f, 58f)
            }
            Category.Productivity -> {
                val path = Path().apply {
                    moveTo(36f, 26f)
                    lineTo(64f, 26f)
                    lineTo(76f, 38f)
                    lineTo(76f, 82f)
                    lineTo(36f, 82f)
                    close()
                }
                canvas.drawPath(path, paint)
                paint.color = Color.rgb(119, 119, 119)
                canvas.drawLine(64f, 26f, 64f, 39f, paint)
                canvas.drawLine(64f, 39f, 76f, 39f, paint)
                drawAccentLine(canvas, paint, 45f, 54f, 65f, 54f)
            }
            Category.System -> {
                canvas.drawLine(28f, 39f, 80f, 39f, paint)
                canvas.drawCircle(63f, 39f, 7f, paint)
                canvas.drawLine(28f, 56f, 80f, 56f, paint)
                drawAccentCircle(canvas, paint, 43f, 56f)
                paint.color = Color.rgb(216, 216, 216)
                canvas.drawLine(28f, 73f, 80f, 73f, paint)
                canvas.drawCircle(68f, 73f, 7f, paint)
            }
            Category.Generic -> drawInitial(canvas, paint, packageName)
        }
    }

    private fun drawInitial(canvas: Canvas, paint: Paint, packageName: String) {
        val token = packageName.split('.').lastOrNull { it.isNotBlank() }.orEmpty()
        val initial = token.firstOrNull()?.uppercaseChar()?.toString() ?: "A"
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2.6f
        paint.color = Color.rgb(216, 216, 216)
        canvas.drawRoundRect(RectF(34f, 30f, 74f, 78f), 8f, 8f, paint)
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 28f
        paint.color = Color.rgb(119, 119, 119)
        canvas.drawText(initial, 54f, 62f, paint)
        drawAccentLine(canvas, paint, 44f, 70f, 64f, 70f)
    }

    private fun drawAccentLine(canvas: Canvas, paint: Paint, x1: Float, y1: Float, x2: Float, y2: Float) {
        paint.style = Paint.Style.STROKE
        paint.color = Color.rgb(255, 208, 0)
        canvas.drawLine(x1, y1, x2, y2, paint)
    }

    private fun drawAccentPath(canvas: Canvas, paint: Paint, path: Path) {
        paint.style = Paint.Style.STROKE
        paint.color = Color.rgb(255, 208, 0)
        canvas.drawPath(path, paint)
    }

    private fun drawAccentDot(canvas: Canvas, paint: Paint, x: Float, y: Float) {
        paint.style = Paint.Style.FILL
        paint.color = Color.rgb(255, 208, 0)
        canvas.drawCircle(x, y, 4f, paint)
    }

    private fun drawAccentCircle(canvas: Canvas, paint: Paint, x: Float, y: Float) {
        paint.style = Paint.Style.STROKE
        paint.color = Color.rgb(255, 208, 0)
        canvas.drawCircle(x, y, 7f, paint)
    }

    private fun categoryFor(packageName: String): Category {
        val normalized = packageName.lowercase(Locale.US)
        return when {
            listOf("message", "messenger", "sms", "chat", "whatsapp", "telegram", "signal").any(normalized::contains) -> Category.Messaging
            listOf("music", "video", "youtube", "netflix", "media", "player", "photo").any(normalized::contains) -> Category.Media
            listOf("browser", "chrome", "firefox", "duckduckgo", "edge", "opera").any(normalized::contains) -> Category.Browser
            listOf("bank", "cash", "pay", "wallet", "finance", "coin", "stock").any(normalized::contains) -> Category.Finance
            listOf("shop", "store", "amazon", "walmart", "target", "market").any(normalized::contains) -> Category.Shopping
            listOf("doc", "note", "office", "reader", "code", "task", "todo").any(normalized::contains) -> Category.Productivity
            listOf("settings", "system", "phone", "camera", "clock", "calendar", "contact", "file").any(normalized::contains) -> Category.System
            else -> Category.Generic
        }
    }

    private enum class Category {
        Messaging,
        Media,
        Browser,
        Finance,
        Shopping,
        Productivity,
        System,
        Generic
    }

    private companion object {
        const val SizePx = 108
    }
}
