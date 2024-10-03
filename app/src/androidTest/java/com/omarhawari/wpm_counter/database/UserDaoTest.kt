package com.omarhawari.wpm_counter.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.omarhawari.wpm_counter.database.daos.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UserDaoTest {

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
    fun insertAndGetUserByUsername() = runTest {
        val user = User(
            uuid = "some-uuid",
            username = "testUser",
            createdTime = 1000L
        )
        database.userDao().insertAll(user)

        val retrieveUser = database.userDao().getUserByUsername("testUser")

        // Assert
        assertNotNull(retrieveUser)
        assertEquals(user.uuid, retrieveUser?.uuid)
        assertEquals(user.username, retrieveUser?.username)
        assertEquals(user.createdTime, retrieveUser?.createdTime)
    }

    @Test
    fun insertAndGetUserByUserId() = runTest {
        val user = User(
            uuid = "some-uuid",
            username = "testUser",
            createdTime = 1000L
        )
        database.userDao().insertAll(user)

        val retrieveUser = database.userDao().getUserByUserId("some-uuid")

        // Assert
        assertNotNull(retrieveUser)
        assertEquals(user.uuid, retrieveUser?.uuid)
        assertEquals(user.username, retrieveUser?.username)
        assertEquals(user.createdTime, retrieveUser?.createdTime)
    }

    @Test
    fun insertMultipleUsersAndRetrieveThem() = runTest {
        val user1 = User(
            uuid = "some-uuid1",
            username = "testUser1",
            createdTime = 1000L
        )
        val user2 = User(
            uuid = "some-uuid2",
            username = "testUser2",
            createdTime = 2000L
        )
        val user3 = User(
            uuid = "some-uuid3",
            username = "testUser3",
            createdTime = 3000L
        )

        database.userDao().insertAll(user1, user2, user3)

        val users = database.userDao().getUsers().first()

        // Assert
        assertNotNull(users)

        // Assert the number of users is correct
        assertEquals(users.size, 3)

        // Assert the order of the users is by createdTime DESC
        assertEquals(users[0].uuid, user3.uuid)
        assertEquals(users[1].uuid, user2.uuid)
        assertEquals(users[2].uuid, user1.uuid)
    }

}