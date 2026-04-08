package com.fittrack.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.fittrack.app.R

class DashboardFragment : Fragment() {

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		return inflater.inflate(R.layout.fragment_dashboard, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val username = arguments?.getString(ARG_USERNAME) ?: "User"
		val tvGreeting = view.findViewById<TextView>(R.id.tvGreeting)
		tvGreeting.text = "Good Morning, $username!"
	}

	companion object {
		private const val ARG_USERNAME = "ARG_USERNAME"

		@JvmStatic
		fun newInstance(username: String) =
			DashboardFragment().apply {
				arguments = Bundle().apply {
					putString(ARG_USERNAME, username)
				}
			}
	}
}

