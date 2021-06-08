package com.bulrog59.ciste2dot0

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this@MainActivity, BackgroundSoundService::class.java)
        startService(intent)
        setContentView(R.layout.activity_main)
    }
}