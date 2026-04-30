package com.outervoid.axiom.settings

data class IconPackOption(
    val displayName: String,
    val packageName: String,
    val description: String
)

object IconPackOptions {
    const val VoidSysPackage = "com.outervoid.voidsys"
    const val SystemPackage = "system"
    const val InstalledLaterPackage = "installed_icon_pack"

    val VoidSys = IconPackOption(
        displayName = "VOID.SYS",
        packageName = VoidSysPackage,
        description = "Intended icon system for AXIOM"
    )

    val SystemDefault = IconPackOption(
        displayName = "System Default",
        packageName = SystemPackage,
        description = "Use launcher icons from installed apps"
    )

    val InstalledLater = IconPackOption(
        displayName = "Installed Icon Pack",
        packageName = InstalledLaterPackage,
        description = "Reserved for expanded icon pack parsing"
    )

    val All = listOf(VoidSys, SystemDefault, InstalledLater)

    fun byPackage(packageName: String): IconPackOption =
        All.firstOrNull { it.packageName == packageName } ?: VoidSys
}
