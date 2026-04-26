package com.fittrack.app.models

/**
 * ExerciseCatalog model representing a predefined exercise
 * Primary key: id (autoincrement)
 * Used to standardize exercise names across workouts
 */
data class ExerciseCatalog(
    val id: Long = 0L,
    val exerciseName: String,
    val muscleGroup: String,
    val description: String? = null,
    val createdAt: String? = null
)

