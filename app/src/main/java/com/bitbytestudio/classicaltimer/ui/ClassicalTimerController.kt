package com.bitbytestudio.classicaltimer.ui

import android.os.CountDownTimer

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

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