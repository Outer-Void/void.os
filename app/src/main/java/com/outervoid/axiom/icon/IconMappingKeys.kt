package com.outervoid.axiom.icon

import java.util.Locale

/** Activity overrides win over package defaults, for both supported map contracts. */
internal object IconMappingKeys {
    fun candidates(packageName: String, activityName: String?): List<String> {
        val pkg = packageName.trim()
        val activity = activityName?.trim()?.takeIf { it.isNotEmpty() }?.let {
            if (it.startsWith('.')) pkg + it else it
        }
        return buildList {
            if (activity != null) {
                add("$pkg/$activity")
                add("ComponentInfo{$pkg/$activity}")
                if (activity.startsWith("$pkg.")) {
                    val relative = activity.removePrefix(pkg)
                    add("$pkg/$relative")
                    add("ComponentInfo{$pkg/$relative}")
                }
            }
            add(pkg)
            add(pkg.lowercase(Locale.US))
        }.distinct()
    }
}
