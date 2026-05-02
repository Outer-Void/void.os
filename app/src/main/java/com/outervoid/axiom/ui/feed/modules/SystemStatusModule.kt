package com.outervoid.axiom.ui.feed.modules

import android.os.Build
import androidx.compose.runtime.Composable
import com.outervoid.axiom.feed.FeedModuleConfig
import com.outervoid.axiom.settings.AxiomSettings

@Composable
fun SystemStatusModule(config: FeedModuleConfig, settings: AxiomSettings) {
    FeedModuleCard(config) {
        ModuleLine("Launcher", "SYSTEM ONLINE")
        ModuleLine("System Panel", if (settings.systemPanelEnabled) "ENABLED" else "DISABLED")
        ModuleLine("Icon System", "VOID.SYS / AXIOM")
        ModuleLine("Android", Build.VERSION.RELEASE ?: "UNKNOWN")
    }
}
