package com.fittrack.app.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.fittrack.app.R
import com.fittrack.app.fragments.DashboardFragment
import com.fittrack.app.fragments.HistoryFragment

class MainActivity : AppCompatActivity() {
	private lateinit var username: String

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		username = intent.getStringExtra("EXTRA_USERNAME") ?: "User"

		findViewById<View>(R.id.nav_home).setOnClickListener {
			openFragment(DashboardFragment.newInstance(username))
		}

		findViewById<View>(R.id.nav_history).setOnClickListener {
			openFragment(HistoryFragment.newInstance(username))
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
}

