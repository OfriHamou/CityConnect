package com.example.cityconnect.model.schemas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val category: String,
    val address: String,
    val phone: String,
    val imageUrl: String?,
    val lat: Double,
    val lon: Double,
)

