package com.outervoid.axiom.icon

import android.graphics.drawable.Drawable

interface IconProvider {
    fun iconFor(packageName: String, fallback: Drawable): Drawable
}
