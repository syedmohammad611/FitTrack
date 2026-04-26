package com.fittrack.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fittrack.app.R
import com.fittrack.app.adapters.QuoteAdapter
import com.fittrack.app.data.RemoteQuoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MotivationFragment : Fragment() {
    private lateinit var adapter: QuoteAdapter
    private lateinit var remoteRepository: RemoteQuoteRepository
    private lateinit var progressBar: ProgressBar
    private lateinit var tvEmptyState: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_motivation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = arguments?.getString(ARG_USERNAME) ?: "User"
        view.findViewById<TextView>(R.id.tvMotivationTitle).text = "Fitness Inspiration for $username"

        progressBar = view.findViewById(R.id.pbLoadingQuotes)
        tvEmptyState = view.findViewById(R.id.tvEmptyState)

        remoteRepository = RemoteQuoteRepository()

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvQuotes)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = QuoteAdapter()
        recyclerView.adapter = adapter

        // Load quotes on fragment creation
        loadQuotes()
    }

    private fun loadQuotes() {
        lifecycleScope.launch {
            try {
                // Show loading state
                progressBar.visibility = View.VISIBLE
                tvEmptyState.visibility = View.GONE

                // Fetch quotes on IO thread
                val quotes = withContext(Dispatchers.IO) {
                    remoteRepository.fetchQuotes(limit = 20)
                }

                // Update UI on main thread
                if (quotes.isNotEmpty()) {
                    adapter.submitList(quotes)
                    tvEmptyState.visibility = View.GONE
                } else {
                    tvEmptyState.text = "No quotes available. Please try again."
                    tvEmptyState.visibility = View.VISIBLE
                    Toast.makeText(
                        requireContext(),
                        "Failed to load quotes. Check your internet connection.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                tvEmptyState.text = "Error loading quotes. Please try again."
                tvEmptyState.visibility = View.VISIBLE
                Toast.makeText(
                    requireContext(),
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                // Hide loading indicator
                progressBar.visibility = View.GONE
            }
        }
    }

    companion object {
        private const val ARG_USERNAME = "ARG_USERNAME"

        @JvmStatic
        fun newInstance(username: String) =
            MotivationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_USERNAME, username)
                }
            }
    }
}

