package com.dicoding.asclepius.data.network

import com.dicoding.asclepius.BuildConfig
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("top-headlines")
    suspend fun getNews(
        @Query("q") query: String = "cancer",
        @Query("category") category: String = "health",
        @Query("language") language: String = "en",
        @Query("apiKey") apiKey: String = BuildConfig.NEWS_API_KEY
    ) : Response<NewsResponse>

}