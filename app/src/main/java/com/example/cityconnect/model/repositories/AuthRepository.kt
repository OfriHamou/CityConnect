package com.example.cityconnect.model.repositories

import android.util.Log
import com.google.firebase.auth.FirebaseAuth

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
) {

    fun isLoggedIn(): Boolean = auth.currentUser != null

    fun currentUid(): String? = auth.currentUser?.uid

    fun currentEmail(): String? = auth.currentUser?.email

    fun login(email: String, pass: String, callback: (Result<Unit>) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener { callback(Result.success(Unit)) }
            .addOnFailureListener { e ->
                Log.e("AuthRepository", "login failed", e)
                callback(Result.failure(e))
            }
    }

    fun register(email: String, pass: String, callback: (Result<Unit>) -> Unit) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { callback(Result.success(Unit)) }
            .addOnFailureListener { e ->
                Log.e("AuthRepository", "register failed", e)
                callback(Result.failure(e))
            }
    }

    fun logout() {
        auth.signOut()
    }
}