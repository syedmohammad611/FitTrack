package com.fittrack.app.data

import android.content.Context
import com.fittrack.app.models.WorkoutSession
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

class FirestoreWorkoutRepository private constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun ensureUser(username: String) {
        val userDoc = firestore.collection(COLLECTION_USERS).document(username)
        userDoc.set(
            mapOf(
                FIELD_USERNAME to username,
                FIELD_LAST_ACTIVE_AT to FieldValue.serverTimestamp()
            ),
            com.google.firebase.firestore.SetOptions.merge()
        ).await()
    }

    fun observeSessions(
        username: String,
        onChanged: (List<WorkoutSession>) -> Unit,
        onError: (Throwable) -> Unit
    ): ListenerRegistration {
        return firestore.collection(COLLECTION_SESSIONS)
            .whereEqualTo(FIELD_OWNER_USERNAME, username)
            .orderBy(FIELD_UPDATED_AT)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    onError(error)
                    return@addSnapshotListener
                }

                val sessions = snapshots?.documents?.mapNotNull { doc ->
                    val date = doc.getString(FIELD_DATE) ?: return@mapNotNull null
                    val workout = doc.getString(FIELD_WORKOUT) ?: return@mapNotNull null
                    val duration = doc.getString(FIELD_DURATION) ?: return@mapNotNull null
                    val volumeKg = doc.getString(FIELD_VOLUME_KG) ?: return@mapNotNull null

                    WorkoutSession(
                        remoteId = doc.id,
                        date = date,
                        workout = workout,
                        duration = duration,
                        volumeKg = volumeKg
                    )
                }?.reversed().orEmpty()

                onChanged(sessions)
            }
    }

    suspend fun createSession(username: String, session: WorkoutSession) {
        val userRef = firestore.collection(COLLECTION_USERS).document(username)
        firestore.collection(COLLECTION_SESSIONS)
            .add(
                mapOf(
                    FIELD_OWNER_USERNAME to username,
                    FIELD_USER_REF to userRef,
                    FIELD_DATE to session.date,
                    FIELD_WORKOUT to session.workout,
                    FIELD_DURATION to session.duration,
                    FIELD_VOLUME_KG to session.volumeKg,
                    FIELD_CREATED_AT to FieldValue.serverTimestamp(),
                    FIELD_UPDATED_AT to FieldValue.serverTimestamp()
                )
            ).await()
    }

    suspend fun updateSession(username: String, session: WorkoutSession) {
        if (session.remoteId.isBlank()) return

        firestore.collection(COLLECTION_SESSIONS)
            .document(session.remoteId)
            .update(
                mapOf(
                    FIELD_OWNER_USERNAME to username,
                    FIELD_DATE to session.date,
                    FIELD_WORKOUT to session.workout,
                    FIELD_DURATION to session.duration,
                    FIELD_VOLUME_KG to session.volumeKg,
                    FIELD_UPDATED_AT to FieldValue.serverTimestamp()
                )
            ).await()
    }

    suspend fun deleteSession(session: WorkoutSession) {
        if (session.remoteId.isBlank()) return

        firestore.collection(COLLECTION_SESSIONS)
            .document(session.remoteId)
            .delete()
            .await()
    }

    suspend fun ensureLinkedExerciseNode(username: String, sessionId: String) {
        if (sessionId.isBlank()) return

        val exerciseDoc = firestore.collection(COLLECTION_EXERCISES)
            .document("${username}_$sessionId")

        exerciseDoc.set(
            mapOf(
                FIELD_OWNER_USERNAME to username,
                FIELD_SESSION_ID to sessionId,
                FIELD_EXERCISE_NAME to "Demo Exercise",
                FIELD_SETS to 3,
                FIELD_REPS to 10,
                FIELD_WEIGHT_KG to 40,
                FIELD_UPDATED_AT to Timestamp.now()
            ),
            com.google.firebase.firestore.SetOptions.merge()
        ).await()
    }

    companion object {
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_SESSIONS = "workout_sessions"
        private const val COLLECTION_EXERCISES = "workout_exercises"

        private const val FIELD_USERNAME = "username"
        private const val FIELD_LAST_ACTIVE_AT = "lastActiveAt"
        private const val FIELD_OWNER_USERNAME = "ownerUsername"
        private const val FIELD_USER_REF = "userRef"
        private const val FIELD_DATE = "date"
        private const val FIELD_WORKOUT = "workout"
        private const val FIELD_DURATION = "duration"
        private const val FIELD_VOLUME_KG = "volumeKg"
        private const val FIELD_CREATED_AT = "createdAt"
        private const val FIELD_UPDATED_AT = "updatedAt"

        private const val FIELD_SESSION_ID = "sessionId"
        private const val FIELD_EXERCISE_NAME = "exerciseName"
        private const val FIELD_SETS = "sets"
        private const val FIELD_REPS = "reps"
        private const val FIELD_WEIGHT_KG = "weightKg"

        fun createIfConfigured(context: Context): FirestoreWorkoutRepository? {
            val app = FirebaseApp.initializeApp(context.applicationContext) ?: return null
            return FirestoreWorkoutRepository(FirebaseFirestore.getInstance(app))
        }
    }
}

