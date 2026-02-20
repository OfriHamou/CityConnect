package com.example.cityconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cityconnect.model.repositories.AuthRepository
import com.example.cityconnect.model.repositories.UserRepository
import com.example.cityconnect.model.schemas.User

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository(),
) : ViewModel() {

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val userMediator = MediatorLiveData<User?>()
    val user: LiveData<User?> = userMediator

    private var currentUid: String? = null
    private var currentSource: LiveData<User?>? = null

    fun loadProfile() {
        val uid = authRepository.currentUid()
        if (uid.isNullOrBlank()) {
            _error.value = "Not logged in"
            currentUid = null
            currentSource?.let { userMediator.removeSource(it) }
            currentSource = null
            userMediator.value = null
            return
        }

        if (currentUid != uid) {
            currentUid = uid

            currentSource?.let { userMediator.removeSource(it) }
            val newSource = userRepository.observeLocalUser(uid)
            currentSource = newSource
            userMediator.addSource(newSource) { u ->
                userMediator.value = u
            }
        }

        // Remote refresh -> UserRepository will cache into Room
        _loading.value = true
        _error.value = null

        userRepository.getUser(uid) { result ->
            _loading.postValue(false)
            result.onFailure { e ->
                _error.postValue(e.message ?: "Failed to refresh profile")
            }
        }
    }
}