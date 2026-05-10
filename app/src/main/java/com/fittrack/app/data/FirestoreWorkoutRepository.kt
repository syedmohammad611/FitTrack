package com.fittrack.app.data

import com.fittrack.app.models.WorkoutSession
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

/**
 * F2: Subcollection [COL_WORKOUT_SESSIONS] under each user profile document.
 * Real-time [observeWorkoutSessions] pushes updates whenever data changes on the server
 * (including edits from another signed-in device for the same account).
 */
class FirestoreWorkoutRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {

    private fun sessionsCollection(uid: String) = db
        .collection(FirestoreUserProfileRepository.COL_USER_PROFILES)
        .document(uid)
        .collection(COL_WORKOUT_SESSIONS)

    fun observeWorkoutSessions(
        uid: String,
        onUpdate: (List<WorkoutSession>) -> Unit,
        onError: (Throwable) -> Unit,
    ): ListenerRegistration {
        return sessionsCollection(uid).addSnapshotListener { snapshot, error ->
            if (error != null) {
                onError(error)
                return@addSnapshotListener
            }
            val docs = snapshot?.documents.orEmpty()
            val sessions = docs.map { doc ->
                WorkoutSession(
                    id = doc.id.hashCode().toLong(),
                    firestoreId = doc.id,
                    date = doc.getString(FIELD_DATE).orEmpty(),
                    workout = doc.getString(FIELD_WORKOUT).orEmpty(),
                    duration = doc.getString(FIELD_DURATION).orEmpty(),
                    volumeKg = doc.getString(FIELD_VOLUME_KG).orEmpty(),
                )
            }
            onUpdate(sessions)
        }
    }

    suspend fun addSession(uid: String, session: WorkoutSession) {
        val data = hashMapOf<String, Any>(
            FIELD_DATE to session.date,
            FIELD_WORKOUT to session.workout,
            FIELD_DURATION to session.duration,
            FIELD_VOLUME_KG to session.volumeKg,
            FIELD_UPDATED_AT to FieldValue.serverTimestamp(),
        )
        sessionsCollection(uid).add(data).await()
    }

    suspend fun updateSession(uid: String, session: WorkoutSession) {
        val docId = session.firestoreId ?: error("Missing Firestore document id")
        val data = hashMapOf<String, Any>(
            FIELD_DATE to session.date,
            FIELD_WORKOUT to session.workout,
            FIELD_DURATION to session.duration,
            FIELD_VOLUME_KG to session.volumeKg,
            FIELD_UPDATED_AT to FieldValue.serverTimestamp(),
        )
        sessionsCollection(uid).document(docId).update(data).await()
    }

    suspend fun deleteSession(uid: String, firestoreId: String) {
        sessionsCollection(uid).document(firestoreId).delete().await()
    }

    companion object {
        const val COL_WORKOUT_SESSIONS = "workout_sessions"
        const val FIELD_DATE = "date"
        const val FIELD_WORKOUT = "workout"
        const val FIELD_DURATION = "duration"
        const val FIELD_VOLUME_KG = "volumeKg"
        const val FIELD_UPDATED_AT = "updatedAt"
    }
}
