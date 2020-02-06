package ru.gordinmitya.dnnbenchmark

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        const val DEBUG = true

        lateinit var instance: Application
    }
}