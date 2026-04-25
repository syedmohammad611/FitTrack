package com.fittrack.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fittrack.app.R
import com.fittrack.app.models.WorkoutSession

class WorkoutSessionAdapter(
    private val onItemClick: (WorkoutSession) -> Unit,
    private val onEditClick: (WorkoutSession) -> Unit,
    private val onDeleteClick: (WorkoutSession) -> Unit
) : RecyclerView.Adapter<WorkoutSessionAdapter.WorkoutSessionViewHolder>() {

    private var displayedSessions: List<WorkoutSession> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutSessionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_session, parent, false)
        return WorkoutSessionViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutSessionViewHolder, position: Int) {
        holder.bind(displayedSessions[position], onItemClick, onEditClick, onDeleteClick)
    }

    override fun getItemCount(): Int = displayedSessions.size

    fun submitList(newSessions: List<WorkoutSession>) {
        displayedSessions = newSessions
        notifyDataSetChanged()
    }

    class WorkoutSessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvWorkout: TextView = itemView.findViewById(R.id.tvWorkout)
        private val tvDuration: TextView = itemView.findViewById(R.id.tvDuration)
        private val tvVolume: TextView = itemView.findViewById(R.id.tvVolume)
        private val btnEdit: TextView = itemView.findViewById(R.id.btnEditSession)
        private val btnDelete: TextView = itemView.findViewById(R.id.btnDeleteSession)

        fun bind(
            session: WorkoutSession,
            onItemClick: (WorkoutSession) -> Unit,
            onEditClick: (WorkoutSession) -> Unit,
            onDeleteClick: (WorkoutSession) -> Unit
        ) {
            tvDate.text = session.date
            tvWorkout.text = session.workout
            tvDuration.text = session.duration
            tvVolume.text = session.volumeKg
            itemView.setOnClickListener { onItemClick(session) }
            btnEdit.setOnClickListener { onEditClick(session) }
            btnDelete.setOnClickListener { onDeleteClick(session) }
        }
    }
}