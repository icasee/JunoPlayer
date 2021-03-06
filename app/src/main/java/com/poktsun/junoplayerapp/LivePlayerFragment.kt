package com.poktsun.junoplayerapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.poktsun.junoplayer.JunoPlayerView
import timber.log.Timber

class LivePlayerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_live_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val junoPlayerView = view.findViewById<JunoPlayerView>(R.id.player_view)
        junoPlayerView?.run {
//            listener = object : JunoPlayerListener{
//                override fun onError(error: Throwable?) {
//                    Timber.d("-28, onError:%s", error?.message)
//                }
//
//                override fun onPlay() {
//                    Timber.d("-32, onPlay:%s",2)
//                }
//
//                override fun onPause() {
//                    Timber.d("-36, onPause:%s",3)
//                }
//            }
//            setPlayerStateChangedListener { playerState, _ ->
//                Timber.d("-41, onViewCreated:%s", playerState)
//            }

            play(GlobalApp.VIDEO_URL)
        }

        Timber.d("-44, onViewCreated:%s", 1)
    }
}