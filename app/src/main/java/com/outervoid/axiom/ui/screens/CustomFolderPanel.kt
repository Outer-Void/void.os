package com.outervoid.axiom.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.settings.AxiomSettings
import com.outervoid.axiom.ui.components.CornerBrackets
import com.outervoid.axiom.ui.theme.Amber
import com.outervoid.axiom.ui.theme.Background
import com.outervoid.axiom.ui.theme.TextPrimary
import com.outervoid.axiom.ui.theme.TextSecondary

@Composable
fun CustomFolderPanel(
    settings: AxiomSettings,
    side: String
) {
    val accent = if (settings.amberAccentEnabled) Amber else TextSecondary
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = if (settings.compactMode) 12.dp else 18.dp, vertical = if (settings.compactMode) 16.dp else 22.dp)
    ) {
        Column {
            Text("CUSTOM FOLDER", color = accent, style = MaterialTheme.typography.titleLarge)
            Text(side, color = TextSecondary, style = MaterialTheme.typography.labelLarge)
            Spacer(
                Modifier
                    .padding(top = 12.dp, bottom = 18.dp)
                    .width(104.dp)
                    .height(1.dp)
                    .background(accent)
            )
            Text("Add selected apps here.", color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
        }
        CornerBrackets(topPadding = 74.dp, bottomPadding = 36.dp, alpha = 0.58f)
    }
}
