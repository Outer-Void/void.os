package com.outervoid.axiom.settings

data class IconPackOption(
    val displayName: String,
    val packageName: String,
    val description: String
)

object IconPackOptions {
    const val VoidSysPackage = "com.outervoid.voidsys"
    const val AxiomFallbackPackage = "axiom_fallback"
    const val SystemPackage = "system"

    val VoidSys = IconPackOption(
        displayName = "VOID.SYS Native",
        packageName = VoidSysPackage,
        description = "Native AXIOM icons with controlled fallback"
    )

    val AxiomFallback = IconPackOption(
        displayName = "AXIOM Fallback",
        packageName = AxiomFallbackPackage,
        description = "Use generated AXIOM symbolic fallback icons"
    )

    val SystemDefault = IconPackOption(
        displayName = "System Icons",
        packageName = SystemPackage,
        description = "Use launcher icons from installed apps"
    )

    val All = listOf(VoidSys, AxiomFallback, SystemDefault)

    fun byPackage(packageName: String): IconPackOption =
        All.firstOrNull { it.packageName == packageName } ?: VoidSys
}
