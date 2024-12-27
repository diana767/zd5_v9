package com.example.zad5_rubtsova

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.widget.ImageView

class StudentAdapter(private val students: List<Student>, private val itemClickListener: (Int) -> Unit) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    inner class StudentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val studentName: TextView = view.findViewById(R.id.tvStudentName)
        val groupId: TextView = view.findViewById(R.id.tvGroupId)
        val course: TextView = view.findViewById(R.id.tvCourse)
        val specialty:  TextView = view.findViewById(R.id.tvSpecialty)
        val dateOfBirthday:  TextView = view.findViewById(R.id.tvDateOfBirth)
        val photoImageView: ImageView = itemView.findViewById(R.id.photo_image_view)
        init {
            itemView.setOnClickListener {
                itemClickListener(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.studentName.text = student.fullName
        holder.groupId.text = student.group1
        holder.course.text = student.course
        holder.specialty.text = student.specialty
        holder.dateOfBirthday.text = student.dateOfBirth

        val resId = holder.itemView.context.resources.getIdentifier(student.photoPath, "drawable", holder.itemView.context.packageName)
        if (resId != 0) {
            holder.photoImageView.setImageResource(resId)
        } else {
            holder.photoImageView.setImageResource(R.drawable.header_image)
        }
    }

    override fun getItemCount(): Int {
        return students.size
    }
}