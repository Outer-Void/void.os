package com.outervoid.axiom.vm

import android.os.Trace
import androidx.annotation.MainThread
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/** Runs one refresh at a time and collapses concurrent requests into one final refresh. */
internal class CoalescingRefreshRunner<T>(
    private val scope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher,
    private val traceName: String?,
    private val load: suspend () -> T,
    private val onLoadingChanged: (Boolean) -> Unit,
    private val onResult: (Result<T>) -> Unit,
    private val onDurationMillis: (Long) -> Unit = {}
) {
    private var job: Job? = null
    private var refreshPending = false

    @MainThread
    fun refresh(): Job {
        job?.takeIf { it.isActive }?.let { activeJob ->
            refreshPending = true
            return activeJob
        }

        return scope.launch {
            onLoadingChanged(true)
            try {
                var result: Result<T>
                do {
                    refreshPending = false
                    val measured = withContext(dispatcher) {
                        val startedAt = System.nanoTime()
                        traceName?.let { Trace.beginSection(it) }
                        val loaded = try {
                            Result.success(load())
                        } catch (cancelled: CancellationException) {
                            throw cancelled
                        } catch (failure: Exception) {
                            Result.failure(failure)
                        } finally {
                            if (traceName != null) Trace.endSection()
                        }
                        loaded to ((System.nanoTime() - startedAt) / 1_000_000L)
                    }
                    result = measured.first
                    onDurationMillis(measured.second)
                } while (refreshPending && currentCoroutineContext().isActive)

                onResult(result)
            } finally {
                onLoadingChanged(false)
            }
        }.also { job = it }
    }
}
