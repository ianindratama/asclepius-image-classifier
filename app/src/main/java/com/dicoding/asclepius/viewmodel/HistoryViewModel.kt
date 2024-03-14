package com.dicoding.asclepius.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.data.local.AnalysisResult
import com.dicoding.asclepius.data.local.AnalysisResultRepository

class HistoryViewModelFactory private constructor(private val analysisResultRepository: AnalysisResultRepository)
    : ViewModelProvider.Factory{

    companion object {
        @Volatile

        private var instance: HistoryViewModelFactory? = null

        fun getInstance(context: Context): HistoryViewModelFactory =
            instance ?: synchronized(this){
                instance ?: HistoryViewModelFactory(
                    AnalysisResultRepository.getInstance(context)
                )
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(analysisResultRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }

}

class HistoryViewModel(private val analysisResultRepository: AnalysisResultRepository): ViewModel() {

    fun getAnalysisResult(): LiveData<List<AnalysisResult>> =
        analysisResultRepository.getAnalysisResults()

}