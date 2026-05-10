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
import com.fittrack.app.data.FirestoreWorkoutRepository
import com.fittrack.app.models.WorkoutSession
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
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
    private lateinit var btnSortDate: Button
    private lateinit var btnSortVolume: Button

    private val firestoreWorkouts = FirestoreWorkoutRepository()
    private var snapshotRegistration: ListenerRegistration? = null

    /** Latest server-backed list; UI applies search/sort in memory (F2 real-time updates). */
    private var latestSessions: List<WorkoutSession> = emptyList()

    private var sortMode: SortMode = SortMode.DEFAULT

    private enum class SortMode {
        DEFAULT,
        DATE_ASC,
        VOLUME_DESC,
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val username = arguments?.getString(ARG_USERNAME) ?: "User"
        view.findViewById<TextView>(R.id.tvHistoryTitle).text = "$username's Workout History"

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Toast.makeText(requireContext(), R.string.error_not_signed_in, Toast.LENGTH_LONG).show()
            return
        }

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
                showEditDialog(uid, session)
            },
            onDeleteClick = { session ->
                deleteSession(uid, session)
            },
        )
        recyclerView.adapter = adapter

        etSessionDate = view.findViewById(R.id.etSessionDate)
        etSessionWorkout = view.findViewById(R.id.etSessionWorkout)
        etSessionDuration = view.findViewById(R.id.etSessionDuration)
        etSessionVolume = view.findViewById(R.id.etSessionVolume)
        btnAddSession = view.findViewById(R.id.btnAddSession)
        btnAddSession.setOnClickListener { createSession(uid) }

        etSearchWorkout = view.findViewById(R.id.etSearchWorkout)
        btnClearSearch = view.findViewById(R.id.btnClearSearch)
        etSearchWorkout.addTextChangedListener { _ ->
            applyFilterAndSort()
        }
        btnClearSearch.setOnClickListener {
            etSearchWorkout.setText("")
            applyFilterAndSort()
        }

        btnSortDate = view.findViewById(R.id.btnSortDate)
        btnSortVolume = view.findViewById(R.id.btnSortVolume)

        btnSortDate.setOnClickListener {
            sortMode = SortMode.DATE_ASC
            applyFilterAndSort()
        }

        btnSortVolume.setOnClickListener {
            sortMode = SortMode.VOLUME_DESC
            applyFilterAndSort()
        }

        snapshotRegistration?.remove()
        snapshotRegistration = firestoreWorkouts.observeWorkoutSessions(
            uid = uid,
            onUpdate = { sessions ->
                viewLifecycleOwner.lifecycleScope.launch {
                    latestSessions = sessions
                    applyFilterAndSort()
                }
            },
            onError = { e ->
                viewLifecycleOwner.lifecycleScope.launch {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.error_firestore_listen, e.message ?: e.javaClass.simpleName),
                        Toast.LENGTH_LONG,
                    ).show()
                }
            },
        )
    }

    override fun onDestroyView() {
        snapshotRegistration?.remove()
        snapshotRegistration = null
        super.onDestroyView()
    }

    private fun applyFilterAndSort() {
        val q = etSearchWorkout.text.toString().trim()
        var list = latestSessions.toList()
        if (q.isNotEmpty()) {
            list = list.filter { it.workout.contains(q, ignoreCase = true) }
        }
        list = when (sortMode) {
            SortMode.DATE_ASC -> list.sortedBy { it.date }
            SortMode.VOLUME_DESC -> list.sortedByDescending { it.volumeKg.toIntOrNull() ?: 0 }
            SortMode.DEFAULT -> list
        }
        adapter.submitList(list)
    }

    private fun createSession(uid: String) {
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
            volumeKg = volume,
        )

        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) { firestoreWorkouts.addSession(uid, session) }
                clearCreateInputs()
                Toast.makeText(requireContext(), "Session created.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message ?: "Failed to save.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun deleteSession(uid: String, session: WorkoutSession) {
        val docId = session.firestoreId
        if (docId.isNullOrBlank()) {
            Toast.makeText(requireContext(), R.string.error_missing_firestore_id, Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) { firestoreWorkouts.deleteSession(uid, docId) }
                Toast.makeText(requireContext(), "Session deleted.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), e.message ?: "Delete failed.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showEditDialog(uid: String, session: WorkoutSession) {
        val docId = session.firestoreId
        if (docId.isNullOrBlank()) {
            Toast.makeText(requireContext(), R.string.error_missing_firestore_id, Toast.LENGTH_SHORT).show()
            return
        }

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
                    volumeKg = etVolume.text.toString().trim(),
                    firestoreId = docId,
                )
                lifecycleScope.launch {
                    try {
                        withContext(Dispatchers.IO) { firestoreWorkouts.updateSession(uid, updated) }
                        Toast.makeText(requireContext(), "Session updated.", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), e.message ?: "Update failed.", Toast.LENGTH_LONG).show()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
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
