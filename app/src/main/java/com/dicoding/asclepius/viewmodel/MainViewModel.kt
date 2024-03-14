package com.dicoding.asclepius.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.data.local.AnalysisResult
import com.dicoding.asclepius.data.local.AnalysisResultRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModelFactory private constructor(private val analysisResultRepository: AnalysisResultRepository)
    : ViewModelProvider.Factory{

    companion object {
        @Volatile

        private var instance: MainViewModelFactory? = null

        fun getInstance(context: Context): MainViewModelFactory =
            instance ?: synchronized(this){
                instance ?: MainViewModelFactory(
                    AnalysisResultRepository.getInstance(context)
                )
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(analysisResultRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }

}

class MainViewModel(private val analysisResultRepository: AnalysisResultRepository): ViewModel() {

    private val _newRecordID = MutableLiveData<Long?>(null)
    val newRecordID: LiveData<Long?> = _newRecordID

    fun insertAnalysisResult(analysisResult: AnalysisResult) {
        viewModelScope.launch(Dispatchers.IO) {
            val recordID = analysisResultRepository.insertAnalysisResult(analysisResult)
            withContext(Dispatchers.Main){
                _newRecordID.value = recordID
            }
        }
    }

}