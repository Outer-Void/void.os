package com.outervoid.axiom.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context

class WidgetPanelManager(context: Context) {
    private val appWidgetManager = AppWidgetManager.getInstance(context.applicationContext)

    fun installedProviders(): List<AppWidgetProviderInfo> =
        appWidgetManager.installedProviders.sortedBy { it.provider.flattenToShortString() }
}
