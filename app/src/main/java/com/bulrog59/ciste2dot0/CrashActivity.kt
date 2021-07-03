package com.bulrog59.ciste2dot0

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CrashActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_crash)

        findViewById<TextView>(R.id.errorDetails).setText(intent.getStringExtra("error"))
    }
}