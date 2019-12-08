package com.poktsun.junoplayer

import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.google.ads.interactivemedia.v3.api.AdEvent.AdEventType.ALL_ADS_COMPLETED
import com.google.ads.interactivemedia.v3.api.AdEvent.AdEventType.CONTENT_PAUSE_REQUESTED
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.ads.AdsLoader
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.util.concurrent.TimeUnit

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

    var mode: Int = 0
        set(value) {
            when(value) {
                1 -> {
                    tvPlayerLive?.visibility = View.VISIBLE
                    tvPlayerPanel?.visibility = View.GONE
                    Timber.d("-48, :%s", 1)
                }
                else -> {
                    tvPlayerLive?.visibility = View.GONE
                    tvPlayerPanel?.visibility = View.VISIBLE
                    Timber.d("-48, :%s", 2)
                }
            }
            field = value
        }

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
            if (junoPlayer.volume == 1f) {
                junoPlayer.volume = 0f
                tvPlayerSpeaker?.setImageResource(R.drawable.ip_speaker_off)
            } else {
                junoPlayer.volume = 1f
                tvPlayerSpeaker?.setImageResource(R.drawable.ip_speaker_on)
            }
        }

        //playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
        super.setPlayer(junoPlayer.getPlayer())
        super.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT)
        super.setKeepScreenOn(true)
        //super.setUseController(false)

        lifecycle.addObserver(this)
    }

    fun play(url: String) {
        junoPlayer.play(url)
    }

    fun playWithAds(videoUrl: String, adTagUrl: String) {
        val imaAdsLoader = ImaAdsLoader(context, Uri.parse(adTagUrl))
        imaAdsLoader.setPlayer(junoPlayer.getPlayer())
        imaAdsLoader.adsLoader.addAdsLoadedListener {
            it?.adsManager?.addAdEventListener { adEvent ->
                when (adEvent?.type) {
                    CONTENT_PAUSE_REQUESTED -> junoPlayer.volume = 0f
                    ALL_ADS_COMPLETED -> junoPlayer.volume = 1f
                    else -> Timber.d("-120 , playWidthAds : %s", adEvent?.type)
                }
                Timber.d("-99 , onAdEvent : %s", adEvent?.type)
            }
        }

        val adsMediaSource = junoPlayer.buildAdsMediaSource(
            videoUrl,
            imaAdsLoader,
            this)
        junoPlayer.prepare(adsMediaSource)
    }

//    private fun setProgress() {
//        disposables.clear()
//        Timber.e("-87, setProgress:%s", 23)
//        val d = Observable.interval(1, TimeUnit.SECONDS)
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe {
//                setPlayerState()
//            }
//
//        disposables.add(d)
//    }
//
//    private fun setPlayerState() {
//        val currentPosition = stringForTime(junoPlayer.currentPosition)
//        val duration = stringForTime(junoPlayer.duration)
//        tvPlayerTimer?.text = String.format("%s / %s", currentPosition, duration)
//        Timber.d("-83 , setProgress : %s", junoPlayer.duration)
//    }
//
//    private fun stringForTime(timeMs: Long): String {
//        val totalSeconds = timeMs / 1000
//        val seconds = totalSeconds % 60
//        val minutes = totalSeconds / 60 % 60
//        val hours = totalSeconds / 3600
//        return when {
//            hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, seconds)
//            seconds > 0 -> String.format("%02d:%02d", minutes, seconds)
//            else -> "0:00"
//        }
//    }

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