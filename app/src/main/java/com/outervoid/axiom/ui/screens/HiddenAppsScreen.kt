package com.outervoid.axiom.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.outervoid.axiom.ui.theme.TextSecondary

@Composable
fun HiddenAppsScreen(
    apps: List<AppInfo>,
    settings: AxiomSettings,
    onHiddenAppsChanged: (Set<String>) -> Unit
) {
    val accent = settings.accentColor()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 18.dp, vertical = 22.dp)
    ) {
        Column {
            Text("HIDDEN APPS", color = accent, style = MaterialTheme.typography.titleLarge)
            Text("Hide from app drawer only.", color = TextSecondary, style = MaterialTheme.typography.labelLarge)
            Spacer(
                Modifier
                    .padding(top = 12.dp, bottom = 18.dp)
                    .width(104.dp)
                    .height(1.dp)
                    .background(accent)
            )
            LazyColumn {
                items(apps, key = { appKey(it.packageName, it.activityName) }) { app ->
                    val key = appKey(app.packageName, app.activityName)
                    SettingsRow(
                        title = app.label,
                        subtitle = app.packageName,
                        trailing = {
                            SettingsToggle(
                                checked = key in settings.hiddenApps,
                                onCheckedChange = { checked ->
                                    onHiddenAppsChanged(if (checked) settings.hiddenApps + key else settings.hiddenApps - key)
                                },
                                accentColor = accent
                            )
                        }
                    )
                }
            }
        }
    }
}
