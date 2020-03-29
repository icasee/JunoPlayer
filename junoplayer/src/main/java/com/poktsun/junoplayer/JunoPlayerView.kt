package com.poktsun.junoplayer

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.google.ads.interactivemedia.v3.api.AdEvent.AdEventType.ALL_ADS_COMPLETED
import com.google.ads.interactivemedia.v3.api.AdEvent.AdEventType.LOADED
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import timber.log.Timber

class JunoPlayerView : PlayerView, LifecycleObserver {
    var title: String = this::class.java.simpleName

    /**
     * for fragment lifecycle
     */
    var lifecycleOwner: LifecycleOwner = (context as LifecycleOwner)
        set(value) {
            if (value == field) {
                return
            }
            field.lifecycle.removeObserver(this)
            value.lifecycle.addObserver(this)
            field = value
        }

    //private val disposables = CompositeDisposable()
    private var tvPlayerPanel: View? = null
    private var tvPlayerLive: View? = null
    private var tvPlayerSpeaker: ImageView? = null

    private val junoPlayer = JunoPlayer(context)
    private val lifecycle: Lifecycle
        get() {
            return lifecycleOwner.lifecycle
        }

    var volume: Float
        set(value) {
            tvPlayerSpeaker?.setImageResource(
                if (value == 1f) R.drawable.ip_speaker_on else R.drawable.ip_speaker_off
            )
            junoPlayer.volume = value
        }
        get() = junoPlayer.volume

    var listener: JunoPlayerListener? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        tvPlayerPanel = findViewById(R.id.exo_panel)
        tvPlayerLive = findViewById(R.id.exo_live_text)
        tvPlayerSpeaker = findViewById(R.id.exo_speaker)
        tvPlayerSpeaker?.setOnClickListener {
            volume = if (volume == 1f) 0f else 1f
        }

        //playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH)
        //super.setUseController(false)
        super.setPlayer(junoPlayer.getPlayer())
        super.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT)
        super.setKeepScreenOn(true)
        junoPlayer.listener = object : JunoPlayerListener {
            override fun onError(error: Throwable?) {
                listener?.onError(error)
            }

            override fun onPlay() {
                listener?.onPlay()
            }

            override fun onPause() {
                listener?.onPause()
            }

            override fun onEnded() {
                listener?.onEnded()
            }

            override fun onBuffering() {
                listener?.onBuffering()
            }
        }
        lifecycle.addObserver(this)
        Timber.d("-84, :%s", lifecycle)
    }

    fun play(url: String, extension: String? = null) {
        junoPlayer.play(url, extension)
    }

    fun playWithAds(videoUrl: String, adTagUrl: String) {
        val imaAdsLoader = ImaAdsLoader(context, Uri.parse(adTagUrl))
        imaAdsLoader.setPlayer(junoPlayer.getPlayer())
        imaAdsLoader.adsLoader?.run {
            addAdsLoadedListener { adsManagerEvent ->
                adsManagerEvent?.adsManager?.run {
                    addAdEventListener { adEvent ->
                        when (adEvent?.type) {
                            LOADED -> listener?.onAdLoaded()
                            ALL_ADS_COMPLETED -> listener?.onAdCompleted()
                            else -> Timber.d("-94, playWithAds:%s", adEvent?.type)
                        }
                    }
                    addAdErrorListener { adEvent ->
                        listener?.onError(adEvent.error)
                    }
                }
            }
            addAdErrorListener { adEvent ->
                listener?.onError(adEvent.error)
            }
        }

        val adsMediaSource = junoPlayer.buildAdsMediaSource(
            videoUrl,
            imaAdsLoader,
            this
        )
        junoPlayer.prepare(adsMediaSource)
    }

    val isPlaying: Boolean
        get () = player.playWhenReady && player.playbackState == Player.STATE_READY

    //Lifecycle
    override fun onDetachedFromWindow() {
        junoPlayer.pause()
        //disposables.clear()
        junoPlayer.release()
        lifecycle.removeObserver(this)
        super.onDetachedFromWindow()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onLifecycleResume() {
        Timber.d("-144, onLifecycleResume:%s", title)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onLifecyclePause() {
        junoPlayer.pause()
        //disposables.clear()
        Timber.d("-151, onLifecyclePause:%s", title)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onLifecycleDestroy() {
        junoPlayer.release()
        Timber.d("-157, onDestroy:%s", title)
    }

}