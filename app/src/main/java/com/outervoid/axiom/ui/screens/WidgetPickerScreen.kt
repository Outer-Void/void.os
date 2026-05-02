package com.outervoid.axiom.ui.screens

import android.appwidget.AppWidgetManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.settings.AxiomSettings
import com.outervoid.axiom.settings.accentColor
import com.outervoid.axiom.ui.components.SettingsRow
import com.outervoid.axiom.ui.theme.Background
import com.outervoid.axiom.ui.theme.TextSecondary

@Composable
fun WidgetPickerScreen(settings: AxiomSettings) {
    val context = LocalContext.current
    val accent = settings.accentColor()
    val widgets = remember {
        AppWidgetManager.getInstance(context).installedProviders.sortedBy {
            it.loadLabel(context.packageManager).toString().lowercase()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 18.dp, vertical = 22.dp)
    ) {
        Text("WIDGETS", color = accent, style = MaterialTheme.typography.titleLarge)
        Text("Some widgets require Android bind permission before hosting.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
        LazyColumn {
            items(widgets) { provider ->
                SettingsRow(
                    title = provider.loadLabel(context.packageManager).toString(),
                    subtitle = provider.provider.flattenToShortString(),
                    value = "SELECT"
                )
            }
        }
    }
}
