package com.dicoding.asclepius.data.local

import android.content.Context
import androidx.lifecycle.LiveData

class AnalysisResultRepository(private val analysisResultDao: AnalysisResultDao) {

    fun insertAnalysisResult(analysisResult: AnalysisResult): Long{
        return analysisResultDao.insert(analysisResult)
    }

    fun getAnalysisResults(): LiveData<List<AnalysisResult>>{
        return analysisResultDao.getAnalysisResults()
    }

    fun deleteAnalysisResult(analysisResult: AnalysisResult){
        return analysisResultDao.delete(analysisResult)
    }

    companion object{

        @Volatile
        private var instance: AnalysisResultRepository? = null

        fun getInstance(context: Context): AnalysisResultRepository {
            return instance ?: synchronized(this) {
                if (instance == null){
                    val database = AnalysisResultDatabase.getInstance(context)
                    instance = AnalysisResultRepository(database.analysisResultDao())
                }
                return instance as AnalysisResultRepository
            }
        }

    }

}