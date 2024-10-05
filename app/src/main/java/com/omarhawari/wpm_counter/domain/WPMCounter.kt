package com.omarhawari.wpm_counter.domain

import com.omarhawari.wpm_counter.database.daos.KeyStroke
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class WPMCounter(
    private val howOftenToCalculate: Long = 50, // How often to calculate the WPM in milliseconds
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    init {
        if (howOftenToCalculate < 0 || howOftenToCalculate > 1000) throw IllegalArgumentException("howOftenToCalculate must be between 0 -> 1000 milliseconds")
    }

    // wpmCount is observed in the UI to display the current WPM
    val wpmCount = MutableStateFlow(0f)

    // isFinished indicates when the counting process has ended
    val isFinished = MutableStateFlow(false)

    // Variables used for calculating words per minute (WPM)
    private var keyStrokesCount: Int = 0 // Total number of keystrokes
    private var timeElapsed: Long = 0 // Total time elapsed in milliseconds
    private var isPaused =
        false // Flag to pause the timer in case the user stops typing for 2 seconds

    // Coroutine setup
    private val wpmScope = CoroutineScope(dispatcher)
    private lateinit var timerJob: Job
    private lateinit var pauseTimerJob: Job

    fun consumeKeyStrokes(keyStrokes: List<KeyStroke>) {
        this.keyStrokesCount = keyStrokes.size
        if (!::timerJob.isInitialized) startTimer()
        startPauseTimer() // Starts pause timer that, if finished, pauses the wpm calculation
    }

    private fun startTimer() {
        timerJob = wpmScope.launch(dispatcher) {
            while (!isFinished.value) {
                delay(howOftenToCalculate)
                if (!isPaused) { // Only calculate the WPM if the timer is not paused, AKA the user has not stopped typing for 2 seconds
                    timeElapsed += howOftenToCalculate
                    calculateWpm()
                }
            }
        }
    }

    private fun startPauseTimer() {
        isPaused = false

        if (::pauseTimerJob.isInitialized) {
            pauseTimerJob.cancel()
        }
        pauseTimerJob = wpmScope.launch(dispatcher) {
            delay(2000)
            isPaused = true
        }
    }

    private suspend fun calculateWpm(): Float {
        val duration = timeElapsed.toDuration(
            DurationUnit.MILLISECONDS
        ).toDouble(DurationUnit.SECONDS)

        val wpm = if (duration == 0.0) // Not to divide by zero
            0f
        else ((keyStrokesCount.toFloat() / 5f) / (duration / 60)).toFloat()

        // The formula is keyStrokeCount / 5 [where 5 is the average word key count] / (duration / 60 [dividing by 60 to convert to minutes])

        wpmCount.emit(wpm)

        return wpm
    }

    // Cleanup after the counter finishes
    suspend fun finish() {
        isFinished.emit(true)

        if (::timerJob.isInitialized) timerJob.cancel()
        if (::pauseTimerJob.isInitialized) pauseTimerJob.cancel()

        wpmScope.coroutineContext.cancelChildren()
    }

}