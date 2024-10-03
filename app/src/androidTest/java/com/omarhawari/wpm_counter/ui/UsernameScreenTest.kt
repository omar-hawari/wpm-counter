package com.omarhawari.wpm_counter.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.omarhawari.wpm_counter.database.WPMDatabase
import com.omarhawari.wpm_counter.database.WPMRepositoryDBImpl
import com.omarhawari.wpm_counter.domain.WPMRepository
import com.omarhawari.wpm_counter.screens.username.TEST_TAG_USER_BUTTON
import com.omarhawari.wpm_counter.screens.username.TEST_TAG_USER_TEXT_FIELD
import com.omarhawari.wpm_counter.screens.username.UsernameScreen
import com.omarhawari.wpm_counter.screens.username.UsernameViewModel
import com.omarhawari.wpm_counter.screens.wpm_counter.generateKeyStrokeAnalytics
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class UsernameScreenTest {

    // Database and repository for testing
    private lateinit var database: WPMDatabase
    private lateinit var repository: WPMRepository
    private lateinit var usernameViewModel: UsernameViewModel

    // Compose test rule to set the content for the tests
    @get:Rule
    val composeTestRule = createComposeRule()

    private val testUsername = "testUsername"

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WPMDatabase::class.java
        ).allowMainThreadQueries().build()

        repository = WPMRepositoryDBImpl(database)

        usernameViewModel = UsernameViewModel(repository)

        runBlocking {
            repository.insertUserIfNotExists(testUsername)
        }
    }

    @After
    fun tearDown() {
        // Close the database
        database.close()
    }

    // Test to ensure that the username is displayed if it exists in the database
    @Test
    fun testUserDisplayedIfExists() = runTest {
        composeTestRule.setContent {
            UsernameScreen(navController = null, viewModel = usernameViewModel)
        }

        // Assert that the user name is displayed
        composeTestRule.onNodeWithText(testUsername).assertIsDisplayed()
    }

    // Test to ensure that a non-existent username is not displayed
    @Test
    fun testUserNotDisplayedIfNotExists() = runTest {
        composeTestRule.setContent {
            UsernameScreen(navController = null, viewModel = usernameViewModel)
        }

        // Not adding any user

        // Assert that the user name is NOT displayed
        composeTestRule.onNodeWithText("non-existent user").assertIsNotDisplayed()
    }

    // Test to input a new username and check if it gets displayed
    @Test
    fun inputNewUsernameThenUserDisplayed() = runTest {
        composeTestRule.setContent {
            UsernameScreen(navController = null, viewModel = usernameViewModel)
        }

        // Input the username
        composeTestRule.onNodeWithTag(TEST_TAG_USER_TEXT_FIELD).performTextInput(testUsername)

        // Add the username to the db
        composeTestRule.onNodeWithTag(TEST_TAG_USER_BUTTON).performClick()

        // Two nodes should have this username in their text. The text field and one of the user rows.
        composeTestRule.onAllNodesWithText(testUsername).assertCountEquals(2)
    }

}