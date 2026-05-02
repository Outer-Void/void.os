package com.outervoid.axiom.wallpaper

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.view.WindowManager

class AxiomWallpaperManager(private val context: Context) {
    fun setLockScreenWallpaper(): Result {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return Result.Restricted
        return try {
            WallpaperManager.getInstance(context).setBitmap(
                createWallpaper(),
                null,
                true,
                WallpaperManager.FLAG_LOCK
            )
            Result.Applied
        } catch (_: SecurityException) {
            Result.Restricted
        } catch (_: RuntimeException) {
            Result.Restricted
        }
    }

    private fun createWallpaper(): Bitmap {
        val (w, h) = screenSize()
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.rgb(11, 11, 11))
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.rgb(255, 208, 0)
        paint.strokeWidth = (h * 0.002f).coerceAtLeast(3f)
        val cx = w * 0.5f
        canvas.drawLine(cx, h * 0.30f, cx, h * 0.70f, paint)
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = (w * 0.085f).coerceIn(64f, 120f)
        paint.letterSpacing = 0.12f
        canvas.drawText("AXIOM", cx, h * 0.27f, paint)
        paint.color = Color.rgb(80, 80, 80)
        paint.textSize = (w * 0.024f).coerceIn(18f, 36f)
        paint.letterSpacing = 0.08f
        canvas.drawText("VOID SYSTEM", cx, h * 0.724f, paint)
        return bitmap
    }

    private fun screenSize(): Pair<Int, Int> {
        val wm = context.getSystemService(WindowManager::class.java)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val bounds = wm.currentWindowMetrics.bounds
            bounds.width().coerceAtLeast(1080) to bounds.height().coerceAtLeast(1920)
        } else {
            @Suppress("DEPRECATION")
            val dm = android.util.DisplayMetrics()
            @Suppress("DEPRECATION")
            wm.defaultDisplay.getRealMetrics(dm)
            dm.widthPixels.coerceAtLeast(1080) to dm.heightPixels.coerceAtLeast(1920)
        }
    }

    sealed interface Result {
        data object Applied : Result
        data object Restricted : Result
    }
}
