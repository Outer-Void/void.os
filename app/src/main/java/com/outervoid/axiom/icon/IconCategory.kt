package com.outervoid.axiom.icon

internal enum class IconCategory {
    Ai,
    Browser,
    Developer,
    Email,
    Files,
    Finance,
    Media,
    Messaging,
    Navigation,
    Productivity,
    Security,
    Shopping,
    System,
    Utility,
    Generic
}

internal fun classifyIcon(packageName: String, activityName: String? = null): IconCategory {
    val app = "$packageName $activityName".lowercase()
    return when {
        app.hasAny("openai", "chatgpt", "gemini", "claude", "perplexity", "copilot") -> IconCategory.Ai
        app.hasAny("browser", "chrome", "firefox", "duckduckgo", "edge", "opera", "brave") -> IconCategory.Browser
        app.hasAny("android.studio", "vscode", "visualstudio", "github", "gitlab", "termux", "terminal", "aide", "developer") -> IconCategory.Developer
        app.hasAny("gmail", "android.gm", "outlook", "protonmail", "email", "mail.", ".mail", "inbox") -> IconCategory.Email
        app.hasAny("filemanager", "file.manager", "documentsui", "files", "drive", "dropbox", "onedrive") -> IconCategory.Files
        app.hasAny("bank", "cash", "pay", "wallet", "finance", "coin", "stock", "paypal", "venmo") -> IconCategory.Finance
        app.hasAny("music", "video", "youtube", "netflix", "media", "player", "photo", "spotify", "twitch") -> IconCategory.Media
        app.hasAny("message", "messenger", "sms", "chat", "whatsapp", "telegram", "signal", "discord", "slack") -> IconCategory.Messaging
        app.hasAny("maps", "navigation", "waze", "google.earth", "citymapper", "transit") -> IconCategory.Navigation
        app.hasAny("note", "office", "reader", "task", "todo", "calendar", "notion", "document") -> IconCategory.Productivity
        app.hasAny("authenticator", "bitwarden", "1password", "proton.pass", "security", "antivirus", "defender", "keeper") -> IconCategory.Security
        app.hasAny("shop", "store", "amazon", "walmart", "target", "market", "ebay", "etsy") -> IconCategory.Shopping
        app.hasAny("settings", "phone", "camera", "clock", "contact", "system") -> IconCategory.System
        app.hasAny("calculator", "compass", "flashlight", "scanner", "qr", "convert", "utility") -> IconCategory.Utility
        else -> IconCategory.Generic
    }
}

private fun String.hasAny(vararg terms: String): Boolean = terms.any { contains(it) }
