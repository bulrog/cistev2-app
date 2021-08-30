package com.bulrog59.ciste2dot0.scenes.video

import android.content.pm.ActivityInfo
import android.widget.MediaController
import android.widget.VideoView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.bulrog59.ciste2dot0.CisteActivity
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.scenes.Scene

class VideoScene(private val videoOption: VideoOption, private val cisteActivity: CisteActivity):
    Scene {
    private lateinit var videoView: VideoView
    private lateinit var mediaController: MediaController
    private var position=0

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun createVideo(){
        cisteActivity.setContentView(R.layout.view_video)
        cisteActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        videoView =  cisteActivity.findViewById(R.id.videoView)
        mediaController= MediaController(cisteActivity)
        mediaController.setAnchorView(videoView)
        videoView.setVideoURI (cisteActivity.resourceFinder.getUri(videoOption.videoName));
        videoView.setMediaController(mediaController)
        videoView.setOnPreparedListener { mediaPlayer ->
            videoView.seekTo(position)
            videoView.start()

            // When video Screen change size.
            mediaPlayer.setOnVideoSizeChangedListener { _, _, _ -> // Re-Set the videoView that acts as the anchor for the MediaController
                mediaController.setAnchorView(videoView)
            }

            mediaPlayer.setOnCompletionListener {
                shutdown()
                cisteActivity.setScene(videoOption.nextScene) }
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

    override fun shutdown() {
        videoView.stopPlayback()
    }
}