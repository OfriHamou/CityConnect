package com.example.cityconnect.model.schemas

data class Place(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val address: String = "",
    val phone: String = "",
    val imageUrl: String? = null,
    val lat: Double = 0.0,
    val lon: Double = 0.0,
)

