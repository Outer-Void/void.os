package com.outervoid.axiom.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.ui.theme.Amber
import com.outervoid.axiom.ui.theme.Border
import com.outervoid.axiom.ui.theme.SurfaceAlt
import com.outervoid.axiom.ui.theme.TextDim

@Composable
fun SettingsToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = Amber
) {
    Box(
        modifier = modifier
            .size(width = 44.dp, height = 24.dp)
            .background(if (checked) accentColor.copy(alpha = 0.18f) else SurfaceAlt, RoundedCornerShape(12.dp))
            .border(1.dp, if (checked) accentColor.copy(alpha = 0.55f) else Border, RoundedCornerShape(12.dp))
            .clickable(role = Role.Switch) { onCheckedChange(!checked) },
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            Modifier
                .size(16.dp)
                .background(if (checked) accentColor.copy(alpha = 0.9f) else TextDim, RoundedCornerShape(8.dp))
        )
    }
}
