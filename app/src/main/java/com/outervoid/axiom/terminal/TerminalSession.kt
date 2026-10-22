package com.outervoid.axiom.terminal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.PrintWriter

/** Owns one shell. All process I/O runs off Main; stale readers cannot end a replacement shell. */
class TerminalSession internal constructor(private val createProcess: () -> Process) {
    constructor() : this({
        ProcessBuilder("/system/bin/sh").redirectErrorStream(true).apply {
            environment()["TERM"] = "dumb"
        }.start()
    })

    private val lock = Any()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var process: Process? = null
    private var writer: PrintWriter? = null
    private var generation = 0L
    private var starting = false
    private var disposed = false

    private val _lines = MutableStateFlow<List<String>>(emptyList())
    val lines: StateFlow<List<String>> = _lines.asStateFlow()
    private val _running = MutableStateFlow(false)
    val running: StateFlow<Boolean> = _running.asStateFlow()
    private val _startError = MutableStateFlow<String?>(null)
    val startError: StateFlow<String?> = _startError.asStateFlow()

    fun start() {
        val request = synchronized(lock) {
            if (disposed || starting || process != null) return
            starting = true
            ++generation
        }
        scope.launch {
            val shell = try {
                createProcess()
            } catch (e: IOException) {
                startFailed(request, e)
                return@launch
            } catch (e: SecurityException) {
                startFailed(request, e)
                return@launch
            }
            val accepted = synchronized(lock) {
                if (disposed || generation != request) false else {
                    process = shell
                    writer = PrintWriter(shell.outputStream, true)
                    starting = false
                    _running.value = true
                    _startError.value = null
                    appendLine("AXIOM TERMINAL v1.0")
                    appendLine("SESSION ACTIVE")
                    appendLine("---")
                    true
                }
            }
            if (!accepted) {
                release(shell)
                return@launch
            }
            try {
                shell.inputStream.bufferedReader().use { reader ->
                    // Bound even output without newlines (e.g. binary data or runaway commands).
                    val line = StringBuilder()
                    val buffer = CharArray(1024)
                    while (true) {
                        val count = reader.read(buffer)
                        if (count < 0) break
                        synchronized(lock) {
                            if (generation != request) return@launch
                            for (i in 0 until count) {
                                val ch = buffer[i]
                                if (ch == '\n') {
                                    appendLine(line.toString().removeSuffix("\r"))
                                    line.setLength(0)
                                } else {
                                    line.append(ch)
                                    if (line.length == 4096) {
                                        appendLine(line.toString())
                                        line.setLength(0)
                                    }
                                }
                            }
                        }
                    }
                    synchronized(lock) {
                        if (generation == request && line.isNotEmpty()) appendLine(line.toString())
                    }
                }
            } catch (e: IOException) {
                synchronized(lock) {
                    if (generation == request) appendLine("Shell output failed: ${e.message}")
                }
            } finally {
                synchronized(lock) {
                    if (generation == request) {
                        process = null
                        writer = null
                        _running.value = false
                        appendLine("--- SESSION ENDED ---")
                    }
                }
                release(shell)
            }
        }
    }

    private fun startFailed(request: Long, error: Exception) = synchronized(lock) {
        if (generation == request) {
            starting = false
            _startError.value = "Shell session could not start: ${error.message}"
        }
    }

    fun send(command: String) {
        val target = synchronized(lock) { process ?: return }
        scope.launch {
            // A blocked write must not hold the lifecycle lock or the UI thread.
            val output = synchronized(lock) {
                if (process !== target) return@launch
                appendLine("$ $command")
                writer ?: return@launch
            }
            synchronized(output) {
                output.println(command)
                if (output.checkError()) {
                    synchronized(lock) {
                        if (process === target) appendLine("Shell input failed; close and reopen the terminal.")
                    }
                }
            }
        }
    }

    fun clear() { _lines.value = emptyList() }

    fun kill() {
        val target = synchronized(lock) {
            generation++
            starting = false
            val old = process
            process = null
            writer = null
            _running.value = false
            if (old != null) appendLine("--- SESSION TERMINATED ---")
            old
        }
        // Destroy unblocks the reader, whose finally block closes the streams on IO.
        target?.destroy()
    }

    fun dispose() {
        synchronized(lock) { disposed = true }
        kill()
        scope.cancel()
    }

    private fun release(shell: Process) {
        shell.destroy()
        for (stream in listOf(shell.inputStream, shell.errorStream, shell.outputStream)) {
            try {
                stream.close()
            } catch (e: IOException) {
                appendLine("Shell cleanup failed: ${e.message}")
            }
        }
    }

    private fun appendLine(line: String) {
        _lines.update { (it + line).takeLast(500) }
    }
}
