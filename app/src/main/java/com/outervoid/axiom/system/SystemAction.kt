package com.outervoid.axiom.system

enum class SystemAction(val label: String) {
    Wifi("Wi-Fi"),
    Bluetooth("Bluetooth"),
    Battery("Battery"),
    Data("Data"),
    DarkMode("Dark Mode"),
    Flashlight("Flashlight"),
    Location("Location"),
    Hotspot("Hotspot"),
    Nfc("NFC"),
    Sync("Sync"),
    Airplane("Airplane"),
    DoNotDisturb("Do Not Disturb"),
    Security("Security"),
    Storage("Storage"),
    Users("Users"),
    Developer("Developer");

    companion object {
        val PanelActions = entries.toList()
    }
}
