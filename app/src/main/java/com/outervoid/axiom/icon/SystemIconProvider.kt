package com.outervoid.axiom.icon

import android.graphics.drawable.Drawable

class SystemIconProvider : IconProvider {
    override fun iconFor(packageName: String, activityName: String?, fallback: Drawable): Drawable = fallback
}
