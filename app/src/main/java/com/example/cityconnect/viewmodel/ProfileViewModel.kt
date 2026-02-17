package com.example.cityconnect.viewmodel

import androidx.lifecycle.LiveData
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

    private var currentUid: String? = null
    private var _user: LiveData<User?> = MutableLiveData(null)
    val user: LiveData<User?> = _user

    fun loadProfile() {
        val uid = authRepository.currentUid()
        if (uid.isNullOrBlank()) {
            _error.value = "Not logged in"
            _user = MutableLiveData(null)
            currentUid = null
            return
        }

        if (currentUid != uid) {
            currentUid = uid
            _user = userRepository.observeLocalUser(uid)
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