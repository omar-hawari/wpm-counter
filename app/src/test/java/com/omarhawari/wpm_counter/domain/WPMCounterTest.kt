package com.omarhawari.wpm_counter.domain

import com.omarhawari.wpm_counter.database.daos.KeyStroke
import com.omarhawari.wpm_counter.database.daos.ScreenOrientation
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertThrows
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

        // Initialize a list to hold the keystrokes
        val keyStrokes = arrayListOf<KeyStroke>()

        val keyStroke = KeyStroke(
            uuid = "some-uuid",
            sessionId = "session-id",
            keyPressTime = 1000L,
            keyReleaseTime = 2000L,
            keyCode = "some-key-code",
            phoneOrientation = ScreenOrientation.PORTRAIT,
            isCorrect = true
        )

        // Simulate consuming 50 keystrokes.
        for (i in 1..50) {
            // Add a keystroke to the list
            keyStrokes.add(keyStroke)

            // Consume the new keystrokes list
            wpmCounter.consumeKeyStrokes(keyStrokes)

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

        // Initialize a list to hold the keystrokes
        val keyStrokes = arrayListOf<KeyStroke>()

        val keyStroke = KeyStroke(
            uuid = "some-uuid",
            sessionId = "session-id",
            keyPressTime = 1000L,
            keyReleaseTime = 2000L,
            keyCode = "some-key-code",
            phoneOrientation = ScreenOrientation.PORTRAIT,
            isCorrect = true
        )

        // Simulate consuming 20 keystrokes over 2 seconds
        for (i in 1..20) {
            // Add a keystroke to the list
            keyStrokes.add(keyStroke)

            // Consume the new keystrokes list
            wpmCounter.consumeKeyStrokes(keyStrokes)
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

    @Test
    fun `test WPM Counter emits accuracy correctly`() = runTest(testDispatcher) {

        wpmCounter.consumeAccuracy(50f)

        // Delay to allow accuracy to be emitted
        delay(10)

        val accuracy = wpmCounter.accuracy.value

        // The expected accuracy is 50
        assertEquals(accuracy, 50f)

        wpmCounter.finish()
    }

    @Test
    fun `test WPM Counter throws exception for invalid accuracy negative values`() = runTest(testDispatcher) {

        // Assert that an exception is thrown when trying to consume an invalid accuracy value.
        assertThrows(IllegalArgumentException::class.java) {
            wpmCounter.consumeAccuracy(-0.5f)
        }

        wpmCounter.finish()
    }

    @Test
    fun `test WPM Counter throws exception for invalid accuracy greater than 100 values`() = runTest(testDispatcher) {

        // Assert that an exception is thrown when trying to consume an invalid accuracy value.
        assertThrows(IllegalArgumentException::class.java) {
            wpmCounter.consumeAccuracy(102f)
        }

        wpmCounter.finish()
    }

    @After
    fun tearDown() = runTest {
        // Reset the main dispatcher after the test.
        Dispatchers.resetMain()
        wpmCounter.finish()
    }


}