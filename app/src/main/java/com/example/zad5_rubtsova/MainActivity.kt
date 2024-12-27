package com.example.zad5_rubtsova

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import com.example.zad5_rubtsova.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnManageUniversities.setOnClickListener {
            startActivity(Intent(this, UniversityActivity::class.java))
        }

        binding.btnManageStudents.setOnClickListener {
            startActivity(Intent(this, StudentActivity::class.java))
        }

        binding.btnManageTeachers.setOnClickListener {
            startActivity(Intent(this, TeacherActivity::class.java))
        }
    }
}