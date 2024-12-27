package com.example.zad5_rubtsova

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Query


@Dao
interface UniversityDao {

    @Insert
    suspend fun insertUniversity(university: University): Long

    @Delete
    suspend fun deleteUniversity(university: University)

    @Update
    suspend fun updateUniversity(university: University)

    @Query("SELECT * FROM universities WHERE region = :region")
    suspend fun getUniversitiesByRegion(region: String): List<University>

    @Query("SELECT * FROM universities")
    suspend fun getAllUniversities(): List<University>
}
@Dao
interface SpecialtyDao {

    @Insert
    suspend fun insertSpecialty(specialty: SpecialtyEntity): Long

    @Query("SELECT * FROM specialties WHERE universityId = :universityId")
    suspend fun getSpecialtiesByUniversity(universityId: Long): List<SpecialtyEntity>
}

@Dao
interface StudentDao {
    @Insert
    suspend fun insertStudent(student: Student): Long

    @Delete
    suspend fun deleteStudent(student: Student): Int

    @Update
    fun updateStudent(student: Student): Int

    @Query("SELECT * FROM students WHERE fullName LIKE :name")
    suspend fun searchStudentsByName(name: String): MutableList<Student>

    @Query("SELECT * FROM students")
    suspend fun getAllStudentsByGroup(): MutableList<Student>

    @Query("DELETE FROM students WHERE fullName = :name")
    suspend fun deleteStudentByName(name: String): Int

    @Query("SELECT COUNT(*) FROM students WHERE group1 = :groupId")
    fun getStudentCountByGroup(groupId: String): Int

    @Query("SELECT COUNT(*) FROM students WHERE fullName = :fullName AND dateOfBirth = :dateOfBirth")
    fun countStudentsByNameAndDOB(fullName: String, dateOfBirth: String): Int
}

@Dao
interface TeacherDao {
    @Insert
    suspend fun insertTeacher(teacher: Teacher)

    @Update
    suspend fun updateTeacher(teacher: Teacher)

    @Delete
    suspend fun deleteTeacher(teacher: Teacher)

    @Query("SELECT * FROM teachers WHERE specialtyId = :specialtyId")
    suspend fun getTeachersBySpecialty(specialtyId: Long): List<Teacher>
}