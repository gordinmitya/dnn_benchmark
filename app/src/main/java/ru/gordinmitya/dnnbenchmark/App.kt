package ru.gordinmitya.dnnbenchmark

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        val DEBUG = BuildConfig.DEBUG

        lateinit var instance: Application
    }
}