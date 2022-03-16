package com.bulrog59.ciste2dot0

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SplashActivity : AppCompatActivity() {

    lateinit var sound:MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        setContentView(R.layout.splash_screen)
        sound=MediaPlayer.create(this, R.raw.logo)
        sound.setOnCompletionListener {
            startActivity(Intent(this, DisclaimerActivity::class.java))
        }
        sound.start()
    }

    override fun onResume() {
        super.onResume()
        sound.start()
    }

    override fun onPause() {
        super.onPause()
        sound.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        sound.stop()
    }
}