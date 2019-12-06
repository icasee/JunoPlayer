package com.poktsun.junoplayer

import android.content.Context
import android.util.AttributeSet
import com.google.android.exoplayer2.ui.PlayerView
import timber.log.Timber

class JunoPlayerView: PlayerView{
    private val junoPlayer by lazy {
        JunoPlayer(context)
    }

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        Timber.d("-16, :%s", junoPlayer)
        player = junoPlayer.getPlayer()
        junoPlayer.tvPlayerCurrentTime = findViewById(R.id.exo_current_time)
        junoPlayer.tvPlayerEndTime = findViewById(R.id.exo_end_time)

    }

    fun prepare(url: String, playWhenReady: Boolean = true) {
        junoPlayer.prepare(url, playWhenReady)
    }
}