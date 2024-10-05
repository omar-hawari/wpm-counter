package com.omarhawari.wpm_counter.database

import com.omarhawari.wpm_counter.database.daos.KeyStroke
import com.omarhawari.wpm_counter.database.daos.ScreenOrientation
import com.omarhawari.wpm_counter.database.daos.Session
import com.omarhawari.wpm_counter.database.daos.User
import com.omarhawari.wpm_counter.domain.WPMRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID


/**
 * Implementation of the WPMRepository interface, providing data handling
 * logic by using the WPMDatabase as a source of truth.
 */
class WPMRepositoryDBImpl(val database: WPMDatabase) : WPMRepository {

    // User-related implementation

    override fun generateNewUser(username: String) = User(
        uuid = UUID.randomUUID().toString(),
        username = username,
        createdTime = System.currentTimeMillis()
    )

    override suspend fun getUser(username: String): User? {
        return database.userDao().getUserByUsername(username)
    }

    override suspend fun getUserById(userId: String): User? {
        return database.userDao().getUserByUserId(userId)
    }

    override suspend fun insertUserIfNotExists(username: String): User =
        withContext(Dispatchers.IO) {
            val existingUser = getUser(username.trim())

            if (existingUser == null) {
                val newUser = generateNewUser(username.trim())
                database.userDao().insertAll(newUser)
                newUser
            } else {
                existingUser
            }
        }

    override suspend fun getUsersWithHighScore(): Flow<List<User>> = withContext(Dispatchers.IO) {
        database.userDao().getUsers().map {
            it.map { user ->
                user.copy(
                    highScore = database.sessionDao().getHighScoreSession(user.uuid)?.wordPerMinute
                )
            }.sortedByDescending { user ->
                user.highScore
            }
        }
    }

    // Session-related implementation

    override fun generateSession(userId: String): Session =
        Session(uuid = UUID.randomUUID().toString(), userId = userId)

    override suspend fun insertSession(userId: String): Session = withContext(Dispatchers.IO) {
        val session = generateSession(userId)
        database.sessionDao().insertAll(session)
        session
    }

    override suspend fun updateSession(session: Session) = withContext(Dispatchers.IO) {
        database.sessionDao().updateSession(session)
    }

    override suspend fun getSession(sessionId: String) = withContext(Dispatchers.IO) {
        database.sessionDao().getSession(sessionId)
    }


    // KeyStroke-related implementation

    override fun generateKeyStroke(
        sessionId: String,
        keyPressTime: Long,
        keyReleaseTime: Long,
        keyCode: String,
        isCorrect: Boolean,
        phoneOrientation: ScreenOrientation
    ) = KeyStroke(
        uuid = UUID.randomUUID().toString(),
        sessionId = sessionId,
        keyPressTime = keyPressTime,
        keyReleaseTime = keyReleaseTime,
        keyCode = keyCode,
        isCorrect = isCorrect,
        phoneOrientation = phoneOrientation
    )

    override suspend fun insertKeyStroke(
        sessionId: String,
        keyPressTime: Long,
        keyReleaseTime: Long,
        keyCode: String,
        isCorrect: Boolean,
        phoneOrientation: ScreenOrientation
    ) = withContext(Dispatchers.IO) {
        database.keyStrokeDao().insertAll(
            generateKeyStroke(
                sessionId,
                keyPressTime,
                keyReleaseTime,
                keyCode,
                isCorrect,
                phoneOrientation
            )
        )
    }

    override suspend fun getKeyStrokeCountForSession(sessionId: String): Flow<Int> =
        withContext(Dispatchers.IO) {
            database.keyStrokeDao().getKeyStrokesCountPerSession(sessionId)
        }

    override suspend fun getKeyStrokesForSession(sessionId: String): Flow<List<KeyStroke>> =
        withContext(Dispatchers.IO) {
            database.keyStrokeDao().getKeyStrokesPerSession(sessionId)
        }

    override suspend fun getAccuracyPerSession(sessionId: String): Flow<Float> =
        withContext(Dispatchers.IO) {
            database.keyStrokeDao().getAccuracyRatePerSession(sessionId)
                .map { it * 100 }
        }

}