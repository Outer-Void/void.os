package com.outervoid.axiom.feed

object FeedModuleRegistry {
    val allModules: List<FeedModule> = FeedModuleType.entries.map { FeedModule(it) }

    fun defaultModules(): List<FeedModuleConfig> = listOf(
        FeedModuleType.SystemStatus,
        FeedModuleType.Weather,
        FeedModuleType.Calendar,
        FeedModuleType.FocusPanel,
        FeedModuleType.QuickActions
    ).mapIndexed { index, type ->
        FeedModuleConfig(id = type.name, type = type, position = index)
    }

    fun minimalModules(): List<FeedModuleConfig> = listOf(
        FeedModuleType.MinimalClock,
        FeedModuleType.SystemDotIndicator,
        FeedModuleType.FocusOnlyPanel
    ).mapIndexed { index, type ->
        FeedModuleConfig(id = type.name, type = type, position = index)
    }
}
