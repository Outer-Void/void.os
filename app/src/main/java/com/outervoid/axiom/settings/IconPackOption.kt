package com.outervoid.axiom.settings

data class IconPackOption(
    val displayName: String,
    val packageName: String,
    val description: String
)

object IconPackOptions {
    const val VoidSysPackage = "com.outervoid.voidsys"
    const val SystemPackage = "system"

    val VoidSys = IconPackOption(
        displayName = "VOID.SYS",
        packageName = VoidSysPackage,
        description = "Native AXIOM icon system"
    )

    val SystemDefault = IconPackOption(
        displayName = "System Default",
        packageName = SystemPackage,
        description = "Use launcher icons from installed apps"
    )

    val All = listOf(VoidSys, SystemDefault)

    fun byPackage(packageName: String): IconPackOption =
        All.firstOrNull { it.packageName == packageName } ?: VoidSys
}
