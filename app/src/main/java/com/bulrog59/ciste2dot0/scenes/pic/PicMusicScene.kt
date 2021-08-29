package com.bulrog59.ciste2dot0.scenes.pic

import android.content.pm.ActivityInfo
import android.media.MediaPlayer
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.bulrog59.ciste2dot0.CisteActivity
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.scenes.Scene

class PicMusicScene(
    private val picMusicOption: PicMusicOption,
    private val cisteActivity: CisteActivity,
) : Scene {
    private var mediaPlayer: MediaPlayer? = null
    private var position = 0

    override fun shutdown() {
        mediaPlayer?.stop()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun setup() {
        picMusicOption.musicName.apply {
            mediaPlayer =
                MediaPlayer.create(cisteActivity, cisteActivity.fileFinder.getUri(this))

        }
        mediaPlayer?.apply { isLooping = picMusicOption.loopMusic }
        cisteActivity.setContentView(R.layout.view_pic_music)
        cisteActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        cisteActivity.findViewById<ImageView>(R.id.imageView).apply {
            setImageURI(cisteActivity.fileFinder.getUri(picMusicOption.imageName))
            setOnTouchListener(TouchScreen(picMusicOption.nextScene, cisteActivity, mediaPlayer))
        }
    }


    class TouchScreen(
        private val nextScene: Int,
        private val cisteActivity: CisteActivity,
        private val mediaPlayer: MediaPlayer?
    ) : View.OnTouchListener {

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            if (event?.action == MotionEvent.ACTION_DOWN) {
                mediaPlayer?.stop()
                cisteActivity.setScene(nextScene)

            }
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