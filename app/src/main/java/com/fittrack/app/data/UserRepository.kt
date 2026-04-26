package com.fittrack.app.data

import android.content.ContentValues
import android.content.Context
import com.fittrack.app.models.User

/**
 * UserRepository handles all CRUD operations for Users table
 * Uses SQLiteOpenHelper (FitTrackDatabaseHelper) for data persistence
 */
class UserRepository(context: Context) {
    private val dbHelper = FitTrackDatabaseHelper(context.applicationContext)

    /**
     * Create a new user
     * @return User ID (primary key)
     */
    fun createUser(user: User): Long {
        val values = ContentValues().apply {
            put(FitTrackDatabaseHelper.COL_USER_USERNAME, user.username)
            put(FitTrackDatabaseHelper.COL_USER_EMAIL, user.email)
            put(FitTrackDatabaseHelper.COL_USER_CREATED_AT, java.time.LocalDateTime.now().toString())
        }
        return dbHelper.writableDatabase.insert(
            FitTrackDatabaseHelper.TABLE_USERS,
            null,
            values
        )
    }

    /**
     * Read user by ID
     */
    fun getUserById(userId: Long): User? {
        return dbHelper.readableDatabase.query(
            FitTrackDatabaseHelper.TABLE_USERS,
            null,
            "${FitTrackDatabaseHelper.COL_USER_ID} = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                User(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_USER_ID)),
                    username = cursor.getString(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_USER_USERNAME)),
                    email = cursor.getString(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_USER_EMAIL)),
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_USER_CREATED_AT))
                )
            } else {
                null
            }
        }
    }

    /**
     * Read user by username
     */
    fun getUserByUsername(username: String): User? {
        return dbHelper.readableDatabase.query(
            FitTrackDatabaseHelper.TABLE_USERS,
            null,
            "${FitTrackDatabaseHelper.COL_USER_USERNAME} = ?",
            arrayOf(username),
            null,
            null,
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                User(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_USER_ID)),
                    username = cursor.getString(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_USER_USERNAME)),
                    email = cursor.getString(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_USER_EMAIL)),
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_USER_CREATED_AT))
                )
            } else {
                null
            }
        }
    }

    /**
     * Read all users
     */
    fun getAllUsers(): List<User> {
        return dbHelper.readableDatabase.query(
            FitTrackDatabaseHelper.TABLE_USERS,
            null,
            null,
            null,
            null,
            null,
            "${FitTrackDatabaseHelper.COL_USER_ID} DESC"
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    add(
                        User(
                            id = cursor.getLong(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_USER_ID)),
                            username = cursor.getString(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_USER_USERNAME)),
                            email = cursor.getString(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_USER_EMAIL)),
                            createdAt = cursor.getString(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_USER_CREATED_AT))
                        )
                    )
                }
            }
        }
    }

    /**
     * Update user
     */
    fun updateUser(user: User): Int {
        val values = ContentValues().apply {
            put(FitTrackDatabaseHelper.COL_USER_USERNAME, user.username)
            put(FitTrackDatabaseHelper.COL_USER_EMAIL, user.email)
        }
        return dbHelper.writableDatabase.update(
            FitTrackDatabaseHelper.TABLE_USERS,
            values,
            "${FitTrackDatabaseHelper.COL_USER_ID} = ?",
            arrayOf(user.id.toString())
        )
    }

    /**
     * Delete user (cascades to related sessions and exercises)
     */
    fun deleteUser(userId: Long): Int {
        return dbHelper.writableDatabase.delete(
            FitTrackDatabaseHelper.TABLE_USERS,
            "${FitTrackDatabaseHelper.COL_USER_ID} = ?",
            arrayOf(userId.toString())
        )
    }
}

