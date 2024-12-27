package com.example.zad5_rubtsova

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.zad5_rubtsova.databinding.ActivityUniversityBinding
import android.util.Log


class UniversityActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUniversityBinding
    private lateinit var universityAdapter: UniversityAdapter
    private var universities: List<UniversityResponse> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUniversityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        universityAdapter = UniversityAdapter(universities)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@UniversityActivity)
            adapter = universityAdapter
        }

        binding.btnAddUniversity.setOnClickListener {
            // Logic to add a new university (not implemented here)
            // You can implement a dialog or a new activity to add a university
        }

        fetchUniversities()
    }

    private fun fetchUniversities() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.getUniversities("Belarus")
                universities = response
                withContext(Dispatchers.Main) {
                    universityAdapter.updateData(universities)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@UniversityActivity,
                        "Failed to fetch universities: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("UniversityActivity", "Error fetching universities", e)
                }
            }
        }
    }
}