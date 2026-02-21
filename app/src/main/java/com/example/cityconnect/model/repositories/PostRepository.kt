package com.example.cityconnect.model.repositories

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.cityconnect.base.MyApplication
import com.example.cityconnect.model.dao.PostDao
import com.example.cityconnect.model.dao.UserDao
import com.example.cityconnect.model.local.AppDatabase
import com.example.cityconnect.model.mappers.toDomain
import com.example.cityconnect.model.mappers.toEntity
import com.example.cityconnect.model.remote.firestore.PostsRemote
import com.example.cityconnect.model.remote.storage.ImagesRemote
import com.example.cityconnect.model.schemas.Post
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.UUID

class PostRepository(
    private val authRepository: AuthRepository = AuthRepository(),
    private val remote: PostsRemote = PostsRemote(),
    private val imagesRemote: ImagesRemote = ImagesRemote(),
    private val postDao: PostDao = AppDatabase.getInstance(MyApplication.appContext()).postDao(),
    private val userDao: UserDao = AppDatabase.getInstance(MyApplication.appContext()).userDao(),
) {

    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var postsListener: ListenerRegistration? = null

    fun observePosts(): LiveData<List<Post>> {
        val out = MediatorLiveData<List<Post>>()
        out.addSource(postDao.observeAll()) { entities ->
            out.value = entities.map { it.toDomain() }
        }
        return out
    }

    fun observeLocalPost(postId: String): LiveData<Post?> {
        val out = MediatorLiveData<Post?>()
        out.addSource(postDao.observeById(postId)) { entity ->
            out.value = entity?.toDomain()
        }
        return out
    }

    fun refreshPosts(callback: (Result<Unit>) -> Unit) {
        remote.fetchAllPosts { result ->
            result.fold(
                onSuccess = { posts ->
                    ioScope.launch {
                        // Upsert current snapshot
                        postDao.upsertAll(posts.map { it.toEntity() })
                        // Propagate deletions: remove anything not in the server snapshot
                        val ids = posts.map { it.id }
                        if (ids.isEmpty()) {
                            postDao.clearAll()
                        } else {
                            postDao.deleteAllNotIn(ids)
                        }
                    }
                    callback(Result.success(Unit))
                },
                onFailure = { e -> callback(Result.failure(e)) },
            )
        }
    }

    fun createPost(text: String, imageUri: Uri?, callback: (Result<Unit>) -> Unit) {
        val uid = authRepository.currentUid()
        if (uid.isNullOrBlank()) {
            callback(Result.failure(IllegalStateException("Not logged in")))
            return
        }

        ioScope.launch {
            val localUser = userDao.getUserOnce(uid)

            val now = System.currentTimeMillis()
            val postId = UUID.randomUUID().toString()

            fun createWithImageUrl(imageUrl: String?) {
                val post = Post(
                    id = postId,
                    ownerId = uid,
                    ownerName = localUser?.fullName ?: (authRepository.currentEmail() ?: ""),
                    ownerAvatarUrl = localUser?.avatarUrl ?: "",
                    text = text,
                    imageUrl = imageUrl,
                    createdAt = now,
                    updatedAt = now,
                )

                remote.createPost(post) { result ->
                    result.onSuccess {
                        ioScope.launch { postDao.upsert(post.toEntity()) }
                    }
                    callback(result)
                }
            }

            if (imageUri != null) {
                imagesRemote.uploadPostImage(postId, imageUri) { uploadResult ->
                    uploadResult.fold(
                        onSuccess = { url -> createWithImageUrl(url) },
                        onFailure = { e -> callback(Result.failure(e)) },
                    )
                }
            } else {
                createWithImageUrl(null)
            }
        }
    }

    fun updatePost(
        postId: String,
        newText: String,
        newImageUri: Uri?,
        callback: (Result<Unit>) -> Unit,
    ) {
        val uid = authRepository.currentUid()
        if (uid.isNullOrBlank()) {
            callback(Result.failure(IllegalStateException("Not logged in")))
            return
        }

        ioScope.launch {
            val localPost = postDao.getByIdOnce(postId)
            if (localPost == null) {
                callback(Result.failure(IllegalStateException("Post not found in cache")))
                return@launch
            }

            if (localPost.ownerId != uid) {
                callback(Result.failure(IllegalAccessException("Not allowed")))
                return@launch
            }

            fun updateWithImageUrl(imageUrl: String?) {
                val updated = localPost.toDomain().copy(
                    text = newText,
                    imageUrl = imageUrl ?: localPost.imageUrl,
                    updatedAt = System.currentTimeMillis(),
                )

                remote.updatePost(updated) { result ->
                    result.onSuccess {
                        ioScope.launch { postDao.upsert(updated.toEntity()) }
                    }
                    callback(result)
                }
            }

            if (newImageUri != null) {
                imagesRemote.uploadPostImage(postId, newImageUri) { uploadResult ->
                    uploadResult.fold(
                        onSuccess = { url -> updateWithImageUrl(url) },
                        onFailure = { e -> callback(Result.failure(e)) },
                    )
                }
            } else {
                updateWithImageUrl(null)
            }
        }
    }

    // Keep old signatures for compatibility (delegate)
    fun createPost(text: String, callback: (Result<Unit>) -> Unit) = createPost(text, null, callback)

    fun updatePost(postId: String, newText: String, callback: (Result<Unit>) -> Unit) =
        updatePost(postId, newText, null, callback)

    fun deletePost(postId: String, callback: (Result<Unit>) -> Unit) {
        val uid = authRepository.currentUid()
        if (uid.isNullOrBlank()) {
            callback(Result.failure(IllegalStateException("Not logged in")))
            return
        }

        ioScope.launch {
            val localPost = postDao.getByIdOnce(postId)
            if (localPost == null) {
                callback(Result.failure(IllegalStateException("Post not found in cache")))
                return@launch
            }

            if (localPost.ownerId != uid) {
                callback(Result.failure(IllegalAccessException("Not allowed")))
                return@launch
            }

            remote.deletePost(postId) { result ->
                result.onSuccess {
                    ioScope.launch { postDao.deleteById(postId) }
                }
                callback(result)
            }
        }
    }

    fun observeMyPostsCount(ownerId: String): LiveData<Int> {
        val out = MediatorLiveData<Int>()
        out.addSource(postDao.observeCountByOwner(ownerId)) { count ->
            out.value = count
        }
        return out
    }

    fun updateOwnerInfoForAllPosts(
        ownerId: String,
        ownerName: String,
        ownerAvatarUrl: String,
        callback: (Result<Unit>) -> Unit,
    ) {
        remote.updateOwnerInfoForAllPosts(ownerId, ownerName, ownerAvatarUrl) { result ->
            result.onSuccess {
                ioScope.launch {
                    postDao.updateOwnerInfo(ownerId, ownerName, ownerAvatarUrl)
                }
            }
            callback(result)
        }
    }

    fun startPostsRealtimeSync() {
        if (postsListener != null) return

        postsListener = remote.observeAllPosts { result ->
            result.onSuccess { posts ->
                ioScope.launch {
                    postDao.upsertAll(posts.map { it.toEntity() })

                    val ids = posts.map { it.id }
                    if (ids.isEmpty()) {
                        postDao.clearAll()
                    } else {
                        postDao.deleteAllNotIn(ids)
                    }
                }
            }
        }
    }

    fun stopPostsRealtimeSync() {
        postsListener?.remove()
        postsListener = null
    }
}