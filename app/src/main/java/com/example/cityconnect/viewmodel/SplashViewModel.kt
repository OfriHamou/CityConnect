package com.example.cityconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cityconnect.base.SplashDestination
import com.example.cityconnect.model.repositories.AuthRepository

class SplashViewModel(
    private val repository: AuthRepository = AuthRepository(),
) : ViewModel() {

    private val _destination = MutableLiveData<SplashDestination>()
    val destination: LiveData<SplashDestination> = _destination

    fun decideDestination() {
        _destination.value = if (repository.isLoggedIn()) {
            SplashDestination.ToMain
        } else {
            SplashDestination.ToLogin
        }
    }
}