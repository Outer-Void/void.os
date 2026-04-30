package com.outervoid.axiom.icon

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.outervoid.axiom.settings.IconPackOptions
import org.json.JSONObject

class VoidSysIconProvider(
    private val context: Context,
    private val selectedIconPackPackage: String,
    private val fallbackProvider: IconProvider = SystemIconProvider()
) : IconProvider {
    private val packageManager = context.packageManager
    private val voidSysContext: Context? by lazy { loadVoidSysContext() }
    private val iconMap: Map<String, String> by lazy { loadIconMap(voidSysContext) }

    override fun iconFor(packageName: String, activityName: String?, fallback: Drawable): Drawable {
        if (selectedIconPackPackage != IconPackOptions.VoidSysPackage) {
            return fallbackProvider.iconFor(packageName, activityName, fallback)
        }

        val remoteContext = voidSysContext ?: return fallbackProvider.iconFor(packageName, activityName, fallback)
        val drawableName = resolveDrawableName(packageName, activityName)
            ?: return fallbackProvider.iconFor(packageName, activityName, fallback)

        return loadDrawable(remoteContext, drawableName)
            ?: fallbackProvider.iconFor(packageName, activityName, fallback)
    }

    private fun resolveDrawableName(packageName: String, activityName: String?): String? {
        val componentKey = if (activityName != null) "$packageName/$activityName" else null
        return componentKey?.let { iconMap[it] } ?: iconMap[packageName]
    }

    private fun loadVoidSysContext(): Context? = try {
        packageManager.getPackageInfo(IconPackOptions.VoidSysPackage, 0)
        context.createPackageContext(IconPackOptions.VoidSysPackage, Context.CONTEXT_IGNORE_SECURITY)
    } catch (_: PackageManager.NameNotFoundException) {
        null
    } catch (_: RuntimeException) {
        null
    }

    private fun loadIconMap(remoteContext: Context?): Map<String, String> {
        if (remoteContext == null) return emptyMap()
        return runCatching {
            remoteContext.assets.open(ICON_MAP_ASSET).bufferedReader().use { reader ->
                parseIconMap(JSONObject(reader.readText()))
            }
        }.getOrDefault(emptyMap())
    }

    private fun parseIconMap(json: JSONObject): Map<String, String> {
        val mappings = linkedMapOf<String, String>()
        val keys = json.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            when (val value = json.opt(key)) {
                is String -> mappings[key] = value
                is JSONObject -> value.optString("drawable")
                    .takeIf { it.isNotBlank() }
                    ?.let { mappings[key] = it }
            }
        }
        return mappings
    }

    private fun loadDrawable(remoteContext: Context, drawableName: String): Drawable? {
        val drawableId = remoteContext.resources.getIdentifier(
            drawableName,
            "drawable",
            IconPackOptions.VoidSysPackage
        )
        if (drawableId == 0) return null
        return runCatching {
            remoteContext.resources.getDrawable(drawableId, remoteContext.theme)
        }.getOrNull()
    }

    private companion object {
        const val ICON_MAP_ASSET = "icon_map.json"
    }
}
