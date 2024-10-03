package com.omarhawari.wpm_counter.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.omarhawari.wpm_counter.database.WPMDatabase
import com.omarhawari.wpm_counter.database.WPMRepositoryDBImpl
import com.omarhawari.wpm_counter.di.WPMText
import com.omarhawari.wpm_counter.domain.WPMRepository
import com.omarhawari.wpm_counter.exts.toTwoDecimalPlaces
import com.omarhawari.wpm_counter.screens.wpm_counter.TEST_TAG_ACCURACY
import com.omarhawari.wpm_counter.screens.wpm_counter.TEST_TAG_TEXT_FIELD
import com.omarhawari.wpm_counter.screens.wpm_counter.TEST_TAG_WPM
import com.omarhawari.wpm_counter.screens.wpm_counter.TEST_TAG_WPM_SCREEN
import com.omarhawari.wpm_counter.screens.wpm_counter.WPMCounterScreen
import com.omarhawari.wpm_counter.screens.wpm_counter.WPMCounterViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.random.Random

class WPMCounterScreenTest {

    // Database and repository for testing
    private lateinit var database: WPMDatabase
    private lateinit var repository: WPMRepository
    private lateinit var wpmCounterViewModel: WPMCounterViewModel

    private val wpmText = WPMText("This is a test text")

    // Compose test rule to set the content for the tests
    @get:Rule
    val composeTestRule = createComposeRule()

    private var testUsername = "testUsername"
    private lateinit var sessionId: String

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WPMDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = WPMRepositoryDBImpl(database)

        wpmCounterViewModel = WPMCounterViewModel(repository, wpmText)

        runBlocking {
            val user = repository.insertUserIfNotExists(testUsername)
            val session = repository.insertSession(user.uuid)
            sessionId = session.uuid
        }
    }

    // Test to verify the WPM counter screen displays correctly
    @Test
    fun testWPMCounterScreen() = runTest {
        composeTestRule.setContent {
            WPMCounterScreen(viewModel = wpmCounterViewModel, sessionId = sessionId)
        }

        // Assert the screen is correctly displayed with the text
        composeTestRule.onNodeWithTag(TEST_TAG_WPM_SCREEN).assertIsDisplayed()
        composeTestRule.onNodeWithText(wpmText.value).assertIsDisplayed()
        // Assert the username is displayed correctly
        composeTestRule.onNodeWithText("User: $testUsername").assertIsDisplayed()
    }

    // Test to verify that all correct inputs yield 100% accuracy
    @Test
    fun testWPMCounterInputAllCorrect() = runTest {

        composeTestRule.setContent {
            WPMCounterScreen(viewModel = wpmCounterViewModel, sessionId = sessionId)
        }

        // Input each character of the sample text into the text field
        wpmText.value.forEach {
            composeTestRule.onNodeWithTag(TEST_TAG_TEXT_FIELD).performTextInput(it.toString())
            delay(100)
        }

        // Assert that the accuracy displayed is 100%
        composeTestRule.onNodeWithTag(TEST_TAG_ACCURACY).assertTextEquals("Accuracy 100.00%")
    }

    // Test to verify accuracy when random inputs are given
    @Test
    fun testWPMCounterInputNotAllCorrect() = runTest {

        composeTestRule.setContent {
            WPMCounterScreen(viewModel = wpmCounterViewModel, sessionId = sessionId)
        }

        var totalCharactersCount = 0 // Count of all characters inputted
        var cursor = 0 // Current position in the sample text

        while (cursor < wpmText.value.length) { // Traverse through the text

            // Randomize correct input
            val shouldBeCorrect = Random.nextBoolean()

            composeTestRule.onNodeWithTag(TEST_TAG_TEXT_FIELD).performTextInput(
                if (shouldBeCorrect) // If the random character is correct, enter it
                    wpmText.value[cursor].toString()
                else "!" // Otherwise, enter a random character that is guaranteed to be wrong
            )

            if (shouldBeCorrect) { // If the text that's entered is correct, then move the cursor forward
                cursor++
            } else {
                // Do nothing
            }

            // Increase the total number of characters entered
            totalCharactersCount++

            delay(100)
        }

        val accuracy = (
                wpmText.value.length.toFloat() // The number of charaters in the text is the number of correct input characters
                        / totalCharactersCount // Divided by the total number of inputs
                ) * 100f // Multiplied by 100 to show the percentage


        composeTestRule.onNodeWithTag(TEST_TAG_ACCURACY)
            .assertTextEquals(
                "Accuracy ${
                    accuracy.toTwoDecimalPlaces()
                }%"
            )
    }

    // Test to verify WPM when input is all correct
    @Test
    fun testWPMCountIsCorrectWhenInputIsCorrect() = runTest {

        composeTestRule.setContent {
            WPMCounterScreen(viewModel = wpmCounterViewModel, sessionId = sessionId)
        }

        // Input each character of the sample text into the text field
        wpmText.value.forEach {
            composeTestRule.onNodeWithTag(TEST_TAG_TEXT_FIELD).performTextInput(it.toString())
            delay(100)
        }

        val wpm = wpmCounterViewModel.wpmCounter.wpmCount.value

        // Assert that the accuracy displayed is 100%
        composeTestRule.onNodeWithTag(TEST_TAG_WPM)
            .assertTextEquals("Final WPM is ${wpm.toTwoDecimalPlaces()}")
    }

    // Test to verify WPM when input is all correct
    @Test
    fun testWPMCountIsCorrectWhenInputIsRandom() = runTest {

        composeTestRule.setContent {
            WPMCounterScreen(viewModel = wpmCounterViewModel, sessionId = sessionId)
        }

        var totalCharactersCount = 0 // Count of all characters inputted
        var cursor = 0 // Current position in the sample text

        while (cursor < wpmText.value.length) { // Traverse through the text

            // Randomize correct input
            val shouldBeCorrect = Random.nextBoolean()

            composeTestRule.onNodeWithTag(TEST_TAG_TEXT_FIELD).performTextInput(
                if (shouldBeCorrect) // If the random character is correct, enter it
                    wpmText.value[cursor].toString()
                else "!" // Otherwise, enter a random character that is guaranteed to be wrong
            )

            if (shouldBeCorrect) { // If the text that's entered is correct, then move the cursor forward
                cursor++
            } else {
                // Do nothing
            }

            // Increase the total number of characters entered
            totalCharactersCount++

            delay(100)
        }

        val wpm = wpmCounterViewModel.wpmCounter.wpmCount.value

        // Assert that the accuracy displayed is 100%
        composeTestRule.onNodeWithTag(TEST_TAG_WPM)
            .assertTextEquals("Final WPM is ${wpm.toTwoDecimalPlaces()}")
    }

}