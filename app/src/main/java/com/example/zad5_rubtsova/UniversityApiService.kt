package com.example.zad5_rubtsova

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import androidx.room.Entity
import androidx.room.PrimaryKey

// Интерфейс для API
interface UniversityApiService {
    @GET("search")
    suspend fun getUniversities(@Query("country") country: String): List<UniversityResponse>
}

// Класс для ответа от API
data class UniversityResponse(
    val name: String,
    val country: String,
    val web_pages: List<String>,
    val domains: List<String>,
    val photoUrl: String,
    val specialties: List<SpecialtyResponse> // List of specialties
)

data class SpecialtyResponse(
    val name: String,
    val studentCount: Int // Number of students in this specialty
)

// Объект для создания экземпляра Retrofit
object RetrofitInstance {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://universities.hipolabs.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: UniversityApiService = retrofit.create(UniversityApiService::class.java)
}