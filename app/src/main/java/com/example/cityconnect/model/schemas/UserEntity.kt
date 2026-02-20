package com.example.cityconnect.model.schemas

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val uid: String,
    val email: String,
    val fullName: String,
    val avatarUrl: String,
    val createdAt: Long,
    val updatedAt: Long,
)
