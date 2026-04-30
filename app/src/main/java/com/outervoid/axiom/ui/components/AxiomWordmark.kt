package com.outervoid.axiom.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import com.outervoid.axiom.ui.theme.Amber
import com.outervoid.axiom.ui.theme.AxiomSectionLabelStyle
import com.outervoid.axiom.ui.theme.TextPrimary

@Composable
fun AxiomWordmark() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("AXI", color = TextPrimary, style = AxiomSectionLabelStyle)
        Text("O", color = Amber, style = AxiomSectionLabelStyle)
        Text("M", color = TextPrimary, style = AxiomSectionLabelStyle)
    }
}
