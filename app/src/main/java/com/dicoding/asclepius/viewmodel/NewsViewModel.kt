package com.dicoding.asclepius.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.data.network.ArticlesItem
import com.dicoding.asclepius.data.network.NewsRepository
import com.dicoding.asclepius.data.network.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewsViewModelFactory private constructor(private val newsRepository: NewsRepository)
    : ViewModelProvider.Factory{

    companion object {
        @Volatile

        private var instance: NewsViewModelFactory? = null

        fun getInstance(): NewsViewModelFactory =
            instance ?: synchronized(this){
                instance ?: NewsViewModelFactory(
                    NewsRepository.getInstance()
                )
            }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(NewsViewModel::class.java) -> {
                NewsViewModel(newsRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }

}

class NewsViewModel(newsRepository: NewsRepository): ViewModel() {

    private val _news = MutableLiveData<List<ArticlesItem?>?>(null)
    val news: LiveData<List<ArticlesItem?>?> = _news

    private val _errorStatus = MutableLiveData<String?>(null)
    val errorStatus = _errorStatus

    init {
        viewModelScope.launch(Dispatchers.IO) {
            newsRepository.getNews().asFlow().collect{
                when (it){
                    is Result.Loading -> {}
                    is Result.Error -> {
                        withContext(Dispatchers.Main){
                            _errorStatus.value = "An error has occurred, please try again later"
                        }
                    }
                    is Result.Success -> {
                        withContext(Dispatchers.Main){
                            _news.value = it.data?.articles
                        }
                    }
                }
            }
        }
    }

}