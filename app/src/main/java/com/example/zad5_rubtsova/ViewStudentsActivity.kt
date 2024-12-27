package com.example.zad5_rubtsova

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ViewStudentsActivity : AppCompatActivity() {
    private lateinit var studentDao: StudentDao
    private lateinit var studentAdapter: StudentAdapter

    private lateinit var nameEditText: EditText
    private lateinit var groupEditText: EditText
    private lateinit var specialityEditText: EditText
    private lateinit var courseEditTExt:EditText

    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    private lateinit var etDateOfBirth: TextView
    private lateinit var selDatBtn: Button
    private var currentPosition: Int = -1
    private var students: MutableList<Student> = mutableListOf()

    var selectedDate: String = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_students)

        nameEditText = findViewById(R.id.name)
        groupEditText = findViewById(R.id.group)
        specialityEditText = findViewById(R.id.speciality)
        courseEditTExt = findViewById(R.id.course)

        editButton = findViewById(R.id.edit)
        deleteButton = findViewById(R.id.delete)
        etDateOfBirth = findViewById(R.id.set_date_view)
        selDatBtn = findViewById(R.id.selectedDateBtn)

        val searchView: SearchView = findViewById(R.id.search_view)
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)

        studentDao = CollegeDatabase.getDatabase(applicationContext).studentDao()
        recyclerView.layoutManager = LinearLayoutManager(this)
        loadStudents()

        studentAdapter = StudentAdapter(students) { position ->

            currentPosition = position
            val selectedItem = students[position]

            nameEditText.setText(selectedItem.fullName)
            groupEditText.setText(selectedItem.group1)
            specialityEditText.setText(selectedItem.specialty)
            courseEditTExt.setText(selectedItem.course)
        }

        recyclerView.adapter = studentAdapter

        selDatBtn.setOnClickListener{
            val defaultYear = 2005
            val defaultMonth = 0
            val defaultDay = 1

            val minDateCalendar = Calendar.getInstance()
            minDateCalendar.set(1970, Calendar.JANUARY, 1)
            val minDate = minDateCalendar.timeInMillis

            val maxDateCalendar = Calendar.getInstance()
            maxDateCalendar.set(Calendar.YEAR, maxDateCalendar.get(Calendar.YEAR) - 15)
            val maxDate = maxDateCalendar.timeInMillis

            etDateOfBirth = findViewById(R.id.set_date_view)
            val datePickerDialog = DatePickerDialog(this, { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                selectedDate = String.format("%02d-%02d-%d", selectedDay, selectedMonth + 1, selectedYear)
                etDateOfBirth.text = selectedDate
            }, defaultYear, defaultMonth, defaultDay)

            datePickerDialog.datePicker.minDate = minDate
            datePickerDialog.datePicker.maxDate = maxDate
            datePickerDialog.show()
        }

        editButton.setOnClickListener {
            if (currentPosition != -1) {
                val selectedStudent = students[currentPosition]

                val updatedName = nameEditText.text.toString()
                val updatedGroup = groupEditText.text.toString()
                val updatedSpecialty = specialityEditText.text.toString()
                val updatedDate = selectedDate

                if (updatedName.isBlank() || updatedGroup.isBlank() || updatedSpecialty == null) {
                    Toast.makeText(this, "Пожалуйста, заполните все поля корректно", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val updatedStudent = selectedStudent.copy(
                    fullName = updatedName,
                    group1 = updatedGroup,
                    specialty = updatedSpecialty,
                    dateOfBirth = updatedDate
                )

                Thread {
                    studentDao.updateStudent(updatedStudent)

                    runOnUiThread {
                        students[currentPosition] = updatedStudent
                        studentAdapter.notifyItemChanged(currentPosition)
                        Toast.makeText(this, "Данные обновлены", Toast.LENGTH_SHORT).show()
                    }
                }.start()
            } else {
                Toast.makeText(this, "Выберите студента для редактирования", Toast.LENGTH_SHORT).show()
            }
        }
        deleteButton.setOnClickListener {
            val nameToDelete = nameEditText.text.toString()
            deleteStudentByName(nameToDelete)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchStudents(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    loadStudents()
                }
                return true
            }
        })
    }

    private fun deleteStudentByName(name: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val deletedRows = studentDao.deleteStudentByName(name) // Удаляем студента по имени
                if (deletedRows > 0) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ViewStudentsActivity, "Студент удален", Toast.LENGTH_SHORT).show()
                        loadStudents()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ViewStudentsActivity, "Студент не найден", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("EditStudentActivity", "Ошибка при удалении студента", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ViewStudentsActivity, "Ошибка при удалении студента: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun loadStudents() {
        CoroutineScope(Dispatchers.IO).launch {
            val newStudents = studentDao.getAllStudentsByGroup() // Получаем студентов
            withContext(Dispatchers.Main) {
                students.clear() // Очищаем текущий список
                students.addAll(newStudents) // Добавляем новых студентов
                studentAdapter.notifyDataSetChanged() // Уведомляем адаптер об изменениях
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun searchStudents(name: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = studentDao.searchStudentsByName("%$name%")
            withContext(Dispatchers.Main) {
                students.clear()
                students.addAll(result)
                studentAdapter.notifyDataSetChanged()
            }
        }
    }
}