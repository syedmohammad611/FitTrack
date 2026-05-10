package com.fittrack.app.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.fittrack.app.R
import com.fittrack.app.data.FirestoreUserProfileRepository
import com.fittrack.app.fragments.DashboardFragment
import com.fittrack.app.fragments.HistoryFragment
import com.fittrack.app.fragments.MotivationFragment
import com.fittrack.app.fragments.ProfileFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    FirestoreUserProfileRepository().ensureUserProfile(
                        firebaseUser.uid,
                        firebaseUser.displayName,
                        firebaseUser.email,
                    )
                }
            } catch (_: Exception) {
                // Profile sync is best-effort; workout listener will surface Firestore errors if rules block writes.
            }
        }

        username = intent.getStringExtra(EXTRA_USERNAME)
            ?: firebaseUser.displayName?.takeIf { it.isNotBlank() }
            ?: firebaseUser.email?.substringBefore('@')
            ?: "User"

        findViewById<View>(R.id.nav_home).setOnClickListener {
            openFragment(DashboardFragment.newInstance(username))
        }

        findViewById<View>(R.id.nav_history).setOnClickListener {
            openFragment(HistoryFragment.newInstance(username))
        }

        findViewById<View>(R.id.nav_motivation).setOnClickListener {
            openFragment(MotivationFragment.newInstance(username))
        }

        findViewById<View>(R.id.nav_profile).setOnClickListener {
            openFragment(ProfileFragment.newInstance(username))
        }

        if (savedInstanceState == null) {
            openFragment(DashboardFragment.newInstance(username))
        }
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    companion object {
        const val EXTRA_USERNAME = "EXTRA_USERNAME"
    }
}
