package com.example.zad5_rubtsova

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "universities")
data class University(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val region: String,
    val photoUrl: String
)

@Entity(tableName = "specialties") // Annotate the class as an entity
data class SpecialtyEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // Primary key with auto-increment
    val name: String,        // Name of the specialty
    val studentCount: Int,   // Number of students in this specialty
    val universityId: Long   // Foreign key to associate with a university
)
@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val group1: String,
    val course: String,
    val specialty: String,
    val dateOfBirth: String,
    val photoPath: String
){
    override fun toString(): String {
        return "$id. $fullName, $group1, курс: $course. №Специальности: $specialty. Дата рождения: $dateOfBirth"
    }
}

@Entity(tableName = "teachers")
data class Teacher(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val fullName: String,
    val workload: Int,
    val specialtyId: Long
)