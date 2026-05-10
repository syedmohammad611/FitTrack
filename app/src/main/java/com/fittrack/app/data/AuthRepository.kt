package com.fittrack.app.data

import android.content.Context
import com.fittrack.app.R
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

/**
 * Firebase Authentication with Kotlin coroutines (no blocking the main thread).
 * F1: Email/password and Google ID token exchange with Firebase Auth.
 */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
) {

    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> = runCatching {
        auth.signInWithEmailAndPassword(email, password).await().user
            ?: error("Sign-in succeeded but user is null")
    }

    suspend fun registerWithEmail(email: String, password: String): Result<FirebaseUser> = runCatching {
        auth.createUserWithEmailAndPassword(email, password).await().user
            ?: error("Registration succeeded but user is null")
    }

    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> = runCatching {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).await().user
            ?: error("Google sign-in succeeded but user is null")
    }

    fun signOut() {
        auth.signOut()
    }

    fun currentUser(): FirebaseUser? = auth.currentUser

    companion object {
        fun googleSignInOptions(context: Context): GoogleSignInOptions {
            val webClientId = context.getString(R.string.default_web_client_id)
            return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build()
        }
    }
}
