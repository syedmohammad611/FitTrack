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
                insertSessionInternal(db, WorkoutSession(0, null, "Jun 4", "Push Day - Chest", "55m", "3200"))
                insertSessionInternal(db, WorkoutSession(0, null, "Jun 5", "Pull Day - Back", "60m", "2800"))
                insertSessionInternal(db, WorkoutSession(0, null, "Jun 6", "Leg Day - Quads", "70m", "4500"))
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

    /**
     * F5: Dynamic SQL Queries
     * Executes queries with dynamic selection (LIKE) and dynamic ordering (ORDER BY)
     */
    fun readSessions(
        searchQuery: String? = null,
        sortBy: String = "id DESC"
    ): List<WorkoutSession> {
        val db = dbHelper.readableDatabase
        
        var selection: String? = null
        var selectionArgs: Array<String>? = null

        if (!searchQuery.isNullOrBlank()) {
            // Dynamic Filtering using LIKE
            selection = "${FitTrackDatabaseHelper.COL_SESSION_NAME} LIKE ?"
            selectionArgs = arrayOf("%$searchQuery%")
        }

        // Dynamic Sorting using ORDER BY
        return db.query(
            FitTrackDatabaseHelper.TABLE_WORKOUT_SESSIONS,
            null,
            selection,
            selectionArgs,
            null,
            null,
            sortBy
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    add(
                        WorkoutSession(
                            id = cursor.getLong(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_SESSION_ID)),
                            firestoreId = null,
                            date = cursor.getString(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_SESSION_DATE)),
                            workout = cursor.getString(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_SESSION_NAME)),
                            duration = cursor.getString(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_SESSION_DURATION)),
                            volumeKg = cursor.getString(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_SESSION_VOLUME)),
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
        val values = ContentValues().apply {
            put(FitTrackDatabaseHelper.COL_SESSION_USER_ID, 1) // Default demo user
            put(FitTrackDatabaseHelper.COL_SESSION_DATE, session.date)
            put(FitTrackDatabaseHelper.COL_SESSION_NAME, session.workout)
            put(FitTrackDatabaseHelper.COL_SESSION_DURATION, session.duration)
            put(FitTrackDatabaseHelper.COL_SESSION_VOLUME, session.volumeKg)
        }
        return db.insert(FitTrackDatabaseHelper.TABLE_WORKOUT_SESSIONS, null, values)
    }
}
