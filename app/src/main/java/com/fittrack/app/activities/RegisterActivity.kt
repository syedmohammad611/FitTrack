package com.fittrack.app.activities

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.fittrack.app.R
import com.fittrack.app.data.AuthRepository
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etEmail = findViewById<EditText>(R.id.etRegisterEmail)
        val etPassword = findViewById<EditText>(R.id.etRegisterPassword)
        val etConfirm = findViewById<EditText>(R.id.etRegisterConfirmPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegisterSubmit)

        btnRegister.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            val confirm = etConfirm.text.toString()

            if (email.isEmpty()) {
                etEmail.error = getString(R.string.error_email_required)
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = getString(R.string.error_email_invalid)
                return@setOnClickListener
            }
            if (password.length < 6) {
                etPassword.error = getString(R.string.error_password_short)
                return@setOnClickListener
            }
            if (password != confirm) {
                etConfirm.error = getString(R.string.error_password_mismatch)
                return@setOnClickListener
            }

            btnRegister.isEnabled = false
            lifecycleScope.launch {
                try {
                    authRepository.registerWithEmail(email, password)
                        .onSuccess {
                            Toast.makeText(
                                this@RegisterActivity,
                                R.string.toast_registration_success,
                                Toast.LENGTH_SHORT,
                            ).show()
                            finish()
                        }
                        .onFailure { e ->
                            val msg = (e as? FirebaseAuthException)?.message ?: e.message
                                ?: getString(R.string.error_auth_generic)
                            Toast.makeText(this@RegisterActivity, msg, Toast.LENGTH_LONG).show()
                        }
                } finally {
                    btnRegister.isEnabled = true
                }
            }
        }
    }
}
