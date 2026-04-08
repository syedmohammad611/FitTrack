package com.fittrack.app.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.fittrack.app.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            if (username.isNotEmpty()) {
                // STEP 3: Create an explicit Intent to MainActivity
                val intent = Intent(this, MainActivity::class.java)
                // Pass meaningful piece of data using putExtra()
                intent.putExtra("EXTRA_USERNAME", username)
                startActivity(intent)
                finish() // Optional: finish LoginActivity so user can't go back
            } else {
                etUsername.error = "Please enter a username"
            }
        }
    }
}
