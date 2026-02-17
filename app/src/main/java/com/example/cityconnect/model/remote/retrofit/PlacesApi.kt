package com.example.cityconnect.model.remote.retrofit

import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApi {

    @GET("v2/places")
    suspend fun getPlaces(
        @Query("categories") categories: String,
        @Query("filter") filter: String,
        @Query("limit") limit: Int = 30,
        @Query("apiKey") apiKey: String,
    ): GeoapifyPlacesResponse
}

