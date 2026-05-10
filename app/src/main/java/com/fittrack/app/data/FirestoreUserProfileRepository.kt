package com.fittrack.app.data

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

/**
 * F2: Top-level collection [COL_USER_PROFILES] keyed by Firebase Auth UID.
 * Related to [FirestoreWorkoutRepository] via the same document ID (parent of workout_sessions).
 */
class FirestoreUserProfileRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {

    suspend fun ensureUserProfile(uid: String, displayName: String?, email: String?) {
        val doc = db.collection(COL_USER_PROFILES).document(uid)
        val payload = hashMapOf<String, Any>(
            FIELD_DISPLAY_NAME to (displayName ?: ""),
            FIELD_EMAIL to (email ?: ""),
            FIELD_UPDATED_AT to FieldValue.serverTimestamp(),
        )
        doc.set(payload, SetOptions.merge()).await()
    }

    companion object {
        const val COL_USER_PROFILES = "user_profiles"
        const val FIELD_DISPLAY_NAME = "displayName"
        const val FIELD_EMAIL = "email"
        const val FIELD_UPDATED_AT = "updatedAt"
    }
}
