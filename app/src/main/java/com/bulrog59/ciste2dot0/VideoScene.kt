package com.bulrog59.ciste2dot0

import android.widget.MediaController
import android.widget.VideoView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class VideoScene(private val videoName:String, private val cisteActivity: CisteActivity): LifecycleObserver {
    private lateinit var videoView: VideoView
    private lateinit var mediaController: MediaController
    private var position=0

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun createVideo(){
        cisteActivity.setContentView(R.layout.activity_video2)
        videoView =  cisteActivity.findViewById(R.id.videoView)
        mediaController= MediaController(cisteActivity)
        mediaController.setAnchorView(videoView)
        videoView.setVideoURI (Util(cisteActivity.packageName).getUri("trial"));
        videoView.setMediaController(mediaController)
        videoView.setOnPreparedListener { mediaPlayer ->
            videoView.seekTo(position)
            videoView.start()

            // When video Screen change size.
            mediaPlayer.setOnVideoSizeChangedListener { mp, width, height -> // Re-Set the videoView that acts as the anchor for the MediaController
                mediaController.setAnchorView(videoView)
            }

            mediaPlayer.setOnCompletionListener { cisteActivity.setScene(2) }
        }
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun pauseVideo() {
        position=videoView.currentPosition
        videoView.pause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun restartVideo(){
        videoView.seekTo(position)
    }
}