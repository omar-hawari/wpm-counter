package com.omarhawari.wpm_counter.ui

import androidx.compose.runtime.mutableStateOf
import com.omarhawari.wpm_counter.screens.wpm_counter.generateKeyStrokeAnalytics
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GenerateKeyStrokeAnalyticsTest {

    // Test keyStrokeGeneration with normal conditions
    @Test
    fun testKeyStrokeGeneration() = runTest {

        // Setup lastKeyReleaseTime to 1000L as the keyReleaseTime for the previous key stroke
        val lastKeyReleaseTime = mutableStateOf(1000L)

        // Generate the key stroke analytics for the current key stroke
        val keyStrokeAnalytics = generateKeyStrokeAnalytics(lastKeyReleaseTime, 2000L)

        assert(keyStrokeAnalytics.keyPressTime == 1000L) // Key press time should be the same as the last key release time
        assert(keyStrokeAnalytics.keyReleaseTime == 2000L) // Key press time should be equal to the current time
    }

    // Test keyStrokeGeneration if the lastKeyReleaseTime is zero
    @Test
    fun testKeyStrokeGenerationILastKeyReleaseTimeIsZero() = runTest {

        // Setup lastKeyReleaseTime to 0 as the keyReleaseTime. Meaning, the user hasn't typed anything yet
        val lastKeyReleaseTime = mutableStateOf(0L)

        // Generate the key stroke analytics for the current key stroke
        val keyStrokeAnalytics = generateKeyStrokeAnalytics(lastKeyReleaseTime, 2000L)

        assert(keyStrokeAnalytics.keyPressTime == 2000L) // Key press time should be the same as the last key release time, which is set to 2000 in generateKeyStrokeAnalytics
        assert(keyStrokeAnalytics.keyReleaseTime == 2000L) // Key press time should be equal to the current time
    }
}