package com.fittrack.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.fittrack.app.R

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve username from arguments
        val username = arguments?.getString(ARG_USERNAME) ?: "User"

        // Update profile name and handle
        val tvProfileName = view.findViewById<TextView>(R.id.tvProfileName)
        val tvProfileHandle = view.findViewById<TextView>(R.id.tvProfileHandle)

        tvProfileName.text = username
        tvProfileHandle.text = "@${username.lowercase().replace(" ", "")}"
    }

    companion object {
        private const val ARG_USERNAME = "ARG_USERNAME"

        @JvmStatic
        fun newInstance(username: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_USERNAME, username)
                }
            }
    }
}