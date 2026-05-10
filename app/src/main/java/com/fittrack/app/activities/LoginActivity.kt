package com.fittrack.app.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.fittrack.app.R
import com.fittrack.app.data.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val authRepository = AuthRepository()

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        val data = result.data ?: return@registerForActivityResult
        lifecycleScope.launch {
            setLoading(true)
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken.isNullOrBlank()) {
                    Toast.makeText(
                        this@LoginActivity,
                        R.string.error_google_no_token,
                        Toast.LENGTH_LONG,
                    ).show()
                    return@launch
                }
                authRepository.signInWithGoogle(idToken)
                    .onSuccess { user -> navigateToMain(user) }
                    .onFailure { e ->
                        Toast.makeText(
                            this@LoginActivity,
                            e.message ?: getString(R.string.error_auth_generic),
                            Toast.LENGTH_LONG,
                        ).show()
                    }
            } catch (e: ApiException) {
                Toast.makeText(
                    this@LoginActivity,
                    e.message ?: getString(R.string.error_google_failed),
                    Toast.LENGTH_LONG,
                ).show()
            } finally {
                setLoading(false)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLoginEmail = findViewById<Button>(R.id.btnLoginEmail)
        val btnGoogle = findViewById<Button>(R.id.btnGoogleSignIn)
        val tvRegister = findViewById<TextView>(R.id.tvGoRegister)

        btnLoginEmail.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            if (email.isEmpty()) {
                etEmail.error = getString(R.string.error_email_required)
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                etPassword.error = getString(R.string.error_password_required)
                return@setOnClickListener
            }

            lifecycleScope.launch {
                setLoading(true)
                authRepository.signInWithEmail(email, password)
                    .onSuccess { user -> navigateToMain(user) }
                    .onFailure { e ->
                        val msg = (e as? FirebaseAuthException)?.message ?: e.message
                            ?: getString(R.string.error_auth_generic)
                        Toast.makeText(this@LoginActivity, msg, Toast.LENGTH_LONG).show()
                    }
                setLoading(false)
            }
        }

        btnGoogle.setOnClickListener {
            val webClientId = getString(R.string.default_web_client_id)
            if (webClientId.isBlank() || webClientId.contains("PASTE_")) {
                Toast.makeText(
                    this,
                    R.string.error_configure_web_client_id,
                    Toast.LENGTH_LONG,
                ).show()
                return@setOnClickListener
            }
            val gso = AuthRepository.googleSignInOptions(this)
            val client = GoogleSignIn.getClient(this, gso)
            googleSignInLauncher.launch(client.signInIntent)
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    override fun onStart() {
        super.onStart()
        authRepository.currentUser()?.let { navigateToMain(it) }
    }

    private fun setLoading(loading: Boolean) {
        findViewById<ProgressBar>(R.id.progressLogin).visibility =
            if (loading) android.view.View.VISIBLE else android.view.View.GONE
        findViewById<Button>(R.id.btnLoginEmail).isEnabled = !loading
        findViewById<Button>(R.id.btnGoogleSignIn).isEnabled = !loading
    }

    private fun navigateToMain(user: FirebaseUser) {
        val display = user.displayName?.takeIf { it.isNotBlank() }
            ?: user.email?.substringBefore('@')
            ?: "User"
        startActivity(
            Intent(this, MainActivity::class.java).apply {
                putExtra(MainActivity.EXTRA_USERNAME, display)
            },
        )
        finish()
    }
}
