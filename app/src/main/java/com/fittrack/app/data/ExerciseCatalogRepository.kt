package com.fittrack.app.data

import android.content.ContentValues
import android.content.Context
import com.fittrack.app.models.ExerciseCatalog

/**
 * ExerciseCatalogRepository handles all CRUD operations for Exercise Catalog table
 * Uses SQLiteOpenHelper (FitTrackDatabaseHelper) for data persistence
 */
class ExerciseCatalogRepository(context: Context) {
    private val dbHelper = FitTrackDatabaseHelper(context.applicationContext)

    /**
     * Add exercise to catalog
     * @return Exercise ID (primary key)
     */
    fun addExerciseToCatalog(exercise: ExerciseCatalog): Long {
        val values = ContentValues().apply {
            put(FitTrackDatabaseHelper.COL_CATALOG_EXERCISE_NAME, exercise.exerciseName)
            put(FitTrackDatabaseHelper.COL_CATALOG_EXERCISE_MUSCLE_GROUP, exercise.muscleGroup)
            put(FitTrackDatabaseHelper.COL_CATALOG_EXERCISE_DESCRIPTION, exercise.description)
            put(FitTrackDatabaseHelper.COL_CATALOG_EXERCISE_CREATED_AT, java.time.LocalDateTime.now().toString())
        }
        return dbHelper.writableDatabase.insert(
            FitTrackDatabaseHelper.TABLE_EXERCISE_CATALOG,
            null,
            values
        )
    }

    /**
     * Get exercise from catalog by ID
     */
    fun getExerciseById(exerciseId: Long): ExerciseCatalog? {
        return dbHelper.readableDatabase.query(
            FitTrackDatabaseHelper.TABLE_EXERCISE_CATALOG,
            null,
            "${FitTrackDatabaseHelper.COL_CATALOG_EXERCISE_ID} = ?",
            arrayOf(exerciseId.toString()),
            null,
            null,
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                ExerciseCatalog(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_CATALOG_EXERCISE_ID)),
                    exerciseName = cursor.getString(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_CATALOG_EXERCISE_NAME)),
                    muscleGroup = cursor.getString(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_CATALOG_EXERCISE_MUSCLE_GROUP)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_CATALOG_EXERCISE_DESCRIPTION)),
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_CATALOG_EXERCISE_CREATED_AT))
                )
            } else {
                null
            }
        }
    }

    /**
     * Get exercises by muscle group
     */
    fun getExercisesByMuscleGroup(muscleGroup: String): List<ExerciseCatalog> {
        return dbHelper.readableDatabase.query(
            FitTrackDatabaseHelper.TABLE_EXERCISE_CATALOG,
            null,
            "${FitTrackDatabaseHelper.COL_CATALOG_EXERCISE_MUSCLE_GROUP} = ?",
            arrayOf(muscleGroup),
            null,
            null,
            "${FitTrackDatabaseHelper.COL_CATALOG_EXERCISE_NAME} ASC"
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    add(
                        ExerciseCatalog(
                            id = cursor.getLong(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_CATALOG_EXERCISE_ID)),
                            exerciseName = cursor.getString(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_CATALOG_EXERCISE_NAME)),
                            muscleGroup = cursor.getString(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_CATALOG_EXERCISE_MUSCLE_GROUP)),
                            description = cursor.getString(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_CATALOG_EXERCISE_DESCRIPTION)),
                            createdAt = cursor.getString(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_CATALOG_EXERCISE_CREATED_AT))
                        )
                    )
                }
            }
        }
    }

    /**
     * Get all exercises from catalog
     */
    fun getAllExercises(): List<ExerciseCatalog> {
        return dbHelper.readableDatabase.query(
            FitTrackDatabaseHelper.TABLE_EXERCISE_CATALOG,
            null,
            null,
            null,
            null,
            null,
            "${FitTrackDatabaseHelper.COL_CATALOG_EXERCISE_NAME} ASC"
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    add(
                        ExerciseCatalog(
                            id = cursor.getLong(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_CATALOG_EXERCISE_ID)),
                            exerciseName = cursor.getString(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_CATALOG_EXERCISE_NAME)),
                            muscleGroup = cursor.getString(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_CATALOG_EXERCISE_MUSCLE_GROUP)),
                            description = cursor.getString(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_CATALOG_EXERCISE_DESCRIPTION)),
                            createdAt = cursor.getString(cursor.getColumnIndexOrThrow(FitTrackDatabaseHelper.COL_CATALOG_EXERCISE_CREATED_AT))
                        )
                    )
                }
            }
        }
    }

    /**
     * Update exercise in catalog
     */
    fun updateExercise(exercise: ExerciseCatalog): Int {
        val values = ContentValues().apply {
            put(FitTrackDatabaseHelper.COL_CATALOG_EXERCISE_NAME, exercise.exerciseName)
            put(FitTrackDatabaseHelper.COL_CATALOG_EXERCISE_MUSCLE_GROUP, exercise.muscleGroup)
            put(FitTrackDatabaseHelper.COL_CATALOG_EXERCISE_DESCRIPTION, exercise.description)
        }
        return dbHelper.writableDatabase.update(
            FitTrackDatabaseHelper.TABLE_EXERCISE_CATALOG,
            values,
            "${FitTrackDatabaseHelper.COL_CATALOG_EXERCISE_ID} = ?",
            arrayOf(exercise.id.toString())
        )
    }

    /**
     * Delete exercise from catalog
     */
    fun deleteExercise(exerciseId: Long): Int {
        return dbHelper.writableDatabase.delete(
            FitTrackDatabaseHelper.TABLE_EXERCISE_CATALOG,
            "${FitTrackDatabaseHelper.COL_CATALOG_EXERCISE_ID} = ?",
            arrayOf(exerciseId.toString())
        )
    }

    /**
     * Seed catalog with common exercises (called once on first app run)
     */
    fun seedCatalogIfEmpty() {
        val db = dbHelper.writableDatabase
        val count = db.rawQuery(
            "SELECT COUNT(*) FROM ${FitTrackDatabaseHelper.TABLE_EXERCISE_CATALOG}",
            null
        ).use { cursor ->
            cursor.moveToFirst()
            cursor.getInt(0)
        }

        if (count == 0) {
            val commonExercises = listOf(
                ExerciseCatalog(0, "Bench Press", "Chest"),
                ExerciseCatalog(0, "Incline Dumbbell Press", "Chest"),
                ExerciseCatalog(0, "Barbell Squat", "Legs"),
                ExerciseCatalog(0, "Leg Press", "Legs"),
                ExerciseCatalog(0, "Deadlift", "Back"),
                ExerciseCatalog(0, "Bent Over Row", "Back"),
                ExerciseCatalog(0, "Shoulder Press", "Shoulders"),
                ExerciseCatalog(0, "Lateral Raise", "Shoulders"),
                ExerciseCatalog(0, "Barbell Curl", "Arms"),
                ExerciseCatalog(0, "Tricep Dips", "Arms"),
                ExerciseCatalog(0, "Pull-ups", "Back"),
                ExerciseCatalog(0, "Push-ups", "Chest")
            )
            commonExercises.forEach { addExerciseToCatalog(it) }
        }
    }
}

