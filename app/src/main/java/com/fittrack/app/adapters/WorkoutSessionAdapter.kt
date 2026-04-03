package com.fittrack.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fittrack.app.R
import com.fittrack.app.models.WorkoutSession

class WorkoutSessionAdapter(
    private val sessions: List<WorkoutSession>
) : RecyclerView.Adapter<WorkoutSessionAdapter.WorkoutSessionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutSessionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_session, parent, false)
        return WorkoutSessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutSessionViewHolder, position: Int) {
        holder.bind(sessions[position])
    }

    override fun getItemCount(): Int = sessions.size

    class WorkoutSessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvWorkout: TextView = itemView.findViewById(R.id.tvWorkout)
        private val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
        private val tvVolume: TextView = itemView.findViewById(R.id.tvVolume)

        fun bind(session: WorkoutSession) {
            tvDate.text = session.date
            tvWorkout.text = session.workout
            tvDuration.text = session.duration
            tvVolume.text = session.volumeKg
        }
    }
}

