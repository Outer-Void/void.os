package com.outervoid.axiom.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.ui.theme.Amber
import com.outervoid.axiom.ui.theme.TextPrimary

@Composable
fun SystemOnlineIndicator(modifier: Modifier = Modifier) {
    val pulse by rememberInfiniteTransition(label = "system-online")
        .animateFloat(
            initialValue = 0.35f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
            label = "online-dot-alpha"
        )

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Spacer(
            Modifier
                .size(8.dp)
                .background(Amber.copy(alpha = pulse), CircleShape)
        )
        Spacer(Modifier.size(9.dp))
        Text("SYSTEM ONLINE.", color = TextPrimary, style = MaterialTheme.typography.labelLarge)
    }
}
