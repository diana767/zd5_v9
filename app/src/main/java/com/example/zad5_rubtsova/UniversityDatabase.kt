package com.example.zad5_rubtsova

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [Student::class], version = 1)
abstract class UniversityDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao

    companion object {
        @Volatile
        private var INSTANCE: UniversityDatabase? = null

        fun getDatabase(context: Context): UniversityDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UniversityDatabase::class.java,
                    "university_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}