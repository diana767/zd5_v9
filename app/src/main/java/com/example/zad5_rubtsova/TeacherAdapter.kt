package com.example.zad5_rubtsova

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TeacherAdapter(
    private var teachers: List<Teacher>,
    private val listener: OnTeacherActionListener
) : RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder>() {

    class TeacherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val fullName: TextView = itemView.findViewById(R.id.tvFullName)
        val workload: TextView = itemView.findViewById(R.id.tvWorkload)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete) // Add a delete button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_teacher, parent, false)
        return TeacherViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeacherViewHolder, position: Int) {
        val teacher = teachers[position]
        holder.fullName.text = teacher.fullName
        holder.workload.text = "Workload: ${teacher.workload}"

        // Set click listener for the teacher item
        holder.itemView.setOnClickListener {
            listener.editTeacher(teacher)
        }

        // Set click listener for the delete button
        holder.btnDelete.setOnClickListener {
            listener.deleteTeacher(teacher)
        }
    }

    override fun getItemCount(): Int {
        return teachers.size
    }

    fun updateData(newTeachers: List<Teacher>) {
        teachers = newTeachers
        notifyDataSetChanged()
    }
}