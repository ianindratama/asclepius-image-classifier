package com.dicoding.asclepius.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.data.local.AnalysisResult
import com.dicoding.asclepius.data.local.AnalysisResultRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResultViewModelFactory private constructor(private val analysisResultRepository: AnalysisResultRepository)
    : ViewModelProvider.Factory{

    companion object {
        @Volatile

        private var instance: ResultViewModelFactory? = null

        fun getInstance(context: Context): ResultViewModelFactory =
            instance ?: synchronized(this){
                instance ?: ResultViewModelFactory(
                    AnalysisResultRepository.getInstance(context)
                )
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(ResultViewModel::class.java) -> {
                ResultViewModel(analysisResultRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }

}

class ResultViewModel(private val analysisResultRepository: AnalysisResultRepository): ViewModel() {

    fun deleteAnalysisResult(analysisResult: AnalysisResult){
        viewModelScope.launch(Dispatchers.IO) {
            analysisResultRepository.deleteAnalysisResult(analysisResult)
        }
    }

}