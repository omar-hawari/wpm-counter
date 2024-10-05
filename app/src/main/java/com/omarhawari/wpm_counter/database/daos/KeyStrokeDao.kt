package com.omarhawari.wpm_counter.database.daos

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

const val TABLE_NAME_KEYSTROKE = "TABLE_NAME_KEYSTROKE"

// Entity representing a keystroke within a session
@Entity(tableName = TABLE_NAME_KEYSTROKE)
data class KeyStroke(
    @PrimaryKey val uuid: String, // id for the keystroke
    @ColumnInfo(name = "session_id") val sessionId: String, // ID of the session
    @ColumnInfo(name = "keyPressTime") val keyPressTime: Long? = null, // Time when the key was pressed
    @ColumnInfo(name = "keyReleaseTime") val keyReleaseTime: Long, // Time when the key was released
    @ColumnInfo(name = "keyCode") val keyCode: String, // Code representing the key
    @ColumnInfo(name = "phoneOrientation") val phoneOrientation: ScreenOrientation, // Phone's orientation during the keystroke
    @ColumnInfo(name = "isCorrect") val isCorrect: Boolean, // Whether the key was correct in comparison to the text
)

@Dao
interface KeyStrokeDao {

    // Inserts one or more keystrokes into the database
    @Insert
    suspend fun insertAll(vararg keyStrokes: KeyStroke)

    // Retrieves all keystrokes for a given session, ordered by the key release time in descending order
    @Query("SELECT * FROM $TABLE_NAME_KEYSTROKE WHERE session_id = :sessionId ORDER BY keyReleaseTime DESC")
    fun getKeyStrokesPerSession(sessionId: String): Flow<List<KeyStroke>>

    // Retrieves the count of keystrokes for a given session as a Flow for real-time updates
    @Query("SELECT COUNT (*) FROM $TABLE_NAME_KEYSTROKE WHERE session_id = :sessionId")
    fun getKeyStrokesCountPerSession(sessionId: String): Flow<Int>

    // Calculates the accuracy rate for a session, returning the ratio of correct keystrokes to total keystrokes as a Flow
    @Query("""
        SELECT
            CASE 
                WHEN COUNT(*) = 0 THEN 0 
                ELSE COUNT(*) * 1.0 / (SELECT COUNT(*) FROM $TABLE_NAME_KEYSTROKE WHERE session_id = :sessionId) 
            END AS ratio 
        FROM $TABLE_NAME_KEYSTROKE 
        WHERE session_id = :sessionId AND isCorrect = 1
    """)
    fun getAccuracyRatePerSession(sessionId: String): Flow<Float>
}

// Enum representing the screen orientation during a keystroke
enum class ScreenOrientation {
    PORTRAIT,
    LANDSCAPE
}
