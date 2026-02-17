package com.example.cityconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cityconnect.base.AuthState
import com.example.cityconnect.model.repositories.AuthRepository
import com.example.cityconnect.model.repositories.UserRepository

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository(),
) : ViewModel() {

    private val _state = MutableLiveData<AuthState>(AuthState.Idle)
    val state: LiveData<AuthState> = _state

    fun login(email: String, pass: String) {
        _state.value = AuthState.Loading
        repository.login(email, pass) { result ->
            _state.postValue(
                result.fold(
                    onSuccess = { AuthState.Success },
                    onFailure = { e -> AuthState.Error(e.message ?: "Login failed") },
                ),
            )
        }
    }

    fun register(email: String, pass: String, fullName: String) {
        _state.value = AuthState.Loading
        repository.register(email, pass) { result ->
            result.fold(
                onSuccess = {
                    val uid = repository.currentUid()
                    val userEmail = repository.currentEmail() ?: email

                    if (uid.isNullOrBlank()) {
                        _state.postValue(AuthState.Error("Register succeeded but uid is missing"))
                        return@fold
                    }

                    userRepository.createUser(
                        uid = uid,
                        email = userEmail,
                        fullName = fullName,
                        avatarUrl = "",
                    ) { createResult ->
                        _state.postValue(
                            createResult.fold(
                                onSuccess = { AuthState.Success },
                                onFailure = { e ->
                                    AuthState.Error(e.message ?: "Profile creation failed")
                                },
                            ),
                        )
                    }
                },
                onFailure = { e ->
                    _state.postValue(AuthState.Error(e.message ?: "Register failed"))
                },
            )
        }
    }

    fun logout() {
        repository.logout()
        _state.value = AuthState.Idle
    }
}