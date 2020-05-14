package com.poktsun.junoplayerapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.poktsun.junoplayer.JunoPlayer
import timber.log.Timber

class AudioPlayerFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //return super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_audio_player, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val junoPlayer = JunoPlayer(requireContext()).apply {
            setOnPlay {
                Timber.d("-33, setOnPlay:%s", 1)
            }
            setOnPause {
                Timber.d("-33, setOnPause:%s", 2)
            }
            setOnProgress {
                Timber.d("-33, setOnProgress:%s", 2)
            }
            setOnEnded {
                Timber.d("-33, setOnEnded:%s", 2)
            }
            setOnError {
                Timber.d("-33, setOnError:%s", 3)
            }
//            setOnAdLoaded {
//                Timber.d("-48, setOnAdLoaded:%s", 3)
//            }
//            setOnAdComplete {
//                Timber.d("-51, setOnAdComplete:%s", 5)
//            }
        }
//        junoPlayer.listener = object : JunoPlayerListener{
//
//            override fun onPlay() {
//                Timber.d("-29, onPlay:%s",1)
//            }
//
//            override fun onPause() {
//                Timber.d("-34, onPause:%s",2)
//            }
//        }

        view.findViewById<View>(R.id.button1)?.setOnClickListener {
            junoPlayer.play(GlobalApp.VIDEO_URL)
            Timber.d("-30 , onCreate : %s", 1)

        }
        view.findViewById<View>(R.id.button2)?.setOnClickListener {
            junoPlayer.pause()
            Timber.d("-30 , onCreate : %s", 1)
        }
    }
}