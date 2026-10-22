package com.outervoid.axiom.terminal

import org.junit.Assert.*
import org.junit.Test
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class TerminalSessionTest {
    private fun await(condition: () -> Boolean) {
        val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5)
        while (!condition() && System.nanoTime() < deadline) Thread.sleep(5)
        assertTrue("Timed out waiting for shell state", condition())
    }

    @Test
    fun shellExitAndRestartKeepOutputAndReleaseProcess() {
        val session = TerminalSession { ProcessBuilder("sh").redirectErrorStream(true).start() }
        try {
            session.start()
            await { session.running.value }
            session.send("printf 'first\\n'; exit")
            await { !session.running.value }
            assertTrue(session.lines.value.contains("first"))
            session.start()
            await { session.running.value }
            session.send("printf 'second\\n'")
            await { session.lines.value.contains("second") }
            session.kill()
            assertFalse(session.running.value)
        } finally { session.dispose() }
    }

    @Test
    fun disposedSessionRejectsPendingStartWithoutBlockingCaller() {
        val entered = CountDownLatch(1)
        val proceed = CountDownLatch(1)
        val created = CountDownLatch(1)
        var shell: Process? = null
        val session = TerminalSession {
            entered.countDown()
            check(proceed.await(5, TimeUnit.SECONDS))
            ProcessBuilder("sh").start().also { shell = it; created.countDown() }
        }
        try {
            session.start()
            assertTrue(entered.await(5, TimeUnit.SECONDS))
            session.dispose()
            proceed.countDown()
            assertTrue(created.await(5, TimeUnit.SECONDS))
            await { shell?.isAlive == false }
            assertFalse(session.running.value)
            session.start()
            assertFalse(session.running.value)
        } finally { proceed.countDown(); session.dispose(); shell?.destroy() }
    }

    @Test
    fun runawayOutputIsBoundedAndStartFailureIsVisible() {
        val session = TerminalSession { ProcessBuilder("sh").redirectErrorStream(true).start() }
        try {
            session.start()
            await { session.running.value }
            session.send("i=0; while [ \"\u0024i\" -lt 600 ]; do printf 'line\\n'; i=\u0024((i+1)); done; exit")
            await { !session.running.value }
            assertEquals(500, session.lines.value.size)
            assertTrue(session.lines.value.all { it.length <= 4096 })
        } finally { session.dispose() }
        val failed = TerminalSession { throw IOException("unavailable") }
        try {
            failed.start()
            await { failed.startError.value != null }
            assertFalse(failed.running.value)
            assertTrue(failed.startError.value!!.contains("unavailable"))
        } finally { failed.dispose() }
    }
}
