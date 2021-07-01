package com.bulrog59.ciste2dot0

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import com.bulrog59.ciste2dot0.scenes.PicMusicOption
import com.bulrog59.ciste2dot0.scenes.PicMusicScene
import com.bulrog59.ciste2dot0.scenes.VideoOption
import com.bulrog59.ciste2dot0.scenes.VideoScene

class CisteActivity : AppCompatActivity() {
    private var currentScene: LifecycleObserver? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (currentScene == null) {
            setScene(3)
        }


    }

    fun setScene(sceneId: Int) {
        currentScene?.apply { lifecycle.removeObserver(this) }
        when (sceneId) {
            1 -> {
                currentScene = VideoScene(VideoOption("trial", 2), this)
            }
            2 -> {
                finish()
                System.exit(0)
            }
            3 -> {
                currentScene = PicMusicScene(PicMusicOption("start_screen", "audio", true, 1), this)

            }
        }
        lifecycle.addObserver(currentScene!!)

    }
}