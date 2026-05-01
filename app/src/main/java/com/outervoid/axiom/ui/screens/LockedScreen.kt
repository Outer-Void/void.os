package com.outervoid.axiom.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.ui.components.CornerBrackets
import com.outervoid.axiom.ui.theme.Amber
import com.outervoid.axiom.ui.theme.Background
import com.outervoid.axiom.ui.theme.TextPrimary
import com.outervoid.axiom.ui.theme.TextSecondary

@Composable
fun LockedScreen(
    onCheckLicense: () -> Unit,
    onOpenPlayStore: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text("AXIOM LOCKED", color = Amber, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(14.dp))
            Text("Purchase required.", color = TextPrimary, style = MaterialTheme.typography.bodyLarge)
            Text(
                "This launcher requires a valid AXIOM license.",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(28.dp))
            TextButton(onClick = onCheckLicense) {
                Text("CHECK LICENSE", color = Amber, style = MaterialTheme.typography.labelLarge)
            }
            TextButton(onClick = onOpenPlayStore) {
                Text("OPEN PLAY STORE", color = TextSecondary, style = MaterialTheme.typography.labelLarge)
            }
        }
        CornerBrackets()
    }
}
