package com.outervoid.axiom.feed

data class FeedModule(
    val type: FeedModuleType,
    val title: String = type.label,
    val category: FeedModuleCategory = type.category,
    val status: FeedModuleStatus = type.status
)

