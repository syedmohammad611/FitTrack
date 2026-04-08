package com.fittrack.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.fittrack.app.R
import com.fittrack.app.models.WorkoutSession

class WorkoutDetailFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_workout_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // F2: Retrieve WorkoutSession object passed via Bundle
        val session = arguments?.getParcelable<WorkoutSession>(ARG_SESSION)

        session?.let {
            view.findViewById<TextView>(R.id.tvDetailWorkout).text = it.workout
            view.findViewById<TextView>(R.id.tvDetailDate).text = "📅 Date: ${it.date}"
            view.findViewById<TextView>(R.id.tvDetailDuration).text = "⏱ Duration: ${it.duration}"
            view.findViewById<TextView>(R.id.tvDetailVolume).text = "🏋️ Volume: ${it.volumeKg}"
        }

        // Back button — pops this fragment off the stack
        view.findViewById<Button>(R.id.btnBack).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    companion object {
        private const val ARG_SESSION = "ARG_SESSION"

        fun newInstance(session: WorkoutSession): WorkoutDetailFragment {
            return WorkoutDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_SESSION, session)
                }
            }
        }
    }
}