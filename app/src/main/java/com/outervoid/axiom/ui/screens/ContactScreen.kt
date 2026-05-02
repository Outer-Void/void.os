package com.outervoid.axiom.ui.screens

import androidx.compose.foundation.background
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
import com.outervoid.axiom.settings.accentColor
import com.outervoid.axiom.ui.components.SettingsRow
import com.outervoid.axiom.ui.theme.Background
import com.outervoid.axiom.ui.theme.TextSecondary

@Composable
fun ContactScreen(
    settings: AxiomSettings,
    onOpenOuterVoid: () -> Unit,
    onOpenJustadude: () -> Unit,
    onEmail: () -> Unit
) {
    val accent = settings.accentColor()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = 18.dp, vertical = 22.dp)
    ) {
        Text("CONTACT", color = accent, style = MaterialTheme.typography.titleLarge)
        Text("Support & Feedback", color = TextSecondary, style = MaterialTheme.typography.labelLarge)
        Spacer(
            Modifier
                .padding(top = 12.dp, bottom = 18.dp)
                .width(104.dp)
                .height(1.dp)
                .background(accent)
        )
        SettingsRow("Email", value = "OPEN", subtitle = "theoutervoid@outlook.com", onClick = onEmail)
        SettingsRow("Outer-Void GitHub", value = "OPEN", onClick = onOpenOuterVoid)
        SettingsRow("Justadudeinspace GitHub", value = "OPEN", onClick = onOpenJustadude)
    }
}
