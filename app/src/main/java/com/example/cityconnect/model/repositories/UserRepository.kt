package com.example.cityconnect.model.repositories

import com.example.cityconnect.model.remote.firestore.UsersRemote
import com.example.cityconnect.model.schemas.User

class UserRepository(
    private val remote: UsersRemote = UsersRemote(),
) {

    fun createUser(
        uid: String,
        email: String,
        fullName: String,
        avatarUrl: String = "",
        callback: (Result<Unit>) -> Unit,
    ) {
        remote.createUser(uid, email, fullName, avatarUrl, callback)
    }

    fun getUser(uid: String, callback: (Result<User>) -> Unit) {
        remote.getUser(uid, callback)
    }

    fun updateUser(
        uid: String,
        fullName: String,
        avatarUrl: String,
        callback: (Result<Unit>) -> Unit,
    ) {
        remote.updateUser(uid, fullName, avatarUrl, callback)
    }
}