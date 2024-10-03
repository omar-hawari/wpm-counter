package com.omarhawari.wpm_counter.database.daos

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

const val TABLE_NAME_SESSION = "TABLE_NAME_SESSION"

// Entity representing a session in the database
@Entity(tableName = TABLE_NAME_SESSION)
data class Session(
    @PrimaryKey val uuid: String, // id for each session
    @ColumnInfo(name = "userId") val userId: String, // ID of the user owning the session
    @ColumnInfo(name = "sessionStartTime") val sessionStartTime: Long? = null, // Session start time
    @ColumnInfo(name = "sessionEndTime") val sessionEndTime: Long? = null, // Session end time
    @ColumnInfo(name = "wordPerMinute") val wordPerMinute: Float? = null // WPM score for the session
)

@Dao
interface SessionDao {

    // Inserts one or more sessions into the database
    @Insert
    suspend fun insertAll(vararg sessions: Session)

    // Updates an existing session in the database
    @Update
    suspend fun updateSession(session: Session)

    // Retrieves a session by its uuid
    @Query("SELECT * FROM $TABLE_NAME_SESSION WHERE uuid = :sessionId")
    suspend fun getSession(sessionId: String): Session

    // Retrieves all sessions for a user, ordered by end time in descending order
    @Query("SELECT * FROM $TABLE_NAME_SESSION WHERE userId = :userId ORDER BY sessionEndTime DESC")
    suspend fun getSessionsForUserIdDescTime(userId: String): List<Session>

    // Retrieves all sessions for a user, ordered by WPM in descending order
    @Query("SELECT * FROM $TABLE_NAME_SESSION WHERE userId = :userId ORDER BY wordPerMinute DESC")
    suspend fun getSessionsForUserIdDescWpm(userId: String): List<Session>

    // Retrieves the highest WPM session for a user. Useful for displaying high scores
    @Query("SELECT * FROM $TABLE_NAME_SESSION WHERE userId = :userId ORDER BY wordPerMinute DESC LIMIT 1")
    suspend fun getHighScoreSession(userId: String): Session?
}