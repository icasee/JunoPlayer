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

        val junoPlayer = JunoPlayer(context!!)

        view.findViewById<View>(R.id.button1)?.setOnClickListener {
            junoPlayer.play(GlobalApp.VIDEO_URL)
            Timber.d("-30 , onCreate : %s", 1)

        }
        view.findViewById<View>(R.id.button2)?.setOnClickListener {
            junoPlayer.stop()
            Timber.d("-30 , onCreate : %s", 1)
        }
    }
}