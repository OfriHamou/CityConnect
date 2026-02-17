package com.example.cityconnect.base

import android.app.Application

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        @Volatile
        private var instance: MyApplication? = null

        fun appContext() = requireNotNull(instance).applicationContext
    }
}