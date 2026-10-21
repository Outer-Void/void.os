package com.outervoid.axiom.icon

import org.junit.Assert.assertEquals
import org.junit.Test

class IconCategoryTest {
    @Test
    fun recognizesHighFrequencyAppCategories() {
        assertEquals(IconCategory.Ai, classifyIcon("com.openai.chatgpt"))
        assertEquals(IconCategory.Browser, classifyIcon("org.mozilla.firefox"))
        assertEquals(IconCategory.Developer, classifyIcon("com.termux"))
        assertEquals(IconCategory.Email, classifyIcon("com.google.android.gm"))
        assertEquals(IconCategory.Files, classifyIcon("com.google.android.documentsui"))
        assertEquals(IconCategory.Messaging, classifyIcon("org.telegram.messenger"))
        assertEquals(IconCategory.Navigation, classifyIcon("com.google.android.apps.maps"))
        assertEquals(IconCategory.Security, classifyIcon("com.bitwarden.authenticator"))
    }

    @Test
    fun fallsBackToDeterministicGenericCategory() {
        assertEquals(IconCategory.Generic, classifyIcon("com.example.unknown"))
        assertEquals(IconCategory.Generic, classifyIcon("com.example.unknown", "MainActivity"))
    }

    @Test
    fun technicalSuffixDoesNotEraseAppIdentity() {
        assertEquals("SI", iconMonogramFor("org.signal.mobile"))
        assertEquals("TE", iconMonogramFor("org.telegram.beta"))
        assertEquals("MA", iconMonogramFor("com.example.my_app"))
    }

    @Test
    fun mappedAppsReceiveStableIdentityMonograms() {
        assertEquals("GM", iconMonogramFor("google_messages"))
        assertEquals("AA", iconMonogramFor("adobe_acrobat"))
        assertEquals("UN", iconMonogramFor("com.example.unknown"))
    }
}
