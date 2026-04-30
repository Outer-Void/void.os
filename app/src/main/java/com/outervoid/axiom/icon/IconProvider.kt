package com.outervoid.axiom.icon

import android.graphics.drawable.Drawable

interface IconProvider {
    fun iconFor(packageName: String, activityName: String?, fallback: Drawable): Drawable
}
