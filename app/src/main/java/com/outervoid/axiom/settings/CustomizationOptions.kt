package com.outervoid.axiom.settings

import androidx.compose.ui.graphics.Color

enum class ClockFormat {
    SYSTEM,
    TWELVE_HOUR,
    TWENTY_FOUR_HOUR
}

data class AccentColorOption(
    val key: String,
    val displayName: String,
    val color: Color
)

object AccentColorOptions {
    const val DefaultKey = "AMBER"

    val All = listOf(
        AccentColorOption("AMBER", "Amber", Color(0xFFFFD000)),
        AccentColorOption("RED", "Red", Color(0xFFE53935)),
        AccentColorOption("BLUE", "Blue", Color(0xFF2196F3)),
        AccentColorOption("ORANGE", "Orange", Color(0xFFFF9800)),
        AccentColorOption("GREEN", "Green", Color(0xFF4CAF50)),
        AccentColorOption("PURPLE", "Purple", Color(0xFF9C27B0)),
        AccentColorOption("SKY_BLUE", "Sky Blue", Color(0xFF03A9F4)),
        AccentColorOption("CYAN", "Cyan", Color(0xFF00BCD4)),
        AccentColorOption("TEAL", "Teal", Color(0xFF009688)),
        AccentColorOption("LIME", "Lime", Color(0xFFCDDC39)),
        AccentColorOption("YELLOW", "Yellow", Color(0xFFFFEB3B)),
        AccentColorOption("PINK", "Pink", Color(0xFFE91E63)),
        AccentColorOption("DEEP_PURPLE", "Deep Purple", Color(0xFF673AB7)),
        AccentColorOption("INDIGO", "Indigo", Color(0xFF3F51B5)),
        AccentColorOption("DEEP_ORANGE", "Deep Orange", Color(0xFFFF5722)),
        AccentColorOption("MINT", "Mint", Color(0xFF00E676)),
        AccentColorOption("ELECTRIC_BLUE", "Electric Blue", Color(0xFF2979FF)),
        AccentColorOption("VIOLET", "Violet", Color(0xFF7C4DFF)),
        AccentColorOption("CRIMSON", "Crimson", Color(0xFFD50000)),
        AccentColorOption("WHITE", "White", Color(0xFFD8D8D8)),
        AccentColorOption("GRAPHITE", "Graphite", Color(0xFF777777))
    )

    fun byKey(key: String): AccentColorOption = All.firstOrNull { it.key == key } ?: All.first()
}

fun AxiomSettings.accentColor(): Color = AccentColorOptions.byKey(accentColorKey).color

fun AxiomSettings.hiddenAppKeys(): Set<String> = hiddenApps

fun appKey(packageName: String, activityName: String): String = "$packageName/$activityName"
