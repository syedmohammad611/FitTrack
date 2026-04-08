package com.fittrack.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fittrack.app.R
import com.fittrack.app.adapters.WorkoutSessionAdapter
import com.fittrack.app.models.WorkoutSession

class HistoryFragment : Fragment() {
    private lateinit var adapter: WorkoutSessionAdapter
    private lateinit var etSearchWorkout: EditText
    private lateinit var btnClearSearch: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = arguments?.getString(ARG_USERNAME) ?: "User"
        view.findViewById<TextView>(R.id.tvHistoryTitle).text = "$username's Workout History"

        val sessions = listOf(
            WorkoutSession("Jun 4", "Push Day - Chest & Triceps", "55m", "3,200"),
            WorkoutSession("Jun 3", "Pull Day - Back & Biceps", "62m", "2,800"),
            WorkoutSession("Jun 2", "Leg Day - Quads & Glutes", "70m", "4,100"),
            WorkoutSession("May 31", "Shoulder & Core", "48m", "1,950"),
            WorkoutSession("May 30", "Full Body HIIT", "35m", "890")
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvHistory)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // F2: Pass click listener — opens WorkoutDetailFragment with Bundle
        adapter = WorkoutSessionAdapter(sessions) { session ->
            val detailFragment = WorkoutDetailFragment.newInstance(session)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = adapter

        etSearchWorkout = view.findViewById(R.id.etSearchWorkout)
        btnClearSearch = view.findViewById(R.id.btnClearSearch)

        etSearchWorkout.addTextChangedListener { text ->
            adapter.filter(text?.toString() ?: "")
        }

        // Fixed: setText("") reliably triggers the text watcher to reset filter
        btnClearSearch.setOnClickListener {
            etSearchWorkout.setText("")
            adapter.filter("")
        }
    }

    companion object {
        private const val ARG_USERNAME = "ARG_USERNAME"

        @JvmStatic
        fun newInstance(username: String) =
            HistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_USERNAME, username)
                }
            }
    }
}