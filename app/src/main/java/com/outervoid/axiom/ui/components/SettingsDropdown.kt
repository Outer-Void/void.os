package com.outervoid.axiom.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.ui.theme.Border
import com.outervoid.axiom.ui.theme.Surface
import com.outervoid.axiom.ui.theme.SurfaceAlt
import com.outervoid.axiom.ui.theme.TextPrimary
import com.outervoid.axiom.ui.theme.TextSecondary

@Composable
fun <T> SettingsDropdown(
    title: String,
    selected: T,
    options: List<T>,
    optionLabel: (T) -> String,
    onSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier.fillMaxWidth()) {
        SettingsRow(
            title = title,
            subtitle = subtitle,
            value = optionLabel(selected),
            onClick = { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .widthIn(min = 260.dp)
                .background(Surface)
                .border(1.dp, Border, RoundedCornerShape(4.dp))
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            optionLabel(option),
                            color = if (option == selected) TextPrimary else TextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        expanded = false
                        onSelected(option)
                    },
                    modifier = Modifier.background(if (option == selected) SurfaceAlt else Surface)
                )
            }
        }
    }
}
