package com.outervoid.axiom.system

enum class SystemAction(val label: String) {
    PhoneSettings("Phone Settings"),
    Wifi("Wi-Fi"),
    Bluetooth("Bluetooth"),
    Battery("Battery"),
    MobileNetwork("Mobile Network"),
    DataUsage("Data Usage"),
    Hotspot("Hotspot"),
    Nfc("NFC"),
    Airplane("Airplane Mode"),
    DoNotDisturb("Do Not Disturb"),
    Sound("Sound"),
    Display("Display"),
    Wallpaper("Wallpaper"),
    Apps("Apps"),
    Notifications("Notifications"),
    Accessibility("Accessibility"),
    Security("Security"),
    Privacy("Privacy"),
    Flashlight("Flashlight"),
    Location("Location"),
    Storage("Storage"),
    Users("Users"),
    Accounts("Accounts"),
    Keyboard("Keyboard / Input"),
    DateTime("Date & Time"),
    Language("Language"),
    Vpn("VPN"),
    Developer("Developer Options"),
    DefaultApps("Default Apps"),
    HomeSettings("Home Settings"),
    SystemUpdate("System Update");

    companion object {
        val PanelActions = entries.toList()
    }
}
