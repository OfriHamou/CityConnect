package com.example.cityconnect.model.remote.retrofit

import com.google.gson.annotations.SerializedName

data class GeoapifyPlacesResponse(
    val features: List<FeatureDto> = emptyList(),
)

data class FeatureDto(
    val properties: PropertiesDto = PropertiesDto(),
)

data class PropertiesDto(
    @SerializedName("place_id")
    val placeId: String = "",
    val name: String? = null,
    val formatted: String? = null,
    val lat: Double? = null,
    val lon: Double? = null,
    val categories: List<String>? = null,
    val phone: String? = null,
)

