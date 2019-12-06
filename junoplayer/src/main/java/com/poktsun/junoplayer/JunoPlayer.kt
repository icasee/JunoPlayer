package com.poktsun.junoplayer

import android.content.Context
import android.net.Uri
import android.view.Surface
import android.widget.TextView
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber
import java.util.concurrent.TimeUnit

class JunoPlayer() : AnalyticsListener {
    private val disposables = CompositeDisposable()
    var tvPlayerCurrentTime: TextView? = null
    var tvPlayerEndTime: TextView? = null
    var tvPlayerView: PlayerView? = null
//        set(value) {
//            value?.player = exoPlayer
//        }

    private lateinit var dataSourceFactory: DefaultDataSourceFactory
    private lateinit var exoPlayer: SimpleExoPlayer

    constructor(context: Context) : this() {

        val userAgent = Util.getUserAgent(context, "nowNews")
        val videoTrackSelectionFactory: TrackSelection.Factory = AdaptiveTrackSelection.Factory()
        val rendererFactory = DefaultRenderersFactory(context)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        val loadControl = DefaultLoadControl.Builder().createDefaultLoadControl()

        dataSourceFactory = DefaultDataSourceFactory(context, userAgent)
        exoPlayer = ExoPlayerFactory.newSimpleInstance(context,
            rendererFactory,
            trackSelector,
            loadControl
        )
        exoPlayer.addAnalyticsListener(this)
    }

//    fun setPlayerView(playerView: PlayerView) = apply{
//        playerView.player = exoPlayer
//    }
//
    fun getPlayer(): SimpleExoPlayer = exoPlayer

    fun prepare(url: String, playWhenReady: Boolean = true) {
        val mediaSource = buildMediaSource(Uri.parse(url))
        exoPlayer.playWhenReady = playWhenReady
        exoPlayer.prepare(mediaSource)
        setProgress()
    }


    fun onResume() {
        tvPlayerView?.onResume()
    }

    fun onPause() {
        exoPlayer.playWhenReady = false
        tvPlayerView?.onPause()
    }

    fun onDestroy() {
        //disposables.clear()
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        return when (val type = Util.inferContentType(uri, null)) {
            C.TYPE_DASH -> DashMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
            C.TYPE_SS -> SsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
            C.TYPE_HLS -> HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
            //mp3, mp4...
            C.TYPE_OTHER -> ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
            else -> {
                throw IllegalStateException("Unsupported type: $type")
            }
        }
    }

    private fun setProgress() {
        disposables.clear()
        Timber.e("-87, setProgress:%s", 23)
        val d = Observable.interval(1, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                setPlayerState()
                Timber.d("-83 , xxx : %s", 1)
            }

        disposables.add(d)
    }

    private fun setPlayerState() {
        tvPlayerCurrentTime?.text = stringForTime(exoPlayer.currentPosition)
        tvPlayerEndTime?.text = stringForTime(exoPlayer.duration)
    }

    private fun stringForTime(timeMs: Long): String {
        val totalSeconds = timeMs / 1000
        val seconds = totalSeconds % 60
        val minutes = totalSeconds / 60 % 60
        val hours = totalSeconds / 3600
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    override fun onRenderedFirstFrame(eventTime: AnalyticsListener.EventTime?, surface: Surface?) {
        setPlayerState()
        Timber.d("-129 , onRenderedFirstFrame : %s", exoPlayer.duration)
    }

    override fun onSeekStarted(eventTime: AnalyticsListener.EventTime?) {
        Timber.d("-134 , onSeekStarted : %s", eventTime)
    }

    override fun onSeekProcessed(eventTime: AnalyticsListener.EventTime?) {
        setPlayerState()
        Timber.d("-130 , onSeekProcessed : %s", eventTime)
    }

    override fun onPlayerError(
        eventTime: AnalyticsListener.EventTime?,
        error: ExoPlaybackException?
    ) {
        Timber.d(error, "-85 , onPlayerError : %s", error?.message)
    }

    override fun onPlayerStateChanged(
        eventTime: AnalyticsListener.EventTime?,
        playWhenReady: Boolean,
        playbackState: Int
    ) {
        when (playbackState) {
            Player.STATE_READY -> {
                if (playWhenReady) {
                    setProgress()
                    Timber.d("-87 , PLAY : %s", 1)
                } else {
                    disposables.clear()
                    Timber.d("-87 , PAUSE : %s", 2)
                }
            }
            Player.STATE_BUFFERING -> Timber.d("-107 , onPlayerStateChanged : %s", 4)
            else -> Timber.e("-94 , onPlayerStateChanged : %s", playbackState)
        }
    }


}