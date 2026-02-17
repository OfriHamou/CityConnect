package com.example.cityconnect.model.schemas

data class User(
    val uid: String = "",
    val email: String = "",
    val fullName: String = "",
    val avatarUrl: String = "",
    val updatedAt: Long = 0L,
)

