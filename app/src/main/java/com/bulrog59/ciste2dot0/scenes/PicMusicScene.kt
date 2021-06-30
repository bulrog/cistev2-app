package com.bulrog59.ciste2dot0.scenes

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.bulrog59.ciste2dot0.CisteActivity
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.Util

class PicMusicScene(
    private val imageName: String,
    private val musicName: String?,
    private val loopMusic: Boolean,
    private val cisteActivity: CisteActivity,
) : Scene {
    private var mediaPlayer: MediaPlayer? = null
    private var position = 0

    @SuppressLint("ClickableViewAccessibility")
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun setup() {
        val util = Util(cisteActivity.packageName)
        musicName?.apply {
            mediaPlayer =
                MediaPlayer.create(cisteActivity, util.getUri(this))

        }
        mediaPlayer?.apply { isLooping = loopMusic }
        cisteActivity.setContentView(R.layout.view_pic_music)
        cisteActivity.findViewById<ImageView>(R.id.imageView).apply {
            setImageURI(util.getUri(imageName))
            setOnTouchListener(TouchScreen(cisteActivity,mediaPlayer))
            }
        }


    class TouchScreen(private val cisteActivity: CisteActivity, private val mediaPlayer: MediaPlayer?):View.OnTouchListener {

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            mediaPlayer?.stop()
            cisteActivity.setScene(1)
            return true
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun startMusic() {
        mediaPlayer?.apply {
            seekTo(position)
            start()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun pauseMusic() {
        mediaPlayer?.apply {
            position = currentPosition
            pause()
        }
    }


}