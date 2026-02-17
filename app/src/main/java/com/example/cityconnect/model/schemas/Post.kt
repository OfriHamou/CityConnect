package com.example.cityconnect.model.schemas

data class Post(
    val id: String = "",
    val ownerId: String = "",
    val ownerName: String = "",
    val ownerAvatarUrl: String = "",
    val text: String = "",
    val imageUrl: String? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
)

