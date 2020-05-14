package com.poktsun.junoplayerapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.poktsun.junoplayer.JunoPlayerView
import timber.log.Timber


class VodPlayerFragment : Fragment() {

    val title by lazy {
        arguments?.getString("TITLE") ?: "kelvin"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vod_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val junoPlayerView = view.findViewById<JunoPlayerView>(R.id.player_view).apply {
            // useController = false
            setOnPlay {
                Timber.d("-33, setOnPlay:%s", isPlayingAd)
            }
            setOnPause {
                Timber.d("-33, setOnPause:%s", isPlayingAd)
            }
            setOnProgress {
                Timber.d("-33, setOnProgress:%s", 3)
            }
            setOnEnded {
                Timber.d("-33, setOnEnded:%s", 4)
            }
            setOnError {
                Timber.d("-33, setOnError:%s", it)
            }
            setOnAdLoaded {
                Timber.d("-33, setOnAdLoaded:%s", 6)
            }
            setOnAdComplete {
                Timber.d("-33, setOnAdComplete:%s", 7)
            }
            setOnAdEvent {
                // Timber.d("-33, setOnAdEvent:%s", it)
            }
            shareButton?.setOnClickListener {
                Timber.d("-56, onViewCreated:%s", 1)
            }
        }
        junoPlayerView.title = title
        junoPlayerView.lifecycleOwner = this

        view.findViewById<Button>(R.id.button1)?.apply {
            text = title
            setOnClickListener {
                junoPlayerView.play(GlobalApp.VIDEO_URL)
                Timber.d("-30 , onCreate : %s", title)
            }
        }
        view.findViewById<View>(R.id.button2)?.setOnClickListener {
            junoPlayerView.playWithAds(GlobalApp.VIDEO_URL, GlobalApp.ADTAG_URL)
            Timber.d("-30 , onCreate : %s", 1)
        }
        view.findViewById<View>(R.id.button3)?.setOnClickListener {
            junoPlayerView.pause()
        }
        view.findViewById<View>(R.id.button4)?.setOnClickListener {
            junoPlayerView.play()
        }
        view.findViewById<View>(R.id.button5)?.setOnClickListener {
            if (junoPlayerView.isPlaying) {
                junoPlayerView.pause()
            } else {
                junoPlayerView.play()
            }
        }

        //lifecycle.addObserver(JunoLifecycle("VodPlayerFragment"))
        //junoPlayerView.registerLifecycle(lifecycle)

    }

    override fun onResume() {
        super.onResume()
        Timber.d("-83, onResume:%s", title)
    }

    override fun onPause() {
        super.onPause()
        Timber.d("-88, onPause:%s", title)
    }

    companion object {
        fun netInstance(title: String): VodPlayerFragment {
            return VodPlayerFragment().apply {
                arguments = Bundle().apply {
                    putString("TITLE", title)
                }
            }
        }
    }
}