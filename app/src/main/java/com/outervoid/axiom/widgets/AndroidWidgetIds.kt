package com.outervoid.axiom.widgets

import android.appwidget.AppWidgetManager
import android.content.Context

class AndroidWidgetIds(context: Context, private val host: AxiomWidgetHost) : WidgetIds {
    private val manager = AppWidgetManager.getInstance(context.applicationContext)
    override fun allocate(): Int = host.allocateAppWidgetId()
    override fun owned(): Set<Int> = host.appWidgetIds.toSet()
    override fun provider(id: Int): String? = manager.getAppWidgetInfo(id)?.provider?.flattenToString()
    override fun delete(id: Int) = host.deleteAppWidgetId(id)
}
