package com.fittrack.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fittrack.app.R
import com.fittrack.app.models.WorkoutSession

class WorkoutSessionAdapter(
    private val allSessions: List<WorkoutSession>,
    private val onItemClick: (WorkoutSession) -> Unit  // F2: click callback
) : RecyclerView.Adapter<WorkoutSessionAdapter.WorkoutSessionViewHolder>() {

    private var displayedSessions: List<WorkoutSession> = allSessions

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutSessionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_session, parent, false)
        return WorkoutSessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutSessionViewHolder, position: Int) {
        holder.bind(displayedSessions[position], onItemClick)  // pass click
    }

    override fun getItemCount(): Int = displayedSessions.size

    fun filter(query: String) {
        displayedSessions = if (query.isEmpty()) {
            allSessions
        } else {
            allSessions.filter { session ->
                session.workout.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    class WorkoutSessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvWorkout: TextView = itemView.findViewById(R.id.tvWorkout)
        private val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
        private val tvVolume: TextView = itemView.findViewById(R.id.tvVolume)

        fun bind(session: WorkoutSession, onItemClick: (WorkoutSession) -> Unit) {
            tvDate.text = session.date
            tvWorkout.text = session.workout
            tvDuration.text = session.duration
            tvVolume.text = session.volumeKg
            // F2: trigger click with the session object
            itemView.setOnClickListener { onItemClick(session) }
        }
    }
}