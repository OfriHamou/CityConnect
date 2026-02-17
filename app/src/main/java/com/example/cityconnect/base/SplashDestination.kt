package com.example.cityconnect.base

sealed class SplashDestination {
    data object ToMain : SplashDestination()
    data object ToLogin : SplashDestination()
}

