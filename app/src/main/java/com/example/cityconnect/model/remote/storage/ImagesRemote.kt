package com.example.cityconnect.model.remote.storage

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage

class ImagesRemote(
    private val storage: FirebaseStorage = FirebaseStorage.getInstance(),
) {

    fun uploadPostImage(postId: String, uri: Uri, callback: (Result<String>) -> Unit) {
        val ref = storage.reference.child("post_images/$postId.jpg")
        ref.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception ?: RuntimeException("Upload failed")
                }
                ref.downloadUrl
            }
            .addOnSuccessListener { downloadUri ->
                callback(Result.success(downloadUri.toString()))
            }
            .addOnFailureListener { e ->
                Log.e("ImagesRemote", "uploadPostImage failed", e)
                callback(Result.failure(e))
            }
    }

    fun uploadAvatar(userId: String, uri: Uri, callback: (Result<String>) -> Unit) {
        val ref = storage.reference.child("avatars/$userId.jpg")

        Log.d("ImagesRemote", "uploadAvatar userId=$userId uri=$uri path=${ref.path}")

        ref.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    val ex = task.exception
                    Log.e("ImagesRemote", "uploadAvatar putFile failed", ex)
                    throw ex ?: RuntimeException("Upload failed")
                }
                ref.downloadUrl
            }
            .addOnSuccessListener { downloadUri ->
                Log.d("ImagesRemote", "uploadAvatar success url=$downloadUri")
                callback(Result.success(downloadUri.toString()))
            }
            .addOnFailureListener { e ->
                Log.e("ImagesRemote", "uploadAvatar failed", e)
                callback(Result.failure(e))
            }
    }
}
