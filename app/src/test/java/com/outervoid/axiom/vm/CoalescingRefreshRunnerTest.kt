package com.outervoid.axiom.vm

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CoalescingRefreshRunnerTest {
    @Test
    fun concurrentRequestsCoalesceAndOnlyPublishTheLatestResult() = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val firstQueryGate = CompletableDeferred<Unit>()
        var queryCount = 0
        val published = mutableListOf<Int>()
        val durations = mutableListOf<Long>()
        val runner = CoalescingRefreshRunner(
            scope = scope,
            dispatcher = Dispatchers.Unconfined,
            traceName = null,
            load = {
                queryCount += 1
                if (queryCount == 1) firstQueryGate.await()
                queryCount
            },
            onLoadingChanged = {},
            onResult = { result -> result.onSuccess(published::add) },
            onDurationMillis = durations::add
        )

        val job = runner.refresh()
        runner.refresh()
        runner.refresh()
        firstQueryGate.complete(Unit)
        job.join()

        assertEquals(2, queryCount)
        assertEquals(listOf(2), published)
        assertEquals(2, durations.size)
        assertTrue(durations.all { it >= 0L })
        scope.cancel()
    }

    @Test
    fun cancellationDoesNotPublishAResultAndAlwaysEndsLoading() = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val queryStarted = CompletableDeferred<Unit>()
        val loading = mutableListOf<Boolean>()
        var published = false
        val runner = CoalescingRefreshRunner(
            scope = scope,
            dispatcher = Dispatchers.Unconfined,
            traceName = null,
            load = {
                queryStarted.complete(Unit)
                awaitCancellation()
            },
            onLoadingChanged = loading::add,
            onResult = { published = true }
        )

        val job = runner.refresh()
        queryStarted.await()
        job.cancelAndJoin()

        assertFalse(published)
        assertEquals(listOf(true, false), loading)
        scope.cancel()
    }

    @Test
    fun queryFailureIsReportedWithoutLeavingLoadingActive() = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        val loading = mutableListOf<Boolean>()
        var reportedFailure = false
        val runner = CoalescingRefreshRunner(
            scope = scope,
            dispatcher = Dispatchers.Unconfined,
            traceName = null,
            load = { error("package query failed") },
            onLoadingChanged = loading::add,
            onResult = { reportedFailure = it.isFailure }
        )

        runner.refresh().join()

        assertTrue(reportedFailure)
        assertEquals(listOf(true, false), loading)
        scope.cancel()
    }
}
