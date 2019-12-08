package com.poktsun.junoplayerapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.poktsun.junoplayer.JunoLifecycle
import com.poktsun.junoplayer.JunoPlayerView
import timber.log.Timber

class VodPlayerFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //return super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_vod_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val junoPlayerView = view.findViewById<JunoPlayerView>(R.id.player_view)
        junoPlayerView.mode = 0

        view.findViewById<View>(R.id.button1)?.setOnClickListener {
            junoPlayerView.play(GlobalApp.VIDEO_URL)
            Timber.d("-30 , onCreate : %s", 1)

        }

        view.findViewById<View>(R.id.button2)?.setOnClickListener {
            junoPlayerView.playWithAds(GlobalApp.VIDEO_URL, GlobalApp.ADTAG_URL)
            Timber.d("-30 , onCreate : %s", 1)
        }

        lifecycle.addObserver(JunoLifecycle("VodPlayerFragment"))
        //junoPlayerView.registerLifecycle(lifecycle)

    }
}