package com.poktsun.junoplayer

interface JunoPlayerListener {
    var onPlayerStateChangedListener: ((playerState: Int, error: Throwable?) -> Unit)?

    fun setPlayerStateChangedListener(listener: ((playerState: Int, error: Throwable?) -> Unit)) {
        onPlayerStateChangedListener = listener
    }
}

