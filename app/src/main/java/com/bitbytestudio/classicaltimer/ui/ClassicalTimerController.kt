package com.bitbytestudio.classicaltimer.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TimerState(
    val total: Int = 0,
    val remaining: Int = 0,
    val topBalls: Int = 0,
    val bottomBalls: Int = 0,
    val isRunning: Boolean = false,
    val isPaused: Boolean = false
)

class ClassicalTimerController {

    private val _state = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = _state.asStateFlow()

    private var timerJob: Job? = null
    private var totalSeconds = 0
    private var remainingSeconds = 0

    fun start(seconds: Int, scope: CoroutineScope) {
        stop()
        totalSeconds = seconds
        remainingSeconds = seconds
        _state.value = TimerState(
            total = totalSeconds,
            remaining = remainingSeconds,
            topBalls = remainingSeconds,
            bottomBalls = 0,
            isRunning = true,
            isPaused = false
        )
        launchTimer(scope)
    }

    fun pause() {
        timerJob?.cancel()
        _state.update { it.copy(isRunning = false, isPaused = true) }
    }

    fun resume(scope: CoroutineScope) {
        if (_state.value.isPaused) launchTimer(scope)
    }

    fun stop() {
        timerJob?.cancel()
        totalSeconds = 0
        remainingSeconds = 0
        _state.value = TimerState()
    }

    private fun launchTimer(scope: CoroutineScope) {
        timerJob = scope.launch {
            _state.update { it.copy(isRunning = true, isPaused = false) }

            while (remainingSeconds > 0) {
                delay(1000L)
                remainingSeconds--
                _state.update {
                    TimerState(
                        total = totalSeconds,
                        remaining = remainingSeconds,
                        topBalls = remainingSeconds,
                        bottomBalls = totalSeconds - remainingSeconds,
                        isRunning = remainingSeconds > 0,
                        isPaused = false
                    )
                }
            }
        }
    }
}