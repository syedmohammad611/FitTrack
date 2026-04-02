package com.fittrack.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class DashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // STEP 6: Retrieve Bundle data inside the Fragment
        // Handle edge case where no data is passed (null safety)
        val username = arguments?.getString("ARG_USERNAME") ?: "User"

        // Use it in UI
        val tvGreeting = view.findViewById<TextView>(R.id.tvGreeting)
        tvGreeting.text = "Good Morning, $username! 💪"
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param username The name of the user.
         * @return A new instance of fragment DashboardFragment.
         */
        @JvmStatic
        fun newInstance(username: String) =
            DashboardFragment().apply {
                arguments = Bundle().apply {
                    // Data passed to Fragments MUST use Bundles
                    putString("ARG_USERNAME", username)
                }
            }
    }
}