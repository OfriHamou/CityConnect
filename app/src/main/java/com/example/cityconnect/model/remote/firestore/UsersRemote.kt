package com.example.cityconnect.model.remote.firestore

import android.util.Log
import com.example.cityconnect.model.schemas.User
import com.google.firebase.firestore.FirebaseFirestore

class UsersRemote(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {

    private val usersCollection = db.collection("users")

    fun createUser(
        uid: String,
        email: String,
        fullName: String,
        avatarUrl: String = "",
        callback: (Result<Unit>) -> Unit,
    ) {
        val now = System.currentTimeMillis()
        val user = User(
            uid = uid,
            email = email,
            fullName = fullName,
            avatarUrl = avatarUrl,
            createdAt = now,
            updatedAt = now,
        )

        usersCollection.document(uid)
            .set(user)
            .addOnSuccessListener { callback(Result.success(Unit)) }
            .addOnFailureListener { e ->
                Log.e("UsersRemote", "createUser failed", e)
                callback(Result.failure(e))
            }
    }

    fun getUser(uid: String, callback: (Result<User>) -> Unit) {
        usersCollection.document(uid)
            .get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.toObject(User::class.java)
                if (user == null) {
                    callback(Result.failure(IllegalStateException("User profile not found")))
                } else {
                    callback(Result.success(user))
                }
            }
            .addOnFailureListener { e ->
                Log.e("UsersRemote", "getUser failed", e)
                callback(Result.failure(e))
            }
    }

    fun updateUser(
        uid: String,
        fullName: String,
        avatarUrl: String,
        callback: (Result<Unit>) -> Unit,
    ) {
        val updates = mapOf(
            "fullName" to fullName,
            "avatarUrl" to avatarUrl,
            "updatedAt" to System.currentTimeMillis(),
        )

        usersCollection.document(uid)
            .update(updates)
            .addOnSuccessListener { callback(Result.success(Unit)) }
            .addOnFailureListener { e ->
                Log.e("UsersRemote", "updateUser failed", e)
                callback(Result.failure(e))
            }
    }
}
