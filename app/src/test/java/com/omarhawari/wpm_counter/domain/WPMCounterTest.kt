package com.omarhawari.wpm_counter.domain

import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WPMCounterTest {

    private lateinit var wpmCounter: WPMCounter
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        // Initialize the WPMCounter with the test dispatcher for controlling coroutine timing.
        Dispatchers.setMain(testDispatcher)
        wpmCounter = WPMCounter(dispatcher = testDispatcher, howOftenToCalculate = 50)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testInvalidHowOftenToCalculate() {
        // Should throw IllegalArgumentException
        WPMCounter(howOftenToCalculate = -1)
        WPMCounter(howOftenToCalculate = 2000)
    }

    @Test
    fun `test WPM calculation after consuming keystrokes`() = runTest(testDispatcher) {

        // Simulate consuming 50 keystrokes.
        for (i in 1..50) {
            wpmCounter.consumeKeyStrokes(i)
            delay(100) // Simulate keystroke delay
        }

        wpmCounter.finish()

        // Get the first emitted WPM value from the flow.
        val wpm = wpmCounter.wpmCount.value

        // The expected WPM for 50 keystrokes in 5 seconds is 120 WPM (approx, with 2 of margin of error).
        assertEquals(120f, wpm, 2f)
    }

    @Test
    fun `test WPM Counter is paused after 2 seconds of inactivity`() = runTest(testDispatcher) {

        // Simulate consuming 20 keystrokes over 2 seconds
        for (i in 1..20) {
            wpmCounter.consumeKeyStrokes(i)
            delay(100) // Simulate keystroke delay
        }

        delay(2000) // simulate 2 seconds delay, the counter should be paused

        // Get the wpm value after the counter has been paused
        val wpmAfterPaused = wpmCounter.wpmCount.value

        delay(2000) // simulate another 2 seconds delay, the counter should still be paused.

        val wpmAfterPausedAfterDelay = wpmCounter.wpmCount.value

        // The expected WPM for 50 keystrokes in 5 seconds is 120 WPM (approx, with 2 of margin of error).
        assertEquals(wpmAfterPaused, wpmAfterPausedAfterDelay, 2f)

        wpmCounter.finish()
    }

    @After
    fun tearDown() = runTest {
        // Reset the main dispatcher after the test.
        Dispatchers.resetMain()
        wpmCounter.finish()
    }


}