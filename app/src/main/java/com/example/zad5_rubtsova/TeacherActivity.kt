package com.example.zad5_rubtsova

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.example.zad5_rubtsova.databinding.ActivityTeacherBinding
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope


class TeacherActivity : AppCompatActivity(), OnTeacherActionListener {
    private lateinit var binding: ActivityTeacherBinding
    private lateinit var teacherAdapter: TeacherAdapter
    private var specialtyId: Long = 0 // Assume this is passed from the previous activity
    private var currentTeacherId: Long? = null // To track the teacher being edited

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        teacherAdapter = TeacherAdapter(emptyList(), this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@TeacherActivity)
            adapter = teacherAdapter
        }

        binding.btnAddTeacher.setOnClickListener {
            val fullName = binding.etFullName.text.toString()
            val workload = binding.etWorkload.text.toString().toIntOrNull()

            if (fullName.isNotEmpty() && workload != null) {
                if (workload > 1440) {
                    Toast.makeText(this, "Нагрузка не может превышать 1440 часов/год", Toast.LENGTH_SHORT).show()
                } else {
                    addTeacher(fullName, workload)
                }
            } else {
                Toast.makeText(this, "Пожалуйста, введите корректные данные", Toast.LENGTH_SHORT).show()
            }
        }

        fetchTeachers()
    }

    private fun fetchTeachers() {
        lifecycleScope.launch(Dispatchers.IO) {
            val teachers = TeacherDatabase.getInstance(this@TeacherActivity).teacherDao().getTeachersBySpecialty(specialtyId)
            withContext(Dispatchers.Main) {
                teacherAdapter.updateData(teachers)
            }
        }
    }

    private fun addTeacher(fullName: String, workload: Int) {
        val teacher = Teacher(fullName = fullName, specialtyId = specialtyId, workload = workload)
        lifecycleScope.launch(Dispatchers.IO) {
            TeacherDatabase.getInstance(this@TeacherActivity).teacherDao().insertTeacher(teacher)
            fetchTeachers() // Refresh the list
        }
    }

    override fun deleteTeacher(teacher: Teacher) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                TeacherDatabase.getInstance(this@TeacherActivity).teacherDao().deleteTeacher(teacher)
                fetchTeachers() // Refresh the list
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@TeacherActivity, "Ошибка при удалении преподавателя: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun editTeacher(teacher: Teacher) {
        // Populate fields for editing
        binding.etFullName.setText(teacher.fullName)
        binding.etWorkload.setText(teacher.workload.toString())
        currentTeacherId = teacher.id // Set the current teacher ID for editing
    }

    private fun updateTeacher() {
        val fullName = binding.etFullName.text.toString()
        val workload = binding.etWorkload.text.toString().toIntOrNull()

        if (currentTeacherId != null && fullName.isNotEmpty() && workload != null) {
            if (workload > 1440) {
                Toast.makeText(this, "Нагрузка не может превышать 1440 часов/год", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    val updatedTeacher = Teacher(id = currentTeacherId!!, fullName = fullName, workload = workload, specialtyId = specialtyId)
                    TeacherDatabase.getInstance(this@TeacherActivity).teacherDao().updateTeacher(updatedTeacher)
                    fetchTeachers() // Refresh the list
                    clearFields()
                    currentTeacherId = null // Reset the current teacher ID
                }
            }
        } else {
            Toast.makeText(this, "Пожалуйста, введите корректные данные", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearFields() {
        binding.etFullName.text.clear()
        binding.etWorkload.text.clear()
    }
}