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
    private var playListener: (() -> Unit)? = null
    private var pauseListener: (() -> Unit)? = null
    private var progressListener: (() -> Unit)? = null
    private var loadedListener: (() -> Unit)? = null
    private var completeListener: (() -> Unit)? = null
    private var errorListener: ((Throwable?) -> Unit?)? = null
    private var adEventListener: ((String?) -> Unit?)? = null
    private var endedListener: (() -> Unit?)? = null

    fun setOnPlay(listener: (() -> Unit)) {
        playListener = listener
    }

    fun setOnPause(listener: (() -> Unit)) {
        pauseListener = listener
    }

    fun setOnProgress(listener: (() -> Unit)) {
        progressListener = listener
    }

    fun setOnEnded(listener: (() -> Unit)) {
        endedListener = listener
    }

    fun setOnError(listener: ((error: Throwable?) -> Unit)) {
        errorListener = listener
    }

    fun setOnAdEvent(listener: ((type: String?) -> Unit)) {
        adEventListener = listener
    }

    fun setOnAdLoaded(listener: (() -> Unit)) {
        loadedListener = listener
    }

    fun setOnAdComplete(listener: (() -> Unit)) {
        completeListener = listener
    }


    // Last Modified: 2020-05-14 23:34 ⓒ
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
    var shareButton: View? = null
    var screenButton: View? = null

    private val junoPlayer = JunoPlayer(context).apply {
        setOnPlay {
            playListener?.invoke()
        }
        setOnPause {
            pauseListener?.invoke()
        }
        setOnProgress {
            progressListener?.invoke()
        }
        setOnEnded {
            endedListener?.invoke()
        }
        setOnError {
            errorListener?.invoke(it)
        }
    }
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

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        screenButton = findViewById(R.id.exo_screen_button)
        shareButton = findViewById(R.id.exo_share_button)
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
//        setControllerVisibilityListener {
//            Timber.d("-80, VisibilityListener:%s", it)
//        }
        lifecycle.addObserver(this)
        Timber.d("-84, :%s", lifecycle)
    }

    fun play(url: String, extension: String? = null) {
        junoPlayer.play(url, extension)
    }

    fun play() {
        junoPlayer.play()
    }

    fun pause() {
        junoPlayer.pause()
    }

    fun stop() {
        junoPlayer.stop()
    }

    fun release() {
        junoPlayer.release()
    }

    fun playWithAds(videoUrl: String, adTagUrl: String) {
        val imaAdsLoader = ImaAdsLoader(context, Uri.parse(adTagUrl))
        imaAdsLoader.setPlayer(junoPlayer.getPlayer())
        imaAdsLoader.adsLoader?.run {
            addAdsLoadedListener { adsManagerEvent ->
                adsManagerEvent?.adsManager?.run {
                    addAdEventListener { adEvent ->
                        when (adEvent?.type) {
                            LOADED -> loadedListener?.invoke()
                            ALL_ADS_COMPLETED -> completeListener?.invoke()
                            else -> Timber.d("-94, playWithAds:%s", adEvent?.type)
                        }
                        adEventListener?.invoke(adEvent?.type?.name)
                        // Timber.d("-33, setOnProgress:%s", adEvent?.type?.name)
                    }
                    addAdErrorListener { adEvent ->
                        errorListener?.invoke(adEvent.error)
                    }
                }
            }
            addAdErrorListener { adEvent ->
                errorListener?.invoke(adEvent.error)
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
        get() = junoPlayer.isPlaying

    val isPlayingAd: Boolean
        get() = player.isPlayingAd

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

    //var listener: ((playerState: Int, error: Throwable?) -> Unit)? = null
}