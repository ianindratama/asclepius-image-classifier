package com.dicoding.asclepius.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AnalysisResult::class], version = 1, exportSchema = false)
abstract class AnalysisResultDatabase: RoomDatabase() {

    abstract fun analysisResultDao(): AnalysisResultDao

    companion object {
        @Volatile
        private var INSTANCE: AnalysisResultDatabase? = null

        fun getInstance(context: Context): AnalysisResultDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AnalysisResultDatabase::class.java,
                    "analysis_resultdb.db")
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}