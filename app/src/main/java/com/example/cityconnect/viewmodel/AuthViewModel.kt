package com.example.cityconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cityconnect.base.AuthState
import com.example.cityconnect.model.repositories.AuthRepository

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository(),
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

    fun register(email: String, pass: String) {
        _state.value = AuthState.Loading
        repository.register(email, pass) { result ->
            _state.postValue(
                result.fold(
                    onSuccess = { AuthState.Success },
                    onFailure = { e -> AuthState.Error(e.message ?: "Register failed") },
                ),
            )
        }
    }

    fun logout() {
        repository.logout()
        _state.value = AuthState.Idle
    }
}