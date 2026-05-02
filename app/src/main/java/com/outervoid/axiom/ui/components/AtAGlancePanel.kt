package com.outervoid.axiom.ui.components

import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.provider.Settings
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.outervoid.axiom.media.AxiomNotificationListenerService
import com.outervoid.axiom.ui.theme.Border
import com.outervoid.axiom.ui.theme.BorderAlt
import com.outervoid.axiom.ui.theme.BorderDim
import com.outervoid.axiom.ui.theme.Surface
import com.outervoid.axiom.ui.theme.SurfaceAlt
import com.outervoid.axiom.ui.theme.TextDim
import com.outervoid.axiom.ui.theme.TextPrimary
import com.outervoid.axiom.ui.theme.TextSecondary
import kotlinx.coroutines.delay

private enum class GlancePage {
    Clock,
    Media
}

data class AtAGlanceMediaState(
    val accessGranted: Boolean,
    val active: Boolean,
    val title: String?,
    val artist: String?,
    val appName: String?
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AtAGlancePanel(
    date: String,
    time: String,
    showSystemOnline: Boolean,
    accentColor: Color,
    compactMode: Boolean,
    onOpenClock: () -> Unit,
    onOpenCalendar: () -> Unit,
    onOpenNotificationAccess: () -> Unit
) {
    val context = LocalContext.current
    var page by remember { mutableStateOf(GlancePage.Clock) }
    var mediaState by remember { mutableStateOf(readMediaState(context)) }

    LaunchedEffect(Unit) {
        while (true) {
            mediaState = readMediaState(context)
            delay(2200)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(10.dp))
            .border(1.dp, BorderDim, RoundedCornerShape(10.dp))
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    when {
                        dragAmount < -18f -> page = GlancePage.Media
                        dragAmount > 18f -> page = GlancePage.Clock
                    }
                }
            }
            .padding(horizontal = if (compactMode) 12.dp else 14.dp, vertical = if (compactMode) 10.dp else 12.dp)
    ) {
        Box(Modifier.fillMaxWidth()) {
            Canvas(Modifier.matchParentSize()) {
                drawLine(
                    color = BorderAlt.copy(alpha = 0.42f),
                    start = Offset(size.width * 0.06f, 0f),
                    end = Offset(size.width * 0.42f, 0f),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = Border.copy(alpha = 0.38f),
                    start = Offset(size.width * 0.62f, size.height),
                    end = Offset(size.width * 0.96f, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            Column(Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("AT A GLANCE", color = TextDim, style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                        GlanceDot(active = page == GlancePage.Clock, accentColor = accentColor)
                        GlanceDot(active = page == GlancePage.Media, accentColor = accentColor)
                    }
                }
                Spacer(Modifier.height(if (compactMode) 6.dp else 8.dp))
                when (page) {
                    GlancePage.Clock -> ClockGlance(
                        date = date,
                        time = time,
                        showSystemOnline = showSystemOnline,
                        accentColor = accentColor,
                        onOpenClock = onOpenClock,
                        onOpenCalendar = onOpenCalendar
                    )
                    GlancePage.Media -> MediaGlance(
                        state = mediaState,
                        accentColor = accentColor,
                        onOpenNotificationAccess = onOpenNotificationAccess
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ClockGlance(
    date: String,
    time: String,
    showSystemOnline: Boolean,
    accentColor: Color,
    onOpenClock: () -> Unit,
    onOpenCalendar: () -> Unit
) {
    Text(
        date,
        color = TextSecondary,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.combinedClickable(onClick = onOpenCalendar)
    )
    Text(
        time,
        color = accentColor,
        style = MaterialTheme.typography.displayLarge,
        modifier = Modifier.combinedClickable(onClick = onOpenClock)
    )
    if (showSystemOnline) {
        SystemOnlineIndicator(accentColor = accentColor)
    }
}

@Composable
private fun MediaGlance(
    state: AtAGlanceMediaState,
    accentColor: Color,
    onOpenNotificationAccess: () -> Unit
) {
    if (!state.accessGranted) {
        Text("MUSIC ACCESS REQUIRED", color = accentColor, style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(6.dp))
        Text(
            "Enable notification access for AXIOM to read active Android media sessions.",
            color = TextSecondary,
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(Modifier.height(8.dp))
        TextButton(
            onClick = onOpenNotificationAccess,
            colors = ButtonDefaults.textButtonColors(contentColor = accentColor)
        ) {
            Text("OPEN SETUP")
        }
    } else if (state.active) {
        Text("NOW PLAYING", color = TextDim, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(6.dp))
        Text(
            state.title.orEmpty().ifBlank { "Unknown track" },
            color = TextPrimary,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            listOfNotNull(state.artist, state.appName).filter { it.isNotBlank() }.joinToString(" / ").ifBlank { "Active media session" },
            color = TextSecondary,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    } else {
        Text("NO ACTIVE MEDIA", color = TextPrimary, style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(6.dp))
        Text("Start music in a media app, then return to AXIOM.", color = TextSecondary, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun GlanceDot(active: Boolean, accentColor: Color) {
    Box(
        modifier = Modifier
            .size(if (active) 7.dp else 5.dp)
            .background(if (active) accentColor else SurfaceAlt, RoundedCornerShape(50))
            .border(1.dp, if (active) accentColor.copy(alpha = 0.5f) else BorderDim, RoundedCornerShape(50))
    )
}

private fun readMediaState(context: Context): AtAGlanceMediaState {
    val accessGranted = notificationAccessEnabled(context)
    if (!accessGranted) {
        return AtAGlanceMediaState(accessGranted = false, active = false, title = null, artist = null, appName = null)
    }
    return try {
        val manager = context.getSystemService(MediaSessionManager::class.java)
        val component = ComponentName(context, AxiomNotificationListenerService::class.java)
        val controller = manager.getActiveSessions(component)
            .firstOrNull { it.playbackState?.state == PlaybackState.STATE_PLAYING }
            ?: manager.getActiveSessions(component).firstOrNull()
        controller?.toGlanceState(context)
            ?: AtAGlanceMediaState(accessGranted = true, active = false, title = null, artist = null, appName = null)
    } catch (_: SecurityException) {
        AtAGlanceMediaState(accessGranted = false, active = false, title = null, artist = null, appName = null)
    } catch (_: RuntimeException) {
        AtAGlanceMediaState(accessGranted = true, active = false, title = null, artist = null, appName = null)
    }
}

private fun MediaController.toGlanceState(context: Context): AtAGlanceMediaState {
    val metadata = metadata
    val title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE)
    val artist = metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST)
        ?: metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)
    val appName = runCatching {
        val info = context.packageManager.getApplicationInfo(packageName, 0)
        context.packageManager.getApplicationLabel(info).toString()
    }.getOrNull()
    return AtAGlanceMediaState(
        accessGranted = true,
        active = playbackState?.state == PlaybackState.STATE_PLAYING,
        title = title,
        artist = artist,
        appName = appName
    )
}

private fun notificationAccessEnabled(context: Context): Boolean {
    val enabled = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners").orEmpty()
    return enabled.contains(context.packageName, ignoreCase = true)
}
