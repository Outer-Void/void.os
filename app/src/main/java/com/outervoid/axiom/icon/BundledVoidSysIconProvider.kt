package com.outervoid.axiom.icon

import android.content.Context
import android.graphics.drawable.Drawable
import com.outervoid.axiom.settings.IconPackOptions
import org.json.JSONObject
import java.util.Locale

class BundledVoidSysIconProvider(
    private val context: Context,
    private val selectedIconPackPackage: String,
    private val externalProvider: VoidSysIconProvider = VoidSysIconProvider(context, selectedIconPackPackage),
    private val fallbackProvider: IconProvider = AxiomFallbackIconProvider(context),
    private val systemProvider: IconProvider = SystemIconProvider()
) : IconProvider {
    private val iconMap: Map<String, String> by lazy { loadIconMap() }

    override fun iconFor(packageName: String, activityName: String?, fallback: Drawable): Drawable {
        if (selectedIconPackPackage == IconPackOptions.SystemPackage) {
            return systemProvider.iconFor(packageName, activityName, fallback)
        }

        if (selectedIconPackPackage == IconPackOptions.AxiomFallbackPackage) {
            return fallbackProvider.iconFor(packageName, activityName, fallback)
        }

        return findBundledIcon(packageName, activityName)
            ?: externalProvider.findIcon(packageName, activityName)
            ?: fallbackProvider.iconFor(packageName, activityName, fallback)
    }

    private fun findBundledIcon(packageName: String, activityName: String?): Drawable? {
        val drawableName = candidateKeys(packageName, activityName)
            .firstNotNullOfOrNull { key -> iconMap[key] }
            ?: return null

        val drawableId = context.resources.getIdentifier(drawableName, "drawable", context.packageName)
        if (drawableId == 0) return null
        return runCatching {
            context.resources.getDrawable(drawableId, context.theme)
        }.getOrNull()
    }

    private fun candidateKeys(packageName: String, activityName: String?): List<String> {
        val normalizedPackage = packageName.trim()
        val normalizedActivity = activityName?.trim()?.takeIf { it.isNotBlank() }
        val relativeActivity = normalizedActivity
            ?.takeIf { it.startsWith(normalizedPackage) }
            ?.removePrefix(normalizedPackage)
            ?.takeIf { it.startsWith(".") }

        return buildList {
            add(normalizedPackage)
            if (normalizedActivity != null) {
                add("$normalizedPackage/$normalizedActivity")
                add("ComponentInfo{$normalizedPackage/$normalizedActivity}")
            }
            if (relativeActivity != null) {
                add("$normalizedPackage/$relativeActivity")
                add("ComponentInfo{$normalizedPackage/$relativeActivity}")
            }
            add(normalizedPackage.lowercase(Locale.US))
        }.distinct()
    }

    private fun loadIconMap(): Map<String, String> {
        return runCatching {
            context.assets.open(ICON_MAP_ASSET).bufferedReader().use { reader ->
                parseIconMap(JSONObject(reader.readText()))
            }
        }.getOrDefault(emptyMap())
    }

    private fun parseIconMap(json: JSONObject): Map<String, String> {
        val mappings = linkedMapOf<String, String>()
        val keys = json.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val normalizedKey = key.trim()
            when (val value = json.opt(key)) {
                is String -> mappings[normalizedKey] = value
                is JSONObject -> value.optString("drawable")
                    .takeIf { it.isNotBlank() }
                    ?.let { mappings[normalizedKey] = it }
            }
        }
        return mappings
    }

    private companion object {
        const val ICON_MAP_ASSET = "voidsys_icon_map.json"
    }
}
