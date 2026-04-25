package com.fittrack.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fittrack.app.R
import com.fittrack.app.adapters.WorkoutSessionAdapter
import com.fittrack.app.data.WorkoutRepository
import com.fittrack.app.models.WorkoutSession
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryFragment : Fragment() {
    private lateinit var adapter: WorkoutSessionAdapter
    private lateinit var etSearchWorkout: EditText
    private lateinit var btnClearSearch: Button
    private lateinit var etSessionDate: EditText
    private lateinit var etSessionWorkout: EditText
    private lateinit var etSessionDuration: EditText
    private lateinit var etSessionVolume: EditText
    private lateinit var btnAddSession: Button
    private lateinit var repository: WorkoutRepository

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

        repository = WorkoutRepository(requireContext())

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvHistory)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = WorkoutSessionAdapter(
            onItemClick = { session ->
                val detailFragment = WorkoutDetailFragment.newInstance(session)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit()
            },
            onEditClick = { session ->
                showEditDialog(session)
            },
            onDeleteClick = { session ->
                deleteSession(session)
            }
        )
        recyclerView.adapter = adapter

        etSessionDate = view.findViewById(R.id.etSessionDate)
        etSessionWorkout = view.findViewById(R.id.etSessionWorkout)
        etSessionDuration = view.findViewById(R.id.etSessionDuration)
        etSessionVolume = view.findViewById(R.id.etSessionVolume)
        btnAddSession = view.findViewById(R.id.btnAddSession)
        btnAddSession.setOnClickListener {
            createSession()
        }

        etSearchWorkout = view.findViewById(R.id.etSearchWorkout)
        btnClearSearch = view.findViewById(R.id.btnClearSearch)

        etSearchWorkout.addTextChangedListener { text ->
            loadSessions(text?.toString().orEmpty())
        }

        btnClearSearch.setOnClickListener {
            etSearchWorkout.setText("")
            loadSessions("")
        }

        lifecycleScope.launch {
            withContext(Dispatchers.IO) { repository.seedIfEmpty() }
            loadSessions("")
        }
    }

    private fun createSession() {
        val date = etSessionDate.text.toString().trim()
        val workout = etSessionWorkout.text.toString().trim()
        val duration = etSessionDuration.text.toString().trim()
        val volume = etSessionVolume.text.toString().trim()

        if (date.isEmpty() || workout.isEmpty() || duration.isEmpty() || volume.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        val session = WorkoutSession(
            date = date,
            workout = workout,
            duration = duration,
            volumeKg = volume
        )

        lifecycleScope.launch {
            withContext(Dispatchers.IO) { repository.createSession(session) }
            clearCreateInputs()
            loadSessions(etSearchWorkout.text.toString().trim())
            Toast.makeText(requireContext(), "Session created.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteSession(session: WorkoutSession) {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) { repository.deleteSession(session.id) }
            loadSessions(etSearchWorkout.text.toString().trim())
            Toast.makeText(requireContext(), "Session deleted.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showEditDialog(session: WorkoutSession) {
        val editView = layoutInflater.inflate(R.layout.dialog_edit_session, null)
        val etDate = editView.findViewById<EditText>(R.id.etEditDate)
        val etWorkout = editView.findViewById<EditText>(R.id.etEditWorkout)
        val etDuration = editView.findViewById<EditText>(R.id.etEditDuration)
        val etVolume = editView.findViewById<EditText>(R.id.etEditVolume)

        etDate.setText(session.date)
        etWorkout.setText(session.workout)
        etDuration.setText(session.duration)
        etVolume.setText(session.volumeKg)

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Update Session")
            .setView(editView)
            .setPositiveButton("Update") { _, _ ->
                val updated = session.copy(
                    date = etDate.text.toString().trim(),
                    workout = etWorkout.text.toString().trim(),
                    duration = etDuration.text.toString().trim(),
                    volumeKg = etVolume.text.toString().trim()
                )
                lifecycleScope.launch {
                    withContext(Dispatchers.IO) { repository.updateSession(updated) }
                    loadSessions(etSearchWorkout.text.toString().trim())
                    Toast.makeText(requireContext(), "Session updated.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun loadSessions(query: String) {
        lifecycleScope.launch {
            val sessions = withContext(Dispatchers.IO) {
                repository.readSessions(query)
            }
            adapter.submitList(sessions)
        }
    }

    private fun clearCreateInputs() {
        etSessionDate.setText("")
        etSessionWorkout.setText("")
        etSessionDuration.setText("")
        etSessionVolume.setText("")
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