package com.example.cityconnect.model.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.cityconnect.base.MyApplication
import com.example.cityconnect.model.dao.PostDao
import com.example.cityconnect.model.dao.UserDao
import com.example.cityconnect.model.local.AppDatabase
import com.example.cityconnect.model.mappers.toDomain
import com.example.cityconnect.model.mappers.toEntity
import com.example.cityconnect.model.remote.firestore.PostsRemote
import com.example.cityconnect.model.schemas.Post
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.UUID

class PostRepository(
    private val authRepository: AuthRepository = AuthRepository(),
    private val remote: PostsRemote = PostsRemote(),
    private val postDao: PostDao = AppDatabase.getInstance(MyApplication.appContext()).postDao(),
    private val userDao: UserDao = AppDatabase.getInstance(MyApplication.appContext()).userDao(),
) {

    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

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
                        postDao.upsertAll(posts.map { it.toEntity() })
                    }
                    callback(Result.success(Unit))
                },
                onFailure = { e -> callback(Result.failure(e)) },
            )
        }
    }

    fun createPost(text: String, callback: (Result<Unit>) -> Unit) {
        val uid = authRepository.currentUid()
        if (uid.isNullOrBlank()) {
            callback(Result.failure(IllegalStateException("Not logged in")))
            return
        }

        ioScope.launch {
            val localUser = userDao.getUserOnce(uid)

            val now = System.currentTimeMillis()
            val post = Post(
                id = UUID.randomUUID().toString(),
                ownerId = uid,
                ownerName = localUser?.fullName ?: (authRepository.currentEmail() ?: ""),
                ownerAvatarUrl = localUser?.avatarUrl ?: "",
                text = text,
                imageUrl = null,
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
    }

    fun updatePost(postId: String, newText: String, callback: (Result<Unit>) -> Unit) {
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

            val updated = localPost.toDomain().copy(
                text = newText,
                updatedAt = System.currentTimeMillis(),
            )

            remote.updatePost(updated) { result ->
                result.onSuccess {
                    ioScope.launch { postDao.upsert(updated.toEntity()) }
                }
                callback(result)
            }
        }
    }

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
}