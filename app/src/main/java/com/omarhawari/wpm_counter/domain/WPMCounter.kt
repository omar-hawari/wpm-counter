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

/**
 * WPMCounter is a class responsible for calculating Words Per Minute (WPM) based on keystrokes
 * and managing the counting process asynchronously using coroutines.
 *
 * @param howOftenToCalculate The interval (in milliseconds) at which WPM should be recalculated. Default is 50ms.
 * @param dispatcher The coroutine dispatcher that determines the thread on which the coroutine runs. Default is Dispatchers.IO. This is useful for testing purposes.
 */
class WPMCounter(
    private val howOftenToCalculate: Long = 50,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    init {
        // Ensure the calculation interval is within a valid range (0 to 1000 milliseconds)
        if (howOftenToCalculate < 0 || howOftenToCalculate > 1000) throw IllegalArgumentException("howOftenToCalculate must be between 0 -> 1000 milliseconds")
    }

    // Observed in the UI to display the current WPM (Word Per Minute)
    val wpmCount = MutableStateFlow(0f)

    // Observed in the UI to display the current accuracy
    val accuracy = MutableStateFlow(0f)

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

    /**
     * Consumes a list of KeyStroke objects, updating the keyStrokesCount and triggering
     * the WPM calculation timer. Also starts a pause timer that pauses the calculation if the user stops typing after 2 seconds
     *
     * @param keyStrokes List of KeyStroke objects representing the user's input.
     */
    fun consumeKeyStrokes(keyStrokes: List<KeyStroke>) {
        this.keyStrokesCount = keyStrokes.size
        if (!::timerJob.isInitialized) startTimer()
        startPauseTimer() // Starts pause timer that, if finished, pauses the wpm calculation
    }

    /**
     * Consumes the accuracy value (a Float between 0 and 100) and updates the accuracy state flow.
     *
     * @param accuracy A Float representing typing accuracy. Must be between 0 and 100
     * @throws IllegalArgumentException if the accuracy is outside the allowed range (0 to 100).
     */
    fun consumeAccuracy(accuracy: Float) {
        if (accuracy < 0 || accuracy > 100f)
            throw IllegalArgumentException("Accuracy cannot be negative nor greater than 100f")
        wpmScope.launch {
            this@WPMCounter.accuracy.emit(accuracy)
        }
    }

    /**
     * Starts a coroutine that recalculates WPM at regular intervals, determined by the howOftenToCalculate value (default is 50 ms)
     * Stops recalculating when isFinished is set to true, as the counting process has ended
     */
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

    /**
     * Starts a timer that pauses the WPM calculation if the user stops typing for 2 seconds.
     */
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

    /**
     * Calculates the Words Per Minute (WPM) based on the number of keystrokes and time elapsed.
     * Uses the formula: (keyStrokeCount / 5) / (duration in minutes)
     * Where 5 is the average number of characters per word.
     *
     * @return The calculated WPM value as a Float.
     */
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