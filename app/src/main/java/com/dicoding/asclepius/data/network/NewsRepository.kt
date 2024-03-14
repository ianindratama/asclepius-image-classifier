package com.dicoding.asclepius.data.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData

class NewsRepository {

    fun getNews(): LiveData<Result<NewsResponse?>> = liveData {
        emit(Result.Loading)
        val returnValue = MutableLiveData<Result<NewsResponse?>>()
        val response = RetrofitInstance.API_OBJECT.getNews()
        if (response.isSuccessful){
            returnValue.value = Result.Success(response.body())
            emitSource(returnValue)
        } else {
            val error = "An Error has occurred, please try again later"
            returnValue.value = Result.Error(error)
            emitSource(returnValue)
        }
    }

    companion object{

        @Volatile
        private var instance: NewsRepository? = null

        fun getInstance(): NewsRepository {
            return instance ?: synchronized(this) {
                if (instance == null){
                    instance = NewsRepository()
                }
                return instance as NewsRepository
            }
        }

    }

}