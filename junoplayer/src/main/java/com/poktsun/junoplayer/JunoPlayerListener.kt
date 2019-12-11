package com.poktsun.junoplayer

interface JunoPlayerListener {
    fun onPlay() {}
    fun onPause() {}
    fun onAdLoaded() {}
    fun onAdCompleted() {}
}