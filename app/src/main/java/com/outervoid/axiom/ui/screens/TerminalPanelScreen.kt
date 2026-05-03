package com.outervoid.axiom.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.outervoid.axiom.security.PermissionManager
import com.outervoid.axiom.settings.AxiomSettings
import com.outervoid.axiom.settings.accentColor
import com.outervoid.axiom.terminal.TerminalSession
import com.outervoid.axiom.ui.theme.Background
import com.outervoid.axiom.ui.theme.Border
import com.outervoid.axiom.ui.theme.Surface
import com.outervoid.axiom.ui.theme.TextPrimary
import com.outervoid.axiom.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun TerminalPanelScreen(
    settings: AxiomSettings,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val accent = settings.accentColor()
    val session = remember { TerminalSession() }
    val lines by session.lines.collectAsState()
    val running by session.running.collectAsState()
    val startError by session.startError.collectAsState()
    val listState = rememberLazyListState()
    var input by remember { mutableStateOf("") }
    val permManager = remember(context) { PermissionManager(context) }
    val termuxInstalled = remember { permManager.isTermuxInstalled() }

    DisposableEffect(Unit) {
        session.start()
        onDispose { session.dispose() }
    }

    LaunchedEffect(lines.size) {
        if (lines.isNotEmpty()) listState.animateScrollToItem(lines.lastIndex)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("AXIOM TERMINAL", color = accent, style = MaterialTheme.typography.titleMedium)
                    Text(
                        if (running) "SESSION ACTIVE" else "SESSION INACTIVE",
                        color = if (running) accent else TextSecondary,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                Row {
                    if (!running && termuxInstalled && startError != null) {
                        TextButton(onClick = { launchTermux(context) }) {
                            Text("TERMUX", color = accent, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    TextButton(onClick = { session.clear() }) {
                        Text("CLEAR", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                    }
                    TextButton(onClick = { session.kill(); onClose() }) {
                        Text("CLOSE", color = TextSecondary, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            Spacer(
                Modifier
                    .padding(top = 8.dp, bottom = 10.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(accent)
            )

            if (startError != null && !running) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Surface, RoundedCornerShape(8.dp))
                        .border(1.dp, Border, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text("SHELL SESSION UNAVAILABLE", color = accent, style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(6.dp))
                    Text(startError ?: "", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    if (termuxInstalled) {
                        Spacer(Modifier.height(8.dp))
                        Text("Termux is installed. Tap TERMUX to open a full terminal session.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    } else {
                        Spacer(Modifier.height(8.dp))
                        Text("Install Termux from F-Droid or GitHub for a full terminal. AXIOM will bridge to it automatically.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Surface, RoundedCornerShape(8.dp))
                    .border(1.dp, Border, RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                items(lines) { line ->
                    Text(
                        text = line,
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = if (line.startsWith("$")) accent
                            else if (line.startsWith("AXIOM") || line.startsWith("SESSION") || line.startsWith("---")) accent.copy(alpha = 0.7f)
                            else TextPrimary
                        )
                    )
                }
            }

            if (running) {
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Surface, RoundedCornerShape(8.dp))
                        .border(1.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("$ ", color = accent, style = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp))
                    BasicTextField(
                        value = input,
                        onValueChange = { input = it },
                        textStyle = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp,
                            color = TextPrimary
                        ),
                        cursorBrush = SolidColor(accent),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (input.isNotBlank()) {
                                    session.send(input.trim())
                                    input = ""
                                }
                            }
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    TextButton(
                        onClick = {
                            if (input.isNotBlank()) {
                                session.send(input.trim())
                                input = ""
                            }
                        }
                    ) {
                        Text("RUN", color = accent, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

private fun launchTermux(context: Context) {
    try {
        val intent = context.packageManager.getLaunchIntentForPackage("com.termux")
        if (intent != null) {
            context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    } catch (_: Exception) {}
}
