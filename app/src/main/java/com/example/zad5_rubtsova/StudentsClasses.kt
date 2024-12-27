package com.example.zad5_rubtsova

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Student::class, Teacher::class], version = 3)
abstract class CollegeDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun teacherDao(): TeacherDao

    companion object {
        @Volatile
        private var instance: CollegeDatabase? = null

        fun getDatabase(context: Context): CollegeDatabase {
            return instance ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    CollegeDatabase::class.java,
                    "college_database"
                ).build()
                instance = db
                db
            }
        }
    }
}