package com.omarhawari.wpm_counter.screens.wpm_counter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omarhawari.wpm_counter.database.daos.ScreenOrientation
import com.omarhawari.wpm_counter.database.daos.Session
import com.omarhawari.wpm_counter.database.daos.User
import com.omarhawari.wpm_counter.di.WPMText
import com.omarhawari.wpm_counter.domain.WPMCounter
import com.omarhawari.wpm_counter.domain.WPMRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing the WPM counter session, handling user input, and updating
 * session statistics like accuracy and WPM. This ViewModel interacts with the repository
 * to fetch and store session-related data.
 * This ViewModel is linked to the WPMCounterScreen composable and provides UI state
 */
@HiltViewModel
class WPMCounterViewModel @Inject constructor(
    private val repository: WPMRepository,
    // Putting text in the constructor for testing purposes
    val text: WPMText
) : ViewModel() {

    // Where the WPM counter logic and code is executed
    val wpmCounter = WPMCounter()

    // Holds the accuracy of the typing session as a percentage. Updates from db
    val accuracy = MutableStateFlow(0f)

    // Holds the current session data, fetched from the repository
    private lateinit var sessionId: String
    val session = MutableStateFlow<Session?>(null)

    // Holds the current user participating in the session
    val currentUser = MutableStateFlow<User?>(null)

    // Tracks the current cursor position in the text being typed.
    val cursor = MutableStateFlow(0)


    /**
     * Fetches the session, the currentUser, and the accuracy from the repository
     *
     * @param sessionId The id of the session being fetched
     */
    fun init(sessionId: String) {

        this.sessionId = sessionId

        // Fetch the user
        viewModelScope.launch(Dispatchers.IO) {
            session.emit(repository.getSession(sessionId))
            session.value?.let {
                currentUser.emit(repository.getUserById(it.userId))
            }
        }

        // Fetch the session
        viewModelScope.launch(Dispatchers.IO) {
            session.collect {
                if (it != null)
                    repository.updateSession(it)
            }
        }

        // Start collecting Accuracy stats from the db
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAccuracyPerSession(sessionId).collect {
                accuracy.emit(it)
            }
        }

        // Start collecting keystrokes from the db
        collectKeyStrokesFromDB()
    }

    // Contains a list of the inputted characters and whether they are correct or not
    // Is consumed by the UI to display the correct/incorrect characters in the overlay
    val letterMatchList = mutableListOf<Pair<Char, Boolean>>()

    /**
     * Handles character input, checks if it is correct, and stores the result in the database
     *
     * Finishes the session if the input is correct and the cursor is at the end of the text
     *
     * @param inputChar The character input by the user
     * @param keyReleaseTime The timestamp of the key stroke
     * @param orientation The screen orientation at the time of the key stroke
     */
    fun onCharReceived(
        inputChar: Char,
        keyPressTime: Long,
        keyReleaseTime: Long,
        orientation: ScreenOrientation
    ) {

        viewModelScope.launch(Dispatchers.IO) {

            // Start the session if it is not already started
            if (session.value?.sessionStartTime == null) {
                updateSession(sessionStartTime = keyPressTime)
            }

            val isInputCharacterCorrect = text.value[cursor.value] == inputChar

            if (isInputCharacterCorrect) {

                if (cursor.value < text.value.length - 1) { // If the cursor is not yet at the end of the text, move the cursor forward
                    cursor.emit(cursor.value + 1)
                } else { // Otherwise, finish the session and stop the wpmCounter
                    wpmCounter.finish()
                    updateSession(
                        sessionEndTime = keyReleaseTime,
                        wordPerMinute = wpmCounter.wpmCount.value
                    )
                }
            } else {
                // If the input character is incorrect, do not move the cursor and just accept the input as incorrect input
            }

            // Insert the input character and its correctness into the letterMatchList
            letterMatchList.add(Pair(inputChar, isInputCharacterCorrect))

            // Insert the keystroke into the database, to be used in WPM and Accuracy calculations
            repository.insertKeyStroke(
                sessionId = sessionId,
                keyPressTime = keyPressTime,
                keyReleaseTime = keyReleaseTime,
                keyCode = inputChar.toString(),
                isCorrect = isInputCharacterCorrect,
                phoneOrientation = orientation
            )
        }
    }

    /**
     * Updates session details such as start time, end time, and WPM count
     *
     * @param sessionStartTime The start time of the session, if applicable
     * @param sessionEndTime The end time of the session, if applicable
     * @param wordPerMinute The WPM count for the session, if applicable
     */
    private suspend fun updateSession(
        sessionStartTime: Long? = null,
        sessionEndTime: Long? = null,
        wordPerMinute: Float? = null
    ) {
        session.emit(
            session.value?.copy(
                sessionStartTime = sessionStartTime ?: session.value?.sessionStartTime,
                sessionEndTime = sessionEndTime ?: session.value?.sessionStartTime,
                wordPerMinute = wordPerMinute ?: session.value?.wordPerMinute
            )
        )
    }

    /**
     * Collects keystrokes from the repository and passes them to the WPMCounter for WPM calculations
     */
    private fun collectKeyStrokesFromDB() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getKeyStrokeCountForSession(sessionId).collect { keyStrokeCount ->
                if (session.value == null || session.value?.sessionStartTime == null) {
                    return@collect
                }
                wpmCounter.consumeKeyStrokes(keyStrokeCount)
            }
        }
    }
}