package com.outervoid.axiom.feed

data class FeedModuleConfig(
    val id: String,
    val type: FeedModuleType,
    val enabled: Boolean = true,
    val collapsed: Boolean = false,
    val position: Int = 0,
    val size: FeedModuleSize = FeedModuleSize.MEDIUM,
    val titleOverride: String? = null,
    val settings: Map<String, String> = emptyMap()
)

data class FeedState(
    val enabled: Boolean = true,
    val minimalMode: Boolean = false,
    val modules: List<FeedModuleConfig> = FeedModuleRegistry.defaultModules(),
    val priorities: List<String> = listOf("Stabilize AXIOM", "Test device flows"),
    val tasks: List<String> = listOf("Review widgets", "Verify icon style"),
    val nextAction: String = "Run device test pass",
    val notes: List<String> = listOf("AXIOM Feed is a user-built control layer."),
    val appShortcutKeys: Set<String> = emptySet(),
    val quickActions: List<String> = listOf("Wifi", "Bluetooth", "PhoneSettings", "SystemPanelShortcut")
) {
    val activeModules: List<FeedModuleConfig>
        get() = modules.filter { it.enabled }.sortedBy { it.position }
}
