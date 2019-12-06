package com.poktsun.junoplayerapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.poktsun.junoplayer.JunoPlayer
import com.poktsun.junoplayer.JunoPlayerView
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    val VIDEO_URL = "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //val junoPlayerView = JunoPlayer(baseContext)
        val junoPlayerView = findViewById<JunoPlayerView>(R.id.player_view)

        findViewById<View>(R.id.button1)?.setOnClickListener {
            junoPlayerView.prepare(VIDEO_URL)
            Timber.d("-30 , onCreate : %s", 1)

        }

        findViewById<View>(R.id.button2)?.setOnClickListener {
            junoPlayerView.prepare(VIDEO_URL)
            Timber.d("-30 , onCreate : %s", 1)

        }

    }
}
