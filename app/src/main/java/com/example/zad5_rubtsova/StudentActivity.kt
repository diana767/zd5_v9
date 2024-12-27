package com.example.zad5_rubtsova

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import com.example.zad5_rubtsova.databinding.ActivityStudentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Toast
import android.content.ContentValues
import android.os.Bundle
import android.app.DatePickerDialog
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import com.example.zd5_v66.CollegeDatabase
import kotlinx.coroutines.CoroutineScope
import java.util.Calendar

class StudentActivity : AppCompatActivity(),
    PhotoPickerDialogFragment.OnPhotoSelectedListener {
    private lateinit var studentDao: StudentDao
    private lateinit var etFullName: EditText
    private lateinit var etGroupId: EditText
    private lateinit var etDateOfBirth: TextView
    private lateinit var btnSaveStudent: Button

    private lateinit var spinner: Spinner
    private lateinit var tv_user_speciality: TextView
    private lateinit var course: EditText

    var selectedDate: String = ""
    var selectedSpecialties: MutableSet<String> = mutableSetOf()
    var budgetSpecialtySelected: Boolean = false
    private lateinit var selectedPhotoImageView: ImageView
    private var selectedPhoto: String? = null

    @SuppressLint("MissingInflatedId", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)

        etFullName = findViewById(R.id.et_full_name)
        etGroupId = findViewById(R.id.et_group_id)
        btnSaveStudent = findViewById(R.id.btn_save_student)
        spinner = findViewById(R.id.spinner)
        tv_user_speciality = findViewById(R.id.tv_user_speciality)
        course = findViewById(R.id.et_course)

        etDateOfBirth = findViewById(R.id.set_date)
        selectedPhotoImageView = findViewById(R.id.selected_photo_image_view)


        studentDao = CollegeDatabase.getDatabase(applicationContext).studentDao()
        tv_user_speciality.text = ""

        val filmSpinner = findViewById<Spinner>(R.id.spinner)
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.spec_types, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        filmSpinner.adapter = adapter

        filmSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent.getItemAtPosition(position).toString()

                if (selectedSpecialties.size < 3) {
                    if (selectedItem.endsWith("(бюджет)")) {
                        if (!budgetSpecialtySelected) {
                            selectedSpecialties.add(selectedItem)
                            budgetSpecialtySelected = true
                        } else {
                            Toast.makeText(
                                this@StudentActivity,
                                "Вы уже выбрали бюджетную специальность",
                                Toast.LENGTH_SHORT
                            ).show()
                            return
                        }
                    } else {
                        if (!selectedSpecialties.contains(selectedItem)) {
                            selectedSpecialties.add(selectedItem)
                        } else {
                            Toast.makeText(
                                this@StudentActivity,
                                "Эта специальность уже выбрана",
                                Toast.LENGTH_SHORT
                            ).show()
                            return
                        }
                    }
                    tv_user_speciality.text = selectedSpecialties.joinToString("\n")
                } else {
                    Toast.makeText(
                        this@StudentActivity,
                        "Вы можете выбрать максимум 3 специальности",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Toast.makeText(
                    this@StudentActivity,
                    "Выберите хотя бы одну специальность",
                    Toast.LENGTH_SHORT
                )
            }
        }

        btnSaveStudent.setOnClickListener {
            val fullName = etFullName.text.toString()
            val group1 = etGroupId.text.toString()
            val courseAgain = course.text.toString()
            val specialties = tv_user_speciality.text.toString()
            val dateOfBirth = etDateOfBirth.text.toString()

            if (selectedPhoto == null) {
                Toast.makeText(this, "Выберите фотографию!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val existingCount = studentDao.countStudentsByNameAndDOB(fullName, dateOfBirth)

                    if (existingCount > 0) {
                        runOnUiThread {
                            Toast.makeText(this@StudentActivity, "Такой студент уже существует!", Toast.LENGTH_SHORT).show()
                        }
                        return@launch
                    }

                    val currentCount = studentDao.getStudentCountByGroup(group1)
                    if (currentCount >= 15) {
                        runOnUiThread {
                            Toast.makeText(this@StudentActivity, "Лимит студентов в группе - 15 человек!", Toast.LENGTH_SHORT).show()
                        }
                        return@launch
                    }

                    val student = Student(
                        fullName = fullName,
                        group1 = group1,
                        course = courseAgain,
                        specialty = specialties,
                        dateOfBirth = dateOfBirth,
                        photoPath = selectedPhoto!!
                    )

                    val id = studentDao.insertStudent(student)
                    runOnUiThread {
                        Toast.makeText(
                            this@StudentActivity,
                            "Студент добавлен",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("AddStudentsActivity", "Ошибка при добавлении студента", e)
                    runOnUiThread {
                        Toast.makeText(
                            this@StudentActivity,
                            "Ошибка при добавлении студента: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        findViewById<Button>(R.id.button_select_photo).setOnClickListener {
            val dialog = PhotoPickerDialogFragment()
            dialog.show(supportFragmentManager, "PhotoPicker")
        }
    }

    override fun onPhotoSelected(photo: String) {
        val resId = resources.getIdentifier(photo, "drawable", packageName)
        if (resId != 0) {
            selectedPhotoImageView.setImageResource(resId)
            selectedPhoto = resources.getResourceEntryName(resId)
        } else {
            Log.e("AddStudentsActivity", "Image resource not found: $photo")
        }
    }

    fun deleteSpeciality(view: View) {
        tv_user_speciality.text = ""

        selectedSpecialties.clear()
        budgetSpecialtySelected = false

        Toast.makeText(this, "Специальности сброшены. Выберите заново.", Toast.LENGTH_SHORT)
            .show()
    }

    fun selectedDate(view: View) {
        val defaultYear = 2005
        val defaultMonth = 0
        val defaultDay = 1

        val minDateCalendar = Calendar.getInstance()
        minDateCalendar.set(1970, Calendar.JANUARY, 1)
        val minDate = minDateCalendar.timeInMillis

        val maxDateCalendar = Calendar.getInstance()
        maxDateCalendar.set(Calendar.YEAR, maxDateCalendar.get(Calendar.YEAR) - 15)
        val maxDate = maxDateCalendar.timeInMillis

        etDateOfBirth = findViewById(R.id.set_date)
        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                selectedDate =
                    String.format("%02d-%02d-%d", selectedDay, selectedMonth + 1, selectedYear)
                etDateOfBirth.text = selectedDate
            },
            defaultYear,
            defaultMonth,
            defaultDay
        )

        datePickerDialog.datePicker.minDate = minDate
        datePickerDialog.datePicker.maxDate = maxDate
        datePickerDialog.show()
    }
}