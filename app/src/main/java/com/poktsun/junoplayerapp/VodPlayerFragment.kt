package com.poktsun.junoplayerapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.poktsun.junoplayer.JunoPlayerListener
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

        val junoPlayerView = view.findViewById<JunoPlayerView>(R.id.player_view)
        junoPlayerView.title = title
        junoPlayerView.lifecycleOwner = this
        junoPlayerView.run {
            volume = 0f
            listener = object : JunoPlayerListener {

                override fun onEnded() {
                    Timber.d("-34, onEnded:%s", 5)
                }

                override fun onBuffering() {
                    Timber.d("-38, onBuffering:%s", 6)
                }

                override fun onPlay() {
                    Timber.d("-34, onPlay:%s", 1)
                }

                override fun onPause() {
                    Timber.d("-38, onPause:%s", 2)
                }

                override fun onAdLoaded() {
                    Timber.d("-42, onAdLoaded:%s", 3)
                }

                override fun onAdCompleted() {
                    Timber.d("-36, onAdCompleted: %s", 4)
                }
            }
        }

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