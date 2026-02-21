package com.example.cityconnect.model.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.cityconnect.BuildConfig
import com.example.cityconnect.base.MyApplication
import com.example.cityconnect.model.dao.PlaceDao
import com.example.cityconnect.model.local.AppDatabase
import com.example.cityconnect.model.mappers.toDomain
import com.example.cityconnect.model.mappers.toEntity
import com.example.cityconnect.model.remote.retrofit.RetrofitClient
import com.example.cityconnect.model.schemas.Place
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PlaceRepository(
    private val placeDao: PlaceDao = AppDatabase.getInstance(MyApplication.appContext()).placeDao(),
) {

    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun observePlaces(categoryLabel: String): LiveData<List<Place>> {
        val out = MediatorLiveData<List<Place>>()
        out.addSource(placeDao.observeByCategory(categoryLabel)) { entities ->
            out.value = entities.map { it.toDomain() }
        }
        return out
    }

    fun refreshPlaces(categoryLabel: String, callback: (Result<Unit>) -> Unit) {
        if (BuildConfig.GEOAPIFY_API_KEY.isBlank()) {
            callback(Result.failure(IllegalStateException("Missing GEOAPIFY_API_KEY")))
            return
        }
        val categories = when (categoryLabel) {
            "Restaurants" -> "catering.restaurant"
            "Businesses" -> "commercial"
            "Services" -> "service"
            else -> "catering.restaurant"
        }

        // Tel Aviv fixed circle: lon,lat,radius
        val filter = "circle:34.7818,32.0853,3000"
        val apiKey = BuildConfig.GEOAPIFY_API_KEY

        ioScope.launch {
            try {
                val response = RetrofitClient.placesApi.getPlaces(
                    categories = categories,
                    filter = filter,
                    limit = 30,
                    apiKey = apiKey,
                )

                val places = response.features
                    .map { feature -> feature.toDomain(categoryLabel) }
                    .filter { it.id.isNotBlank() }

                placeDao.replaceCategory(categoryLabel, places.map { it.toEntity() })
                callback(Result.success(Unit))
            } catch (e: Exception) {
                callback(Result.failure(e))
            }
        }
    }
}
