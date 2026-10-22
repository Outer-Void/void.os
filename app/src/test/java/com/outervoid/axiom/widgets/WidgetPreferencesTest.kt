package com.outervoid.axiom.widgets

import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.outervoid.axiom.settings.SettingsKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import java.nio.file.Files

class WidgetPreferencesTest {
    @Test fun malformedRecordsAreFlaggedInsteadOfBeingSilentlyDeleted() {
        for (records in listOf(setOf("1:10", "1:11"), setOf("1:10", "2:10"), setOf("bad"), setOf("1:-1"))) {
            assertTrue(widgetSnapshot(preferencesOf(SettingsKeys.PanelWidgets to records)).malformed)
        }
        assertTrue(widgetSnapshot(preferencesOf(stringPreferencesKey("panel_widgets") to "wrong type")).malformed)
        assertTrue(widgetSnapshot(preferencesOf(PendingWidgetKey to "broken")).malformed)
    }
    @Test fun validLegacyAssignmentsRemainReadable() {
        val state = widgetSnapshot(preferencesOf(SettingsKeys.PanelWidgets to setOf("1:10", "4:12")))
        assertFalse(state.malformed); assertEquals(mapOf(1 to 10, 4 to 12), state.panels)
    }
    @Test fun diskBackedPendingAndCommittedStateSurviveStoreReconstruction() = runBlocking {
        val dir = Files.createTempDirectory("widget-store-test").toFile()
        val file = dir.resolve("widgets.preferences_pb")
        val firstJob = SupervisorJob()
        val secondJob = SupervisorJob()
        try {
            val first = PreferencesWidgetStore(PreferenceDataStoreFactory.create(scope = CoroutineScope(firstJob + Dispatchers.IO), produceFile = { file }))
            val op = WidgetOperation("operation", 1, 12, "app/.Widget", WidgetStage.Configuring)
            first.update { WidgetSnapshot(mapOf(1 to 10), op) }
            firstJob.cancelAndJoin()
            val restored = PreferencesWidgetStore(PreferenceDataStoreFactory.create(scope = CoroutineScope(secondJob + Dispatchers.IO), produceFile = { file }))
            assertEquals(op, restored.read().pending)
            assertEquals(mapOf(1 to 10), restored.read().panels)
            restored.update { it.copy(panels = mapOf(1 to 12), pending = null, garbage = setOf(10)) }
            val committed = restored.read()
            assertNull(committed.pending); assertEquals(mapOf(1 to 12), committed.panels); assertEquals(setOf(10), committed.garbage)
        } finally {
            firstJob.cancelAndJoin(); secondJob.cancelAndJoin(); dir.deleteRecursively()
        }
    }
}
