package com.outervoid.axiom.terminal

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter

class TerminalSession {
    private var process: Process? = null
    private var writer: PrintWriter? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private var readJob: Job? = null

    private val _lines = MutableStateFlow<List<String>>(emptyList())
    val lines: StateFlow<List<String>> = _lines.asStateFlow()

    private val _running = MutableStateFlow(false)
    val running: StateFlow<Boolean> = _running.asStateFlow()

    private val _startError = MutableStateFlow<String?>(null)
    val startError: StateFlow<String?> = _startError.asStateFlow()

    fun start() {
        if (_running.value) return
        try {
            val pb = ProcessBuilder("/system/bin/sh")
            pb.redirectErrorStream(true)
            pb.environment()["TERM"] = "dumb"
            val p = pb.start()
            process = p
            writer = PrintWriter(p.outputStream, true)
            _running.value = true
            _startError.value = null
            appendLine("AXIOM TERMINAL v1.0")
            appendLine("SESSION ACTIVE")
            appendLine("---")
            readJob = scope.launch {
                val reader = BufferedReader(InputStreamReader(p.inputStream))
                try {
                    while (isActive) {
                        val line = reader.readLine() ?: break
                        appendLine(line)
                    }
                } catch (_: Exception) {}
                _running.value = false
                appendLine("--- SESSION ENDED ---")
            }
        } catch (e: Exception) {
            _running.value = false
            _startError.value = "Shell session could not start: ${e.message}"
        }
    }

    fun send(command: String) {
        if (!_running.value) return
        appendLine("$ $command")
        writer?.println(command)
    }

    fun clear() {
        _lines.value = emptyList()
    }

    fun kill() {
        readJob?.cancel()
        process?.destroy()
        process = null
        writer = null
        _running.value = false
        appendLine("--- SESSION TERMINATED ---")
    }

    fun dispose() {
        readJob?.cancel()
        process?.destroy()
        scope.cancel()
    }

    private fun appendLine(line: String) {
        val current = _lines.value
        _lines.value = if (current.size > 500) current.drop(100) + line else current + line
    }
}
