package com.outervoid.axiom.widgets

internal object WidgetOperationPolicy {
    fun abandonedWidgetId(widgetId: Int?, operationAccepted: Boolean): Int? =
        widgetId?.takeUnless { operationAccepted }
}
