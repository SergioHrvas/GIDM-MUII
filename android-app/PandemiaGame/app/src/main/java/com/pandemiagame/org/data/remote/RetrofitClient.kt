package com.pandemiagame.org.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    //private const val BASE_URL = "http://192.168.50.232:8000/"
    private const val BASE_URL = "http://192.168.1.136:8000/"
    //private const val BASE_URL = "http://192.168.33.232:8000/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}