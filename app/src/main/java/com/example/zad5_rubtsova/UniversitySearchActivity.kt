package com.example.zad5_rubtsova

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import com.example.zad5_rubtsova.databinding.ActivityUniversitySearchBinding


class UniversitySearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUniversitySearchBinding
    private lateinit var universityAdapter: UniversityAdapter
    private var universities: List<UniversityResponse> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUniversitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        universityAdapter = UniversityAdapter(universities)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = universityAdapter

        binding.btnSearch.setOnClickListener {
            val query = binding.etSearch.text.toString()
            if (query.isNotEmpty()) {
                searchUniversities(query)
            } else {
                Toast.makeText(this, "Введите название университета или специальности", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchUniversities(query: String) {
        val url = "http://universities.hipolabs.com/search?name=$query"

        // Create OkHttpClient instance
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                // Use 'response.use' to ensure resources are closed properly
                response.use {
                    // Check if the response is successful
                    if (!it.isSuccessful) {
                        // Handle the error case
                        runOnUiThread {
                            Toast.makeText(this@UniversitySearchActivity, "Unexpected code: ${it.code}", Toast.LENGTH_SHORT).show()
                        }
                        return
                    }

                    // Safely get the response body
                    val responseBody = it.body
                    if (responseBody == null) {
                        // Handle the case where the body is null
                        runOnUiThread {
                            Toast.makeText(this@UniversitySearchActivity, "Response body is null", Toast.LENGTH_SHORT).show()
                        }
                        return // Exit if body is null
                    }

                    // Read the response body as a string
                    val jsonData = responseBody.string()

                    // Parse the JSON data
                    val universitiesList = Gson().fromJson(jsonData, Array<UniversityResponse>::class.java).toList()

                    // Update the UI on the main thread
                    runOnUiThread {
                        universities = universitiesList
                        universityAdapter.updateData(universities)
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@UniversitySearchActivity, "Ошибка сети: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}