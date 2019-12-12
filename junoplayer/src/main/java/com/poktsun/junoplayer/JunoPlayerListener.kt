package com.poktsun.junoplayer

interface JunoPlayerListener {
    fun onError(error: Throwable?) {}
    fun onPlay() {}
    fun onPause() {}
    fun onEnded() {}
    fun onBuffering() {}
    fun onAdLoaded() {}
    fun onAdCompleted() {}
}