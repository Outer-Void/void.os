package com.outervoid.axiom.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.data.AppInfo
import com.outervoid.axiom.settings.AxiomSettings
import com.outervoid.axiom.settings.accentColor
import com.outervoid.axiom.settings.appKey
import com.outervoid.axiom.ui.components.SettingsRow
import com.outervoid.axiom.ui.components.SettingsToggle
import com.outervoid.axiom.ui.theme.Background

@Composable
fun FolderAppSelectionScreen(
    title: String,
    apps: List<AppInfo>,
    selectedKeys: Set<String>,
    settings: AxiomSettings,
    onChanged: (Set<String>) -> Unit
) {
    val accent = settings.accentColor()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 18.dp, vertical = 22.dp)
    ) {
        Text(title, color = accent, style = MaterialTheme.typography.titleLarge)
        LazyColumn {
            items(apps, key = { appKey(it.packageName, it.activityName) }) { app ->
                val key = appKey(app.packageName, app.activityName)
                SettingsRow(
                    title = app.label,
                    subtitle = app.packageName,
                    trailing = {
                        SettingsToggle(
                            checked = key in selectedKeys,
                            onCheckedChange = { checked -> onChanged(if (checked) selectedKeys + key else selectedKeys - key) },
                            accentColor = accent
                        )
                    }
                )
            }
        }
    }
}
