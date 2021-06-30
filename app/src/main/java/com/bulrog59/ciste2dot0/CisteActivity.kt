package com.bulrog59.ciste2dot0

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

class CisteActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        lifecycle.addObserver(VideoScene("trial",this))

    }
}