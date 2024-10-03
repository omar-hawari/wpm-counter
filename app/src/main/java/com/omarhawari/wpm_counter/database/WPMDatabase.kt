package com.omarhawari.wpm_counter.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.omarhawari.wpm_counter.database.daos.KeyStroke
import com.omarhawari.wpm_counter.database.daos.KeyStrokeDao
import com.omarhawari.wpm_counter.database.daos.Session
import com.omarhawari.wpm_counter.database.daos.SessionDao
import com.omarhawari.wpm_counter.database.daos.User
import com.omarhawari.wpm_counter.database.daos.UserDao

const val DATABASE_NAME = "wpm-db"

/**
 * WPMDatabase holds three tables: User, Session, and KeyStroke
 * User: Contains information about each user
 * Session: Contains information about each WPM session. A session is associated with a user
 * KeyStroke: Contains information about KeyStrokes within a session. A keystroke is associated with a session
 * */
@Database(entities = [User::class, Session::class, KeyStroke::class], version = 1)
abstract class WPMDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun sessionDao(): SessionDao
    abstract fun keyStrokeDao(): KeyStrokeDao

}