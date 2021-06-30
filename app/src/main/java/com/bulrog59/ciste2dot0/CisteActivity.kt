package com.bulrog59.ciste2dot0

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver

class CisteActivity:AppCompatActivity() {
    private var currentScene: LifecycleObserver?=null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setScene(1)


    }

    fun setScene(sceneId: Int){
        currentScene?.apply { lifecycle.removeObserver(this) }
        when (sceneId) {
            1-> lifecycle.addObserver(VideoScene("trial",this))
            2-> {
                finish()
                System.exit(0)
            }
        }

    }
}