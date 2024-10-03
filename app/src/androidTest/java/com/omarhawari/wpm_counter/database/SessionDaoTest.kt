package com.omarhawari.wpm_counter.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.omarhawari.wpm_counter.database.daos.Session
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SessionDaoTest {

    private lateinit var database: WPMDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WPMDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertSessionAndRetrieval() = runTest {
        val session = Session(
            uuid = "some-uuid",
            userId = "user-id",
            sessionStartTime = 1000L,
            sessionEndTime = 2000L,
            wordPerMinute = 30.0f
        )
        database.sessionDao().insertAll(session)

        val retrieveSession = database.sessionDao().getSession(session.uuid)

        // Assert
        assertNotNull(retrieveSession)
        assertEquals(retrieveSession.uuid, session.uuid)
        assertEquals(retrieveSession.userId, session.userId)
    }

    // Update session and assert that the session has been updated in the database
    @Test
    fun insertSessionAndUpdate() = runTest {

        // Insert session
        val session = Session(
            uuid = "some-uuid",
            userId = "user-id",
        )
        database.sessionDao().insertAll(session)

        // Update the session's start time
        val updatedSession = session.copy(sessionStartTime = 2000L)
        database.sessionDao().updateSession(updatedSession)

        val retrieveSession = database.sessionDao().getSession(updatedSession.uuid)

        // Assert
        assertNotNull(retrieveSession)
        assertEquals(retrieveSession.uuid, updatedSession.uuid)
        assertEquals(retrieveSession.sessionStartTime, updatedSession.sessionStartTime)
    }

    // Insert multiple sessions and retrieve them in descending order by session end time
    @Test
    fun insertSessionsRetrieveByTimeDesc() = runTest {

        // Insert sessions
        val session1 = Session(
            uuid = "some-uuid1",
            userId = "user-id",
            sessionEndTime = 1000L
        )
        val session2 = Session(
            uuid = "some-uuid2",
            userId = "user-id",
            sessionEndTime = 2000L
        )
        database.sessionDao().insertAll(session1, session2)

        val retrieveSession = database.sessionDao().getSessionsForUserIdDescTime("user-id")

        // Assert
        assertNotNull(retrieveSession)
        assertEquals(retrieveSession[0].uuid, session2.uuid)
        assertEquals(retrieveSession[1].uuid, session1.uuid)
    }

    // Insert multiple sessions and retrieve them in descending order by wpm
    @Test
    fun insertSessionsRetrieveByWPMDesc() = runTest {

        // Insert sessions
        val session1 = Session(
            uuid = "some-uuid1",
            userId = "user-id",
            wordPerMinute = 100F
        )
        val session2 = Session(
            uuid = "some-uuid2",
            userId = "user-id",
            wordPerMinute = 105F
        )
        database.sessionDao().insertAll(session1, session2)

        val retrieveSession = database.sessionDao().getSessionsForUserIdDescWpm("user-id")

        // Assert
        assertNotNull(retrieveSession)
        assertEquals(retrieveSession.size, 2)
        assertEquals(retrieveSession[0].uuid, session2.uuid)
        assertEquals(retrieveSession[1].uuid, session1.uuid)
    }

    // Insert multiple sessions and retrieve the session with the highest wpm for a user
    @Test
    fun testGetHighScoreSession() = runTest {

        val userId = "user1"
        val session1 = Session(uuid = "session1", userId = userId, wordPerMinute = 100f)
        val session2 = Session(uuid = "session2", userId = userId, wordPerMinute = 120f)
        // Insert session for a different user to make sure the query doesn't query all sessions.
        val session3 = Session(uuid = "session3", userId = "user2", wordPerMinute = 130f)

        // Insert some sessions into the database
        database.sessionDao().insertAll(session1, session2, session3)

        val highScoreSession = database.sessionDao().getHighScoreSession(userId)

        // Assert
        assertNotNull(highScoreSession)
        assertEquals(session2.uuid, highScoreSession?.uuid) // session2 has the highest wordPerMinute for user1
        assertEquals(120f, highScoreSession?.wordPerMinute)
    }

    // Insert no sessions and retrieve null for a user with no sessions
    @Test
    fun testGetHighScoreSessionForUserWithNoSessions() = runTest {

        val userId = "userWithNoSessions"

        // Act
        val highScoreSession = database.sessionDao().getHighScoreSession(userId)

        // Assert
        assertNull(highScoreSession) // Should return null for a user with no sessions
    }

}