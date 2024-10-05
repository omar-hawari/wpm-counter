package com.omarhawari.wpm_counter.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.omarhawari.wpm_counter.database.KeyStrokeTestData.keyStroke1
import com.omarhawari.wpm_counter.database.KeyStrokeTestData.keyStroke2
import com.omarhawari.wpm_counter.database.KeyStrokeTestData.keyStroke3
import com.omarhawari.wpm_counter.database.KeyStrokeTestData.keyStroke4
import com.omarhawari.wpm_counter.database.daos.KeyStroke
import com.omarhawari.wpm_counter.database.daos.ScreenOrientation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class KeyStrokeDaoTest {

    private lateinit var database: WPMDatabase

    // Setup the in-memory database before each test
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

    // Test to insert multiple keystrokes and retrieve them by session ID
    @Test
    fun insertMultipleKeyStrokeAndRetrieval() = runTest {

        // Insert the two keystrokes into the database
        database.keyStrokeDao().insertAll(
            keyStroke1.copy(keyReleaseTime = 1000L),
            keyStroke2.copy(keyReleaseTime = 2000L)
        )

        val retrieveKeyStrokes = database.keyStrokeDao().getKeyStrokesPerSession("session-id").first()

        // Assert
        assertNotNull(retrieveKeyStrokes)
        assertEquals(retrieveKeyStrokes.size, 2)

        // Assert that the first retrieved keystroke matches the expected data
        assertEquals(retrieveKeyStrokes[0].uuid, keyStroke2.uuid)
        assertEquals(retrieveKeyStrokes[0].phoneOrientation, keyStroke2.phoneOrientation)

        // Assert that the second retrieved keystroke matches the expected data
        assertEquals(retrieveKeyStrokes[1].uuid, keyStroke1.uuid)
        assertEquals(retrieveKeyStrokes[1].phoneOrientation, keyStroke1.phoneOrientation)

    }

    // Test to insert multiple keystrokes and retrieve them by session ID
    @Test
    fun insertMultipleKeyStrokeAndRetrieveCount() = runTest {

        database.keyStrokeDao().insertAll(keyStroke1, keyStroke2)

        val retrieveKeyStrokesCount =
            database.keyStrokeDao().getKeyStrokesCountPerSession("session-id").first()

        // Assert
        assertNotNull(retrieveKeyStrokesCount)
        assertEquals(retrieveKeyStrokesCount, 2)
    }

    // Insert multiple keystrokes with different inCorrect field values and assert accuracy is correct
    @Test
    fun insertMultipleKeyStrokeAndRetrieveAccuracy() = runTest {

        // Insert the four keystrokes into the database with different isCorrect values
        database.keyStrokeDao().insertAll(
            keyStroke1.copy(isCorrect = true),
            keyStroke2.copy(isCorrect = true),
            keyStroke3.copy(isCorrect = true),
            keyStroke4.copy(isCorrect = false)
        )

        val accuracy = database.keyStrokeDao().getAccuracyRatePerSession("session-id").first()


        // Assert accuracy calculation is correct
        assertNotNull(accuracy)
        assertEquals(accuracy, 0.75f)
    }

    // Insert no keystrokes, and then accuracy should be 0
    @Test
    fun accuracyShouldBeZeroIfNoKeyStrokesAreInSessionId() = runTest {
        val accuracy = database.keyStrokeDao().getAccuracyRatePerSession("session-id").first()

        // Assert accuracy is 0 in case there's no key strokes in the session
        assertNotNull(accuracy)
        assertEquals(accuracy, 0f)
    }

}

object KeyStrokeTestData {
    val keyStroke1 = KeyStroke(
        uuid = "some-uuid1",
        sessionId = "session-id",
        keyPressTime = 1000L,
        keyReleaseTime = 2000L,
        keyCode = "some-key-code",
        phoneOrientation = ScreenOrientation.PORTRAIT,
        isCorrect = true
    )

    val keyStroke2 = KeyStroke(
        uuid = "some-uuid2",
        sessionId = "session-id",
        keyPressTime = 1000L,
        keyReleaseTime = 2000L,
        keyCode = "some-key-code",
        phoneOrientation = ScreenOrientation.PORTRAIT,
        isCorrect = true
    )

    val keyStroke3 = KeyStroke(
        uuid = "some-uuid3",
        sessionId = "session-id",
        keyPressTime = 1000L,
        keyReleaseTime = 2000L,
        keyCode = "some-key-code",
        phoneOrientation = ScreenOrientation.PORTRAIT,
        isCorrect = true
    )

    val keyStroke4 = KeyStroke(
        uuid = "some-uuid4",
        sessionId = "session-id",
        keyPressTime = 1000L,
        keyReleaseTime = 2000L,
        keyCode = "some-key-code",
        phoneOrientation = ScreenOrientation.PORTRAIT,
        isCorrect = false
    )
}