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
        // Table 1: Users (stores app user accounts)
        db.execSQL(
            """
            CREATE TABLE $TABLE_USERS (
                $COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USER_USERNAME TEXT NOT NULL UNIQUE,
                $COL_USER_EMAIL TEXT,
                $COL_USER_CREATED_AT TEXT NOT NULL
            )
            """.trimIndent()
        )

        // Table 2: Workout Sessions (logs user workout sessions)
        db.execSQL(
            """
            CREATE TABLE $TABLE_WORKOUT_SESSIONS (
                $COL_SESSION_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_SESSION_USER_ID INTEGER NOT NULL,
                $COL_SESSION_DATE TEXT NOT NULL,
                $COL_SESSION_NAME TEXT NOT NULL,
                $COL_SESSION_DURATION TEXT NOT NULL,
                $COL_SESSION_VOLUME TEXT NOT NULL,
                FOREIGN KEY($COL_SESSION_USER_ID) REFERENCES $TABLE_USERS($COL_USER_ID) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        // Table 3: Exercise Catalog (predefined exercises)
        db.execSQL(
            """
            CREATE TABLE $TABLE_EXERCISE_CATALOG (
                $COL_CATALOG_EXERCISE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_CATALOG_EXERCISE_NAME TEXT NOT NULL UNIQUE,
                $COL_CATALOG_EXERCISE_MUSCLE_GROUP TEXT NOT NULL,
                $COL_CATALOG_EXERCISE_DESCRIPTION TEXT,
                $COL_CATALOG_EXERCISE_CREATED_AT TEXT NOT NULL
            )
            """.trimIndent()
        )

        // Table 4: Workout Exercises (exercises within a session, references both session and catalog)
        db.execSQL(
            """
            CREATE TABLE $TABLE_WORKOUT_EXERCISES (
                $COL_EXERCISE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_EXERCISE_SESSION_ID INTEGER NOT NULL,
                $COL_EXERCISE_CATALOG_ID INTEGER,
                $COL_EXERCISE_NAME TEXT NOT NULL,
                $COL_EXERCISE_SETS INTEGER NOT NULL,
                $COL_EXERCISE_REPS INTEGER NOT NULL,
                $COL_EXERCISE_WEIGHT_KG REAL,
                $COL_EXERCISE_CREATED_AT TEXT NOT NULL,
                FOREIGN KEY($COL_EXERCISE_SESSION_ID) REFERENCES $TABLE_WORKOUT_SESSIONS($COL_SESSION_ID) ON DELETE CASCADE,
                FOREIGN KEY($COL_EXERCISE_CATALOG_ID) REFERENCES $TABLE_EXERCISE_CATALOG($COL_CATALOG_EXERCISE_ID) ON DELETE SET NULL
            )
            """.trimIndent()
        )

        // Seed Users table with default user
        seedDefaultUser(db)
    }

    private fun seedDefaultUser(db: SQLiteDatabase) {
        db.execSQL(
            """
            INSERT INTO $TABLE_USERS ($COL_USER_USERNAME, $COL_USER_EMAIL, $COL_USER_CREATED_AT)
            VALUES ('demo_user', 'demo@fittrack.app', datetime('now'))
            """
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop tables in reverse dependency order (FK constraints)
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORKOUT_EXERCISES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORKOUT_SESSIONS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXERCISE_CATALOG")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "fittrack.db"
        const val DATABASE_VERSION = 1

        // Table: users
        const val TABLE_USERS = "users"
        const val COL_USER_ID = "id"
        const val COL_USER_USERNAME = "username"
        const val COL_USER_EMAIL = "email"
        const val COL_USER_CREATED_AT = "created_at"

        // Table: workout_sessions
        const val TABLE_WORKOUT_SESSIONS = "workout_sessions"
        const val COL_SESSION_ID = "id"
        const val COL_SESSION_USER_ID = "user_id"
        const val COL_SESSION_DATE = "session_date"
        const val COL_SESSION_NAME = "session_name"
        const val COL_SESSION_DURATION = "duration_minutes"
        const val COL_SESSION_VOLUME = "volume_kg"

        // Table: exercise_catalog
        const val TABLE_EXERCISE_CATALOG = "exercise_catalog"
        const val COL_CATALOG_EXERCISE_ID = "id"
        const val COL_CATALOG_EXERCISE_NAME = "exercise_name"
        const val COL_CATALOG_EXERCISE_MUSCLE_GROUP = "muscle_group"
        const val COL_CATALOG_EXERCISE_DESCRIPTION = "description"
        const val COL_CATALOG_EXERCISE_CREATED_AT = "created_at"

        // Table: workout_exercises
        const val TABLE_WORKOUT_EXERCISES = "workout_exercises"
        const val COL_EXERCISE_ID = "id"
        const val COL_EXERCISE_SESSION_ID = "session_id"
        const val COL_EXERCISE_CATALOG_ID = "catalog_exercise_id"
        const val COL_EXERCISE_NAME = "exercise_name"
        const val COL_EXERCISE_SETS = "sets_count"
        const val COL_EXERCISE_REPS = "reps_count"
        const val COL_EXERCISE_WEIGHT_KG = "weight_kg"
        const val COL_EXERCISE_CREATED_AT = "created_at"
    }
}
