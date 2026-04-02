package com.fittrack.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // STEP 4: Extract the passed data using getIntent()
        // Handle edge case where no data is passed (null safety)
        val username = intent.getStringExtra("EXTRA_USERNAME") ?: "User"

        // STEP 5: Pass data to Fragment using Bundle
        // We use the factory method newInstance to handle Bundle creation
        if (savedInstanceState == null) {
            val dashboardFragment = DashboardFragment.newInstance(username)
            
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, dashboardFragment)
                .commit()
        }
    }
}