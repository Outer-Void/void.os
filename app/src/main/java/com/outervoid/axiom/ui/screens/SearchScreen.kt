package com.outervoid.axiom.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.data.AppInfo
import com.outervoid.axiom.settings.AxiomSettings
import com.outervoid.axiom.ui.components.AppIconCell
import com.outervoid.axiom.ui.components.CornerBrackets
import com.outervoid.axiom.ui.theme.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    query: String,
    apps: List<AppInfo>,
    settings: AxiomSettings,
    onQueryChange: (String) -> Unit,
    onLaunch: (AppInfo) -> Unit,
    onClose: () -> Unit
) {
    val focusRequester = androidx.compose.runtime.remember { FocusRequester() }
    val haptics = LocalHapticFeedback.current
    val accent = if (settings.amberAccentEnabled) Amber else TextSecondary
    fun haptic() {
        if (settings.hapticFeedbackEnabled) haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }
    fun launchSingleResult(): Boolean {
        val only = apps.singleOrNull() ?: return false
        haptic()
        onLaunch(only)
        return true
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(horizontal = if (settings.compactMode) 12.dp else 18.dp, vertical = if (settings.compactMode) 16.dp else 22.dp)
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount > 28f) {
                        haptic()
                        onClose()
                    }
                }
            }
    ) {
        Column(Modifier.fillMaxSize()) {
            Text("SEARCH APPS •", color = accent, style = MaterialTheme.typography.titleLarge)
            Spacer(
                Modifier
                    .padding(top = 12.dp, bottom = 18.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(accent)
            )
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onPreviewKeyEvent { event ->
                        event.type == KeyEventType.KeyUp && event.key == Key.Enter && launchSingleResult()
                    },
                textStyle = MaterialTheme.typography.headlineLarge.copy(color = TextPrimary),
                cursorBrush = SolidColor(accent),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { launchSingleResult() }),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        Text("type to search_", color = TextDim, style = MaterialTheme.typography.headlineLarge)
                    }
                    innerTextField()
                }
            )
            Spacer(Modifier.height(if (settings.compactMode) 16.dp else 24.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(settings.gridColumns),
                horizontalArrangement = Arrangement.spacedBy(if (settings.compactMode) 6.dp else 10.dp),
                verticalArrangement = Arrangement.spacedBy(if (settings.compactMode) 8.dp else 12.dp)
            ) {
                items(apps, key = { "${it.packageName}/${it.activityName}" }) { app ->
                    AppIconCell(
                        app = app,
                        onClick = { haptic(); onLaunch(app) },
                        onLongClick = {},
                        showLabel = settings.showAppLabels,
                        iconScale = settings.iconScale,
                        compactMode = settings.compactMode
                    )
                }
            }
        }
        CornerBrackets()
    }
}
