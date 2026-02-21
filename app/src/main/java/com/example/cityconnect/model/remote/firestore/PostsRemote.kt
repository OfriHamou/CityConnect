package com.example.cityconnect.model.remote.firestore

import android.util.Log
import com.example.cityconnect.model.schemas.Post
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class PostsRemote(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {

    private val postsCollection = db.collection("posts")

    fun fetchAllPosts(callback: (Result<List<Post>>) -> Unit) {
        postsCollection
            .orderBy("createdAt")
            .get()
            .addOnSuccessListener { snapshot ->
                val posts = snapshot.documents.mapNotNull { it.toObject(Post::class.java) }
                    .sortedByDescending { it.createdAt }
                callback(Result.success(posts))
            }
            .addOnFailureListener { e ->
                Log.e("PostsRemote", "fetchAllPosts failed", e)
                callback(Result.failure(e))
            }
    }

    fun createPost(post: Post, callback: (Result<Unit>) -> Unit) {
        postsCollection.document(post.id)
            .set(post)
            .addOnSuccessListener { callback(Result.success(Unit)) }
            .addOnFailureListener { e ->
                Log.e("PostsRemote", "createPost failed", e)
                callback(Result.failure(e))
            }
    }

    fun updatePost(post: Post, callback: (Result<Unit>) -> Unit) {
        val updates = mapOf(
            "text" to post.text,
            "imageUrl" to post.imageUrl,
            "updatedAt" to post.updatedAt,
            "ownerName" to post.ownerName,
            "ownerAvatarUrl" to post.ownerAvatarUrl,
        )

        postsCollection.document(post.id)
            .update(updates)
            .addOnSuccessListener { callback(Result.success(Unit)) }
            .addOnFailureListener { e ->
                Log.e("PostsRemote", "updatePost failed", e)
                callback(Result.failure(e))
            }
    }

    fun deletePost(postId: String, callback: (Result<Unit>) -> Unit) {
        postsCollection.document(postId)
            .delete()
            .addOnSuccessListener { callback(Result.success(Unit)) }
            .addOnFailureListener { e ->
                Log.e("PostsRemote", "deletePost failed", e)
                callback(Result.failure(e))
            }
    }

    fun updateOwnerInfoForAllPosts(
        ownerId: String,
        ownerName: String,
        ownerAvatarUrl: String,
        callback: (Result<Unit>) -> Unit,
    ) {
        postsCollection
            .whereEqualTo("ownerId", ownerId)
            .get()
            .addOnSuccessListener { snapshot ->
                val batch = db.batch()
                snapshot.documents.forEach { doc ->
                    batch.update(
                        doc.reference,
                        mapOf(
                            "ownerName" to ownerName,
                            "ownerAvatarUrl" to ownerAvatarUrl,
                            // don't touch text/imageUrl/createdAt
                            "updatedAt" to System.currentTimeMillis(),
                        ),
                    )
                }

                batch.commit()
                    .addOnSuccessListener { callback(Result.success(Unit)) }
                    .addOnFailureListener { e ->
                        Log.e("PostsRemote", "updateOwnerInfoForAllPosts commit failed", e)
                        callback(Result.failure(e))
                    }
            }
            .addOnFailureListener { e ->
                Log.e("PostsRemote", "updateOwnerInfoForAllPosts query failed", e)
                callback(Result.failure(e))
            }
    }

    fun observeAllPosts(callback: (Result<List<Post>>) -> Unit): ListenerRegistration {
        return postsCollection
            .orderBy("createdAt")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("PostsRemote", "observeAllPosts failed", e)
                    callback(Result.failure(e))
                    return@addSnapshotListener
                }

                val posts = snapshot?.documents
                    ?.mapNotNull { it.toObject(Post::class.java) }
                    ?.sortedByDescending { it.createdAt }
                    ?: emptyList()

                callback(Result.success(posts))
            }
    }
}
