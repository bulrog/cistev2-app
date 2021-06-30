package com.bulrog59.ciste2dot0

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity


class VideoActivity : AppCompatActivity() {
    private lateinit var videoView: VideoView
    private lateinit var mediaController: MediaController
    private var position=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video2)
        videoView =  findViewById(R.id.videoView)
        mediaController= MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setVideoURI (Util(this.packageName).getUri("trial"));
        videoView.setMediaController(mediaController)
        videoView.setOnPreparedListener { mediaPlayer ->
            videoView.seekTo(position)
            videoView.start()

            // When video Screen change size.
            mediaPlayer.setOnVideoSizeChangedListener { mp, width, height -> // Re-Set the videoView that acts as the anchor for the MediaController
                mediaController.setAnchorView(videoView)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        position=videoView.currentPosition
        videoView.pause()
    }

    override fun onResume() {
        super.onResume()
        videoView.seekTo(position)
    }

}