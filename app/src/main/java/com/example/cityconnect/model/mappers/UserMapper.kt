package com.example.cityconnect.model.mappers

import com.example.cityconnect.model.schemas.User
import com.example.cityconnect.model.schemas.UserEntity

fun User.toEntity(): UserEntity = UserEntity(
    uid = uid,
    email = email,
    fullName = fullName,
    avatarUrl = avatarUrl,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun UserEntity.toDomain(): User = User(
    uid = uid,
    email = email,
    fullName = fullName,
    avatarUrl = avatarUrl,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

