package com.omarhawari.wpm_counter.database.daos

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

const val TABLE_NAME_USER = "TABLE_NAME_USER"

// Entity representing a user in the database
@Entity(tableName = TABLE_NAME_USER)
data class User(
    @PrimaryKey val uuid: String, // Id for each user
    @ColumnInfo(name = "username") val username: String, // Username for the user
    @ColumnInfo(name = "createdTime") val createdTime: Long, // Timestamp of user creation
    var highScore: Float? = null // User's high score (not stored in the database directly, to be queried seperately)
)

@Dao
interface UserDao {

    // Inserts one or more users into the database
    @Insert
    suspend fun insertAll(vararg users: User)

    // Retrieves a list of users ordered by their creation time in descending order
    @Query("SELECT * FROM $TABLE_NAME_USER ORDER BY createdTime DESC")
    fun getUsers(): Flow<List<User>>

    // Retrieves a user by username, ignoring case sensitivity
    @Query("SELECT * FROM $TABLE_NAME_USER WHERE LOWER(username) = LOWER(:username)")
    fun getUserByUsername(username: String): User?

    // Retrieves a user by their unique uuid
    @Query("SELECT * FROM $TABLE_NAME_USER WHERE uuid = :userId")
    fun getUserByUserId(userId: String): User?
}