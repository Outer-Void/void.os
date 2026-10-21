package com.outervoid.axiom.feed

import org.junit.Assert.assertEquals
import org.junit.Test

class FeedAvailabilityTest {
    @Test fun unimplementedDataIntegrationsArePlannedRatherThanPermissionGated() {
        listOf(FeedModuleType.Weather, FeedModuleType.NotificationsSummary,
            FeedModuleType.ScreenTimeToday, FeedModuleType.TopApps,
            FeedModuleType.LocationTravel, FeedModuleType.NotificationLoad).forEach {
            assertEquals(it.name, FeedModuleStatus.Planned, it.status)
        }
        assertEquals(FeedModuleStatus.Functional, FeedModuleType.Calendar.status)
    }
}
