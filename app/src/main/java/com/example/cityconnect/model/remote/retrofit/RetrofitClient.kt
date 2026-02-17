package com.example.cityconnect.model.remote.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://api.geoapify.com/"

    private val httpClient: OkHttpClient by lazy {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val placesApi: PlacesApi by lazy {
        retrofit.create(PlacesApi::class.java)
    }
}

