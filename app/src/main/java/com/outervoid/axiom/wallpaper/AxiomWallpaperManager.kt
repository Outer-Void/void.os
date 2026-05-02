package com.outervoid.axiom.wallpaper

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build

class AxiomWallpaperManager(private val context: Context) {
    fun setLockScreenWallpaper(): Result {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            return Result.Restricted
        }
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
        val bitmap = Bitmap.createBitmap(1080, 2400, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.rgb(11, 11, 11))
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.rgb(255, 208, 0)
        paint.strokeWidth = 5f
        canvas.drawLine(540f, 720f, 540f, 1680f, paint)
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = 92f
        paint.letterSpacing = 0.12f
        canvas.drawText("AXIOM", 540f, 650f, paint)
        paint.color = Color.rgb(80, 80, 80)
        paint.textSize = 26f
        paint.letterSpacing = 0.08f
        canvas.drawText("VOID SYSTEM", 540f, 1738f, paint)
        return bitmap
    }

    sealed interface Result {
        data object Applied : Result
        data object Restricted : Result
    }
}
