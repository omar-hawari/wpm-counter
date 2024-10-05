package com.omarhawari.wpm_counter.domain

import com.omarhawari.wpm_counter.database.daos.KeyStroke
import com.omarhawari.wpm_counter.database.daos.ScreenOrientation
import com.omarhawari.wpm_counter.database.daos.Session
import com.omarhawari.wpm_counter.database.daos.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for handling WPM-related database operations
 * Includes methods for user, session, and keystroke management
 */
interface WPMRepository {

    // User related functions

    /**
     * Generates a new User object with a random UUID and current timestamp
     * @param username The username of the new user
     * @return The newly created User object
     */
    fun generateNewUser(username: String): User

    /**
     * Fetches a user from the database by username
     * @param username The username of the user to fetch
     * @return The User object, or null if not found
     */
    suspend fun getUser(username: String): User?

    /**
     * Fetches a user from the database by uuid
     * @param userId The username of the user to fetch
     * @return The User object, or null if not found
     */
    suspend fun getUserById(userId: String): User?

    /**
     * Inserts a user into the database if they don't already exist
     * @param username The username to check and potentially insert
     * @return The existing or newly inserted User object
     */
    suspend fun insertUserIfNotExists(username: String): User

    /**
     * Fetches all users with high scores
     * @return A Flow that emits a list of users with their highest scores
     */
    suspend fun getUsersWithHighScore(): Flow<List<User>>

    // Session related functions

    /**
     * Generates a new Session object with a random UUID
     * @param userId The ID of the user associated with the session
     * @return The newly created Session object
     */
    fun generateSession(userId: String): Session


    /**
     * Inserts a new session for the specified user into the database
     * @param userId The ID of the user to create a session for
     * @return The newly inserted Session object
     */
    suspend fun insertSession(userId: String): Session

    /**
     * Updates an existing session in the database
     * @param session The Session object with updated information
     */
    suspend fun updateSession(session: Session)

    /**
     * Fetches a session by its uuid
     * @param sessionId The uuid of the session to fetch
     * @return The Session object
     */
    suspend fun getSession(sessionId: String): Session


    // KeyStroke related functions


    /**
     * Generates a new KeyStroke object
     * @param sessionId The uuid of the session associated with the keystroke
     * @param keyPressTime The timestamp of the key press event
     * @param keyReleaseTime The timestamp of the key release event
     * @param keyCode The key code of the pressed key
     * @param isCorrect Whether the keystroke matches the expected input.
     * @param phoneOrientation The orientation of the device during the keystroke
     * @return The newly created KeyStroke object
     */
    fun generateKeyStroke(
        sessionId: String,
        keyPressTime: Long,
        keyReleaseTime: Long,
        keyCode: String,
        isCorrect: Boolean,
        phoneOrientation: ScreenOrientation
    ): KeyStroke


    /**
     * Inserts a new KeyStroke into the database
     * @param sessionId The ID of the session associated with the keystroke
     * @param keyPressTime The timestamp of the key press event
     * @param keyReleaseTime The timestamp of the key release event
     * @param keyCode The key code of the pressed key
     * @param isCorrect Whether the keystroke matches the expected input
     * @param phoneOrientation The orientation of the device during the keystroke
     */
    suspend fun insertKeyStroke(
        sessionId: String,
        keyPressTime: Long,
        keyReleaseTime: Long,
        keyCode: String,
        isCorrect: Boolean,
        phoneOrientation: ScreenOrientation
    )

    /**
     * Fetches the count of keystrokes for a given session
     * @param sessionId The UUID of the session
     * @return A Flow that emits the count of keystrokes
     */
    suspend fun getKeyStrokeCountForSession(sessionId: String): Flow<Int>

    /**
     * Fetches the keystrokes for a given session
     * @param sessionId The UUID of the session
     * @return A Flow that emits the keystrokes for a session
     */
    suspend fun getKeyStrokesForSession(sessionId: String): Flow<List<KeyStroke>>


    /**
     * Fetches the accuracy rate for a given session
     * @param sessionId The UUID of the session
     * @return A Flow that emits the accuracy rate as a percentage
     */
    suspend fun getAccuracyPerSession(sessionId: String): Flow<Float>

}
