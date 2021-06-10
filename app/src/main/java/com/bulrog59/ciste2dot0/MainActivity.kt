package com.bulrog59.ciste2dot0

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bulrog59.ciste2dot0.databinding.ActivityFullscreenBinding


class MainActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private var position: Int=0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        mediaPlayer = MediaPlayer.create(this, R.raw.audio)
        mediaPlayer!!.isLooping = true // Set looping

        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        mediaPlayer!!.seekTo(position)
        mediaPlayer!!.start()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer!!.pause()
        position=mediaPlayer!!.currentPosition

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        startActivity(Intent(this, VideoActivity::class.java))
        return super.onTouchEvent(event)
    }

}