package com.example.zad5_rubtsova

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [Teacher::class], version = 1)
abstract class TeacherDatabase : RoomDatabase() {
    abstract fun teacherDao(): TeacherDao

    companion object {
        @Volatile
        private var INSTANCE: TeacherDatabase? = null

        fun getInstance(context: Context): TeacherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TeacherDatabase::class.java,
                    "teacher_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}