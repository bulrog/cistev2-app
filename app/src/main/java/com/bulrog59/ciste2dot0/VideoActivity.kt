package com.bulrog59.ciste2dot0

import android.os.Bundle
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity


class VideoActivity : AppCompatActivity() {
    private var videoView: VideoView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video2)
        videoView =  findViewById(R.id.videoView);
        //to be continued
    }
}