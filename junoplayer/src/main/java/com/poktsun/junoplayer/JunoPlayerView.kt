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
import com.google.ads.interactivemedia.v3.api.AdEvent.AdEventType.CONTENT_PAUSE_REQUESTED
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class JunoPlayerView : PlayerView, LifecycleObserver {
    private val disposables = CompositeDisposable()
    private var tvPlayerPanel: View? = null
    private var tvPlayerLive: View? = null
    private var tvPlayerSpeaker: ImageView? = null
    //var tvPlayerTimer: TextView? = null

    private val junoPlayer = JunoPlayer(context)
    private val lifecycle: Lifecycle
        get() {
            return (context as LifecycleOwner).lifecycle
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

    //TD
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

        //playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
        super.setPlayer(junoPlayer.getPlayer())
        super.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT)
        super.setKeepScreenOn(true)
        //super.setUseController(false)
        player?.addListener(object: Player.EventListener{
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when (playbackState) {
                    Player.STATE_READY -> {
                        if (playWhenReady) {
                            listener?.onPlay()
                            Timber.d("-87 , PLAY : %s", 1)
                        } else {
                            listener?.onPause()
                            Timber.d("-87 , PAUSE : %s", 2)
                        }
                    }
                    Player.STATE_BUFFERING -> Timber.d("-107 , onPlayerStateChanged : %s", 4)
                    else -> Timber.e("-94 , onPlayerStateChanged : %s", playbackState)
                }
                Timber.d("-57, onPlayerStateChanged:%s", playbackState)
            }
        })

        lifecycle.addObserver(this)
    }

    fun play(url: String) {
        junoPlayer.play(url)
    }

    fun playWithAds(videoUrl: String, adTagUrl: String) {
        val imaAdsLoader = ImaAdsLoader(context, Uri.parse(adTagUrl))
        imaAdsLoader.setPlayer(junoPlayer.getPlayer())
        imaAdsLoader.adsLoader.addAdsLoadedListener {adsManagerEvent ->
            adsManagerEvent?.adsManager?.addAdEventListener { adEvent ->
                when (adEvent?.type) {
                    CONTENT_PAUSE_REQUESTED -> listener?.onAdLoaded()
                    ALL_ADS_COMPLETED -> listener?.onAdCompleted()
                    else -> Timber.d("-120 , playWidthAds : %s", adEvent?.type)
                }
            }
        }

        val adsMediaSource = junoPlayer.buildAdsMediaSource(
            videoUrl,
            imaAdsLoader,
            this)
        junoPlayer.prepare(adsMediaSource)
    }

    //Lifecycle
    override fun onDetachedFromWindow() {
        junoPlayer.pause()
        disposables.clear()
        junoPlayer.release()
        lifecycle.removeObserver(this)
        super.onDetachedFromWindow()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onLifecyclePause() {
        junoPlayer.pause()
        disposables.clear()
        Timber.d("-22, onPause:%s", 1)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onLifecycleResume() {
        //setProgress()
        Timber.d("-27, onResume:%s", 2)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onLifecycleDestroy() {
        junoPlayer.release()
        Timber.d("-37, onDestroy:%s", 3)
    }

}