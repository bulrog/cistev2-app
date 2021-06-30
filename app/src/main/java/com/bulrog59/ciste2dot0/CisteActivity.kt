package com.bulrog59.ciste2dot0

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import com.bulrog59.ciste2dot0.scenes.PicMusicScene
import com.bulrog59.ciste2dot0.scenes.VideoScene

class CisteActivity:AppCompatActivity() {
    private var currentScene: LifecycleObserver?=null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setScene(3)


    }

    fun setScene(sceneId: Int){
        currentScene?.apply { lifecycle.removeObserver(this) }
        when (sceneId) {
            1-> lifecycle.addObserver(VideoScene("trial",this))
            2-> {
                finish()
                System.exit(0)
            }
            3-> {
                lifecycle.addObserver(PicMusicScene("start_screen","audio",true,this))
            }
        }

    }
}