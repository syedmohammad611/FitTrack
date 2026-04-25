package com.fittrack.app.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class FitTrackDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_WORKOUT_SESSIONS (
                $COL_SESSION_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_SESSION_DATE TEXT NOT NULL,
                $COL_SESSION_NAME TEXT NOT NULL,
                $COL_SESSION_DURATION TEXT NOT NULL,
                $COL_SESSION_VOLUME TEXT NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE $TABLE_WORKOUT_EXERCISES (
                $COL_EXERCISE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_EXERCISE_SESSION_ID INTEGER NOT NULL,
                $COL_EXERCISE_NAME TEXT NOT NULL,
                $COL_EXERCISE_SETS INTEGER NOT NULL,
                $COL_EXERCISE_REPS INTEGER NOT NULL,
                FOREIGN KEY($COL_EXERCISE_SESSION_ID) REFERENCES $TABLE_WORKOUT_SESSIONS($COL_SESSION_ID) ON DELETE CASCADE
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORKOUT_EXERCISES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORKOUT_SESSIONS")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "fittrack.db"
        const val DATABASE_VERSION = 1

        const val TABLE_WORKOUT_SESSIONS = "workout_sessions"
        const val COL_SESSION_ID = "id"
        const val COL_SESSION_DATE = "session_date"
        const val COL_SESSION_NAME = "session_name"
        const val COL_SESSION_DURATION = "duration_minutes"
        const val COL_SESSION_VOLUME = "volume_kg"

        const val TABLE_WORKOUT_EXERCISES = "workout_exercises"
        const val COL_EXERCISE_ID = "id"
        const val COL_EXERCISE_SESSION_ID = "session_id"
        const val COL_EXERCISE_NAME = "exercise_name"
        const val COL_EXERCISE_SETS = "sets_count"
        const val COL_EXERCISE_REPS = "reps_count"
    }
}
