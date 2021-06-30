package com.bulrog59.ciste2dot0

import android.app.Activity
import android.widget.MediaController
import android.widget.VideoView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class VideoScene(private val videoName:String, private val activity: Activity): LifecycleObserver {
    private lateinit var videoView: VideoView
    private lateinit var mediaController: MediaController
    private var position=0

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun createVideo(){
        activity.setContentView(R.layout.activity_video2)
        videoView =  activity.findViewById(R.id.videoView)
        mediaController= MediaController(activity)
        mediaController.setAnchorView(videoView)
        videoView.setVideoURI (Util(activity.packageName).getUri("trial"));
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