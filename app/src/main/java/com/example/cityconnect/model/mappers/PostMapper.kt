package com.example.cityconnect.model.mappers

import com.example.cityconnect.model.schemas.Post
import com.example.cityconnect.model.schemas.PostEntity

fun Post.toEntity(): PostEntity = PostEntity(
    id = id,
    ownerId = ownerId,
    ownerName = ownerName,
    ownerAvatarUrl = ownerAvatarUrl,
    text = text,
    imageUrl = imageUrl,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun PostEntity.toDomain(): Post = Post(
    id = id,
    ownerId = ownerId,
    ownerName = ownerName,
    ownerAvatarUrl = ownerAvatarUrl,
    text = text,
    imageUrl = imageUrl,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
