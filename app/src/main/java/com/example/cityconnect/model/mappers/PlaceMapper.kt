package com.example.cityconnect.model.mappers

import com.example.cityconnect.model.remote.retrofit.FeatureDto
import com.example.cityconnect.model.schemas.Place
import com.example.cityconnect.model.schemas.PlaceEntity

fun PlaceEntity.toDomain(): Place = Place(
    id = id,
    name = name,
    category = category,
    address = address,
    phone = phone,
    imageUrl = imageUrl,
    lat = lat,
    lon = lon,
)

fun Place.toEntity(): PlaceEntity = PlaceEntity(
    id = id,
    name = name,
    category = category,
    address = address,
    phone = phone,
    imageUrl = imageUrl,
    lat = lat,
    lon = lon,
)

fun FeatureDto.toDomain(categoryLabel: String): Place {
    val p = properties
    return Place(
        id = p.placeId,
        name = p.name ?: "",
        category = categoryLabel,
        address = p.formatted ?: "",
        phone = p.phone ?: "",
        imageUrl = null,
        lat = p.lat ?: 0.0,
        lon = p.lon ?: 0.0,
    )
}

