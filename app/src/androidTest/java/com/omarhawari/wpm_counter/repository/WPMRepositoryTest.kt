package com.omarhawari.wpm_counter.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.omarhawari.wpm_counter.database.WPMDatabase
import com.omarhawari.wpm_counter.database.WPMRepositoryDBImpl
import com.omarhawari.wpm_counter.database.daos.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class WPMRepositoryTest {

    private lateinit var database: WPMDatabase
    private lateinit var repo: WPMRepositoryDBImpl

    @Before
    fun setUp() {

        // Init database
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WPMDatabase::class.java
        ).allowMainThreadQueries().build()

        // Create the repository with the database
        repo = WPMRepositoryDBImpl(database)
    }


    @Test
    fun addUserWhenItNotExists() = runTest {

        val userName = "testUser"

        val addedUser = repo.insertUserIfNotExists(userName)

        assertNotNull(addedUser)
        assertEquals(userName, addedUser.username)
    }

    @Test
    fun returnExistingUserIfItExists() = runTest {

        val userName = "testUser"

        val existingUser = User(
            uuid = "some-uuid",
            username = userName,
            createdTime = System.currentTimeMillis()
        )

        database.userDao().insertAll(existingUser)

        val addedUser = repo.insertUserIfNotExists(userName)

        assertNotNull(addedUser)
        assertEquals(addedUser.uuid, existingUser.uuid)
    }

    @Test
    fun testGetUsersWithHighScore() = runTest {

        // Insert users
        val user1 = repo.insertUserIfNotExists("username1")
        val user2 = repo.insertUserIfNotExists("username2")

        // Insert multiple sessions per user with WPM scores
        repo.updateSession(repo.insertSession(user1.uuid).copy(wordPerMinute = 100f))
        repo.updateSession(repo.insertSession(user1.uuid).copy(wordPerMinute = 50f))

        repo.updateSession(repo.insertSession(user2.uuid).copy(wordPerMinute = 120f))
        repo.updateSession(repo.insertSession(user2.uuid).copy(wordPerMinute = 10f))

        // Execute the function
        val result = repo.getUsersWithHighScore().first()

        // Assertions
        assertEquals(2, result.size)
        assertEquals(120f, result[0].highScore) // the highest score here is 120f
        assertEquals(user2.uuid, result[0].uuid) // the highest score should be for username2
        assertEquals(100f, result[1].highScore) // the second highest score is 100f
        assertEquals(user1.uuid, result[1].uuid) // the second highest score should be for username1
    }

}
