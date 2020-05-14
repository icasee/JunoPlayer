package com.poktsun.junoplayer

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.analytics.AnalyticsListener
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
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
    private var playListener: (() -> Unit)? = null
    private var pauseListener: (() -> Unit)? = null
    private var progressListener: (() -> Unit)? = null
    private var errorListener: ((Throwable?) -> Unit?)? = null
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

    //setup
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

    ///var listener: JunoPlayerListener? = null

    var volume: Float
        set(value) {
            exoPlayer.volume = value
        }
        get() = exoPlayer.volume

    val isPlaying: Boolean
        get() = exoPlayer.playWhenReady && exoPlayer.playbackState == Player.STATE_READY

    init {
        Timber.d("-66, :%s", context)
        exoPlayer.addAnalyticsListener(this)
    }

    fun getPlayer(): SimpleExoPlayer = exoPlayer

    fun play(url: String, extension: String? = null) {
        val mediaSource = buildMediaSource(Uri.parse(url), extension)
        prepare(mediaSource)
    }

    fun play() {
        exoPlayer.playWhenReady = true
        exoPlayer.playbackState
    }

    fun pause() {
        exoPlayer.playWhenReady = false
        exoPlayer.playbackState
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
        playWhenReady: Boolean = true
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

    /**
     * @see <a href="https://exoplayer.dev/progressive.html">progressive</a>
     */
    private fun buildMediaSource(
        uri: Uri,
        extension: String? = null
    ): MediaSource {
        return when (val type = Util.inferContentType(uri, extension)) {
            C.TYPE_DASH -> DashMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
            C.TYPE_SS -> SsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
            C.TYPE_HLS -> HlsMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
            C.TYPE_OTHER -> ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri)
            else -> {
                throw IllegalStateException("Unsupported type: $type")
            }
        }
    }

    override fun onPlayerError(
        eventTime: AnalyticsListener.EventTime?,
        error: ExoPlaybackException?
    ) {
        errorListener?.invoke(error)
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
                    playListener?.invoke()
                } else {
                    pauseListener?.invoke()
                }
            }
            Player.STATE_BUFFERING -> progressListener?.invoke()
            Player.STATE_ENDED -> endedListener?.invoke()
            else -> Timber.e("-151 , onPlayerStateChanged : %s", playbackState)
        }
    }


}