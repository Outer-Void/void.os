package com.outervoid.axiom.icon

/** Inventory-scoped lookup cache. Recreate with the provider after package or pack changes. */
internal class IconResourceIds(private val lookup: (String) -> Int) {
    private val ids = HashMap<String, Int>()
    fun resolve(name: String): Int = ids.getOrPut(name) { lookup(name) }
}
