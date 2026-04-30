package com.outervoid.axiom.icon

import android.graphics.drawable.Drawable

class SystemIconProvider : IconProvider {
    override fun iconFor(packageName: String, fallback: Drawable): Drawable = fallback
}
