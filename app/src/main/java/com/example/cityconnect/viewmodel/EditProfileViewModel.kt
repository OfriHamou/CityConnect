package com.example.cityconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cityconnect.model.repositories.AuthRepository
import com.example.cityconnect.model.repositories.UserRepository
import com.example.cityconnect.model.schemas.User

class EditProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository(),
) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _saveSuccess = MutableLiveData(false)
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    fun loadUser() {
        val uid = authRepository.currentUid()
        if (uid.isNullOrBlank()) {
            _error.value = "Not logged in"
            _user.value = null
            return
        }

        _loading.value = true
        _error.value = null

        userRepository.getUser(uid) { result ->
            _loading.postValue(false)
            result.fold(
                onSuccess = { u -> _user.postValue(u) },
                onFailure = { e ->
                    _error.postValue(e.message ?: "Failed to load user")
                    _user.postValue(null)
                },
            )
        }
    }

    fun save(fullName: String) {
        val uid = authRepository.currentUid()
        if (uid.isNullOrBlank()) {
            _error.value = "Not logged in"
            return
        }

        _loading.value = true
        _error.value = null
        _saveSuccess.value = false

        val avatarUrl = _user.value?.avatarUrl ?: ""

        userRepository.updateUser(uid, fullName, avatarUrl) { result ->
            _loading.postValue(false)
            result.fold(
                onSuccess = { _saveSuccess.postValue(true) },
                onFailure = { e -> _error.postValue(e.message ?: "Failed to save") },
            )
        }
    }
}

