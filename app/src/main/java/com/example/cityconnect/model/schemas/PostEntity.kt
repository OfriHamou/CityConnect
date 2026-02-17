package com.example.cityconnect.model.schemas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey
    val id: String,
    val ownerId: String,
    val ownerName: String,
    val ownerAvatarUrl: String,
    val text: String,
    val imageUrl: String?,
    val createdAt: Long,
    val updatedAt: Long,
)

