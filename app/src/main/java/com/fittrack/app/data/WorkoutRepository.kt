package com.fittrack.app.data

import android.content.ContentValues
import android.content.Context
import com.fittrack.app.models.WorkoutSession

class WorkoutRepository(context: Context) {
    private val dbHelper = FitTrackDatabaseHelper(context.applicationContext)

    fun seedIfEmpty() {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val count = db.rawQuery(
                "SELECT COUNT(*) FROM ${FitTrackDatabaseHelper.TABLE_WORKOUT_SESSIONS}",
                null
            ).use { cursor ->
                cursor.moveToFirst()
                cursor.getInt(0)
            }
            if (count == 0) {
                val sessionId = insertSessionInternal(
                    db,
                    WorkoutSession(
                        id = 0,
                        date = "Jun 4",
                        workout = "Push Day - Chest & Triceps",
                        duration = "55m",
                        volumeKg = "3,200"
                    )
                )
                insertExercise(db, sessionId, "Bench Press", 4, 8)
                insertExercise(db, sessionId, "Incline DB Press", 3, 10)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun createSession(session: WorkoutSession): Long {
        return dbHelper.writableDatabase.use { db ->
            insertSessionInternal(db, session)
        }
    }

    fun readSessions(searchQuery: String? = null): List<WorkoutSession> {
        val db = dbHelper.readableDatabase
        val selection: String?
        val selectionArgs: Array<String>?
        if (searchQuery.isNullOrBlank()) {
            selection = null
            selectionArgs = null
        } else {
            selection = "${FitTrackDatabaseHelper.COL_SESSION_NAME} LIKE ?"
            selectionArgs = arrayOf("%$searchQuery%")
        }

        return db.query(
            FitTrackDatabaseHelper.TABLE_WORKOUT_SESSIONS,
            null,
            selection,
            selectionArgs,
            null,
            null,
            "${FitTrackDatabaseHelper.COL_SESSION_ID} DESC"
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    add(
                        WorkoutSession(
                            id = cursor.getLong(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_SESSION_ID)),
                            date = cursor.getString(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_SESSION_DATE)),
                            workout = cursor.getString(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_SESSION_NAME)),
                            duration = cursor.getString(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_SESSION_DURATION)),
                            volumeKg = cursor.getString(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_SESSION_VOLUME))
                        )
                    )
                }
            }
        }
    }

    fun updateSession(session: WorkoutSession): Int {
        val values = ContentValues().apply {
            put(FitTrackDatabaseHelper.COL_SESSION_DATE, session.date)
            put(FitTrackDatabaseHelper.COL_SESSION_NAME, session.workout)
            put(FitTrackDatabaseHelper.COL_SESSION_DURATION, session.duration)
            put(FitTrackDatabaseHelper.COL_SESSION_VOLUME, session.volumeKg)
        }
        return dbHelper.writableDatabase.update(
            FitTrackDatabaseHelper.TABLE_WORKOUT_SESSIONS,
            values,
            "${FitTrackDatabaseHelper.COL_SESSION_ID} = ?",
            arrayOf(session.id.toString())
        )
    }

    fun deleteSession(sessionId: Long): Int {
        return dbHelper.writableDatabase.delete(
            FitTrackDatabaseHelper.TABLE_WORKOUT_SESSIONS,
            "${FitTrackDatabaseHelper.COL_SESSION_ID} = ?",
            arrayOf(sessionId.toString())
        )
    }

    private fun insertSessionInternal(db: android.database.sqlite.SQLiteDatabase, session: WorkoutSession): Long {
        // Get default user ID (demo_user created during initialization)
        val defaultUserId = db.rawQuery(
            "SELECT ${FitTrackDatabaseHelper.COL_USER_ID} FROM ${FitTrackDatabaseHelper.TABLE_USERS} WHERE ${FitTrackDatabaseHelper.COL_USER_USERNAME} = ?",
            arrayOf("demo_user")
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.getLong(0) else 1L
        }

        val values = ContentValues().apply {
            put(FitTrackDatabaseHelper.COL_SESSION_USER_ID, defaultUserId)
            put(FitTrackDatabaseHelper.COL_SESSION_DATE, session.date)
            put(FitTrackDatabaseHelper.COL_SESSION_NAME, session.workout)
            put(FitTrackDatabaseHelper.COL_SESSION_DURATION, session.duration)
            put(FitTrackDatabaseHelper.COL_SESSION_VOLUME, session.volumeKg)
        }
        return db.insert(FitTrackDatabaseHelper.TABLE_WORKOUT_SESSIONS, null, values)
    }

    private fun insertExercise(
        db: android.database.sqlite.SQLiteDatabase,
        sessionId: Long,
        name: String,
        sets: Int,
        reps: Int
    ) {
        val values = ContentValues().apply {
            put(FitTrackDatabaseHelper.COL_EXERCISE_SESSION_ID, sessionId)
            put(FitTrackDatabaseHelper.COL_EXERCISE_NAME, name)
            put(FitTrackDatabaseHelper.COL_EXERCISE_SETS, sets)
            put(FitTrackDatabaseHelper.COL_EXERCISE_REPS, reps)
        }
        db.insert(FitTrackDatabaseHelper.TABLE_WORKOUT_EXERCISES, null, values)
    }
}
