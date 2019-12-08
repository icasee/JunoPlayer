package com.poktsun.junoplayer

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ads.AdsLoader
import com.google.android.exoplayer2.source.ads.AdsMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import timber.log.Timber

class JunoPlayer(context: Context) : AnalyticsListener {
    private val dataSourceFactory by lazy {
        val userAgent = Util.getUserAgent(context, "nowNews")
        DefaultDataSourceFactory(context, userAgent)
    }
    private val exoPlayer by lazy {
        // val userAgent = Util.getUserAgent(context, "nowNews")
        val videoTrackSelectionFactory: TrackSelection.Factory = AdaptiveTrackSelection.Factory()
        val rendererFactory = DefaultRenderersFactory(context)
        val trackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        val loadControl = DefaultLoadControl.Builder().createDefaultLoadControl()

        //dataSourceFactory = DefaultDataSourceFactory(context, userAgent)
        ExoPlayerFactory.newSimpleInstance(
            context,
            rendererFactory,
            trackSelector,
            loadControl
        )
    }

    var volume: Float
        set(value) {
            exoPlayer.volume = value
        }
        get() = exoPlayer.volume

    val position
        get() = exoPlayer.currentPosition

    val duration
        get() = exoPlayer.duration

    init {
        Timber.d("-66, :%s", context)
        exoPlayer.addAnalyticsListener(this)
    }

    fun getPlayer(): SimpleExoPlayer = exoPlayer

    fun play(
        url: String
    ) {
        val mediaSource = buildMediaSource(Uri.parse(url))
        prepare(mediaSource)
    }

    fun pause() {
        exoPlayer.playWhenReady = false
    }

    fun stop() {
        exoPlayer.playWhenReady = false
        exoPlayer.stop(true)
    }

    fun release() {
        exoPlayer.release()
    }

    fun prepare(
        mediaSource: MediaSource,
        playWhenReady:
        Boolean = true
    ) {
        exoPlayer.playWhenReady = playWhenReady
        exoPlayer.prepare(mediaSource)
    }

    fun buildAdsMediaSource(
        videoUrl: String,
        imaAdsLoader: ImaAdsLoader,
        adViewProvider: AdsLoader.AdViewProvider
    ): AdsMediaSource {
        return AdsMediaSource(
            buildMediaSource(Uri.parse(videoUrl)),
            dataSourceFactory,
            imaAdsLoader,
            adViewProvider
        )
    }

    private fun buildMediaSource(
        uri: Uri
    ): MediaSource {
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
                    //setProgress()
                    Timber.d("-87 , PLAY : %s", 1)
                } else {
                    //disposables.clear()
                    Timber.d("-87 , PAUSE : %s", 2)
                }
            }
            Player.STATE_BUFFERING -> Timber.d("-107 , onPlayerStateChanged : %s", 4)
            else -> Timber.e("-94 , onPlayerStateChanged : %s", playbackState)
        }
    }


}