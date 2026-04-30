package com.outervoid.axiom.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.ui.components.SystemOnlineIndicator
import com.outervoid.axiom.ui.theme.AxiomAmber
import com.outervoid.axiom.ui.theme.AxiomBackground
import com.outervoid.axiom.ui.theme.AxiomTextPrimary

@Composable
fun AmbientScreen(date: String) {
    Column(Modifier.fillMaxSize().background(AxiomBackground).padding(24.dp)) {
        Text("A", color = AxiomTextPrimary.copy(alpha = 0.08f))
        Text("|", color = AxiomAmber)
        Text(date, color = AxiomTextPrimary)
        SystemOnlineIndicator(AxiomAmber, AxiomTextPrimary)
    }
}
