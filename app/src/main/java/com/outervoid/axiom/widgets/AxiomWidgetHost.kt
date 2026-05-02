package com.outervoid.axiom.widgets

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetProviderInfo
import android.content.Context

class AxiomWidgetHost(context: Context) : AppWidgetHost(context.applicationContext, HOST_ID) {
    fun createBoundView(context: Context, widgetId: Int, providerInfo: AppWidgetProviderInfo): AppWidgetHostView =
        createView(context, widgetId, providerInfo)

    companion object {
        const val HOST_ID = 4208
    }
}
