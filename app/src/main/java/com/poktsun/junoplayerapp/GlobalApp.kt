package com.poktsun.junoplayerapp

import android.app.Application
import timber.log.Timber

@Suppress("unused")
class GlobalApp : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

    }
}