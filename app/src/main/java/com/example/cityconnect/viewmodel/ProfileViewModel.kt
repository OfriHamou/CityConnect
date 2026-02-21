package com.example.cityconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cityconnect.model.repositories.AuthRepository
import com.example.cityconnect.model.repositories.UserRepository
import com.example.cityconnect.model.repositories.PostRepository
import com.example.cityconnect.model.schemas.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val postRepository: PostRepository = PostRepository(),
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val userMediator = MediatorLiveData<User?>()
    val user: LiveData<User?> = userMediator
    private val postsCountMediator = MediatorLiveData<Int>()
    val totalPosts: LiveData<Int> = postsCountMediator
    private val memberSinceMediator = MediatorLiveData<String>()
    val memberSince: LiveData<String> = memberSinceMediator

    private var currentUid: String? = null
    private var currentSource: LiveData<User?>? = null
    private var currentPostsCountSource: LiveData<Int>? = null

    fun loadProfile() {
        val uid = authRepository.currentUid()
        if (uid.isNullOrBlank()) {
            _error.value = "Not logged in"
            currentUid = null
            currentSource?.let { userMediator.removeSource(it) }
            currentPostsCountSource?.let { postsCountMediator.removeSource(it) }
            currentSource = null
            currentPostsCountSource = null
            userMediator.value = null
            postsCountMediator.value = 0
            memberSinceMediator.value = ""
            return
        }

        if (currentUid != uid) {
            currentUid = uid

            currentSource?.let { userMediator.removeSource(it) }
            val newSource = userRepository.observeLocalUser(uid)
            currentSource = newSource
            userMediator.addSource(newSource) { u ->
                userMediator.value = u
                memberSinceMediator.value = formatDate(u?.createdAt ?: 0L)
            }

            currentPostsCountSource?.let { postsCountMediator.removeSource(it) }
            val postsCountSource = postRepository.observeMyPostsCount(uid)
            currentPostsCountSource = postsCountSource
            postsCountMediator.addSource(postsCountSource) { count ->
                postsCountMediator.value = count
            }
        }
        _loading.value = true
        _error.value = null

        userRepository.getUser(uid) { result ->
            _loading.postValue(false)
            result.onFailure { e ->
                _error.postValue(e.message ?: "Failed to refresh profile")
            }
        }
        postRepository.refreshPosts { /* ignore */ }
    }

    private fun formatDate(epochMillis: Long): String {
        if (epochMillis <= 0L) return ""
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(epochMillis))
    }
}