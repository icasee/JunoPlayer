package com.poktsun.junoplayer

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import timber.log.Timber

class JunoLifecycle(val className:String): LifecycleObserver {
    fun registerLifecycle(lifecycle: Lifecycle){
        lifecycle.addObserver(this)
    }

    fun unregisterLifecycle(lifecycle: Lifecycle){
        onDestroy()
        lifecycle.removeObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate(){
        Timber.d("-11, onCreate:%s", className)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart(){
        Timber.d("-17, onStart:%s",className)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause(){
        Timber.d("-22, onPause:%s",className)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume(){
        Timber.d("-27, onResume:%s",className)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop(){
        Timber.d("-32, onStop:%s",className)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(){
        Timber.d("-37, onDestroy:%s",className)
    }

}