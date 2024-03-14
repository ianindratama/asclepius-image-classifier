package com.dicoding.asclepius.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AnalysisResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(analysisResult: AnalysisResult): Long

    @Query("SELECT * FROM analysis_result ORDER BY id DESC")
    fun getAnalysisResults(): LiveData<List<AnalysisResult>>

    @Query("SELECT * FROM analysis_result WHERE id=:id")
    fun getAnalysisResult(id: Int): AnalysisResult

    @Delete
    fun delete(analysisResult: AnalysisResult)
}