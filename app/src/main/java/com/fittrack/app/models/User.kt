package com.fittrack.app.models

/**
 * User model representing a FitTrack application user account
 * Primary key: id (autoincrement)
 */
data class User(
    val id: Long = 0L,
    val username: String,
    val email: String? = null,
    val createdAt: String? = null
)

