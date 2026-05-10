package com.fittrack.app.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.fittrack.app.R
import com.fittrack.app.activities.LoginActivity
import com.fittrack.app.data.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = arguments?.getString(ARG_USERNAME) ?: "User"

        val tvProfileName = view.findViewById<TextView>(R.id.tvProfileName)
        val tvProfileHandle = view.findViewById<TextView>(R.id.tvProfileHandle)

        tvProfileName.text = username
        tvProfileHandle.text = "@${username.lowercase().replace(" ", "")}"

        view.findViewById<Button>(R.id.btnLogout).setOnClickListener {
            val context = requireContext()
            AuthRepository().signOut()
            GoogleSignIn.getClient(context, AuthRepository.googleSignInOptions(context)).signOut()
            startActivity(
                Intent(context, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                },
            )
            requireActivity().finish()
        }
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
