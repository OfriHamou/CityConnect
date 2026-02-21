package com.example.cityconnect.model.repositories

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.cityconnect.base.MyApplication
import com.example.cityconnect.model.dao.UserDao
import com.example.cityconnect.model.local.AppDatabase
import com.example.cityconnect.model.mappers.toDomain
import com.example.cityconnect.model.mappers.toEntity
import com.example.cityconnect.model.remote.firestore.UsersRemote
import com.example.cityconnect.model.remote.storage.ImagesRemote
import com.example.cityconnect.model.schemas.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class UserRepository(
    private val remote: UsersRemote = UsersRemote(),
    private val imagesRemote: ImagesRemote = ImagesRemote(),
    private val userDao: UserDao = AppDatabase.getInstance(MyApplication.appContext()).userDao(),
    private val postRepository: PostRepository = PostRepository(),
) {

    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun observeLocalUser(uid: String): LiveData<User?> {
        val out = MediatorLiveData<User?>()
        out.addSource(userDao.getUser(uid)) { entity ->
            out.value = entity?.toDomain()
        }
        return out
    }

    fun saveUserToLocal(user: User) {
        ioScope.launch {
            userDao.upsert(user.toEntity())
        }
    }

    fun createUser(
        uid: String,
        email: String,
        fullName: String,
        avatarUrl: String = "",
        callback: (Result<Unit>) -> Unit,
    ) {
        remote.createUser(uid, email, fullName, avatarUrl) { result ->
            result.onSuccess {
                val now = System.currentTimeMillis()
                // cache locally too
                saveUserToLocal(
                    User(
                        uid = uid,
                        email = email,
                        fullName = fullName,
                        avatarUrl = avatarUrl,
                        createdAt = now,
                        updatedAt = now,
                    ),
                )
            }
            callback(result)
        }
    }

    fun getUser(uid: String, callback: (Result<User>) -> Unit) {
        remote.getUser(uid) { result ->
            result.onSuccess { user ->
                saveUserToLocal(user)
            }
            callback(result)
        }
    }

    fun updateUser(
        uid: String,
        fullName: String,
        avatarUri: Uri?,
        callback: (Result<Unit>) -> Unit,
    ) {
        if (avatarUri != null) {
            imagesRemote.uploadAvatar(uid, avatarUri) { uploadResult ->
                uploadResult.fold(
                    onSuccess = { url ->
                        remote.updateUser(uid, fullName, url) { updateResult ->
                            updateResult.onSuccess {
                                // Refresh local user cache
                                getUser(uid) { /* ignore */ }
                                // Fan-out user changes into existing posts
                                postRepository.updateOwnerInfoForAllPosts(uid, fullName, url) { /* ignore */ }
                            }
                            callback(updateResult)
                        }
                    },
                    onFailure = { e -> callback(Result.failure(e)) },
                )
            }
        } else {
            ioScope.launch {
                val existingAvatarUrl = userDao.getUserOnce(uid)?.avatarUrl.orEmpty()
                remote.updateUser(uid, fullName, existingAvatarUrl) { result ->
                    result.onSuccess {
                        getUser(uid) { /* ignore */ }
                        postRepository.updateOwnerInfoForAllPosts(uid, fullName, existingAvatarUrl) { /* ignore */ }
                    }
                    callback(result)
                }
            }
        }
    }

    // keep old signature for compatibility
    fun updateUser(uid: String, fullName: String, avatarUrl: String, callback: (Result<Unit>) -> Unit) {
        remote.updateUser(uid, fullName, avatarUrl) { result ->
            result.onSuccess { getUser(uid) { /* ignore */ } }
            callback(result)
        }
    }
}