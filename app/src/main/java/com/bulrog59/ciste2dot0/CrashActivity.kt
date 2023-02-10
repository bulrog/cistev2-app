package com.bulrog59.ciste2dot0

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.system.exitProcess


class CrashActivity : AppCompatActivity() {

    companion object{
        const val ERROR="error"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_crash)

        findViewById<Button>(R.id.crash_button).setOnClickListener {
            finish()
            exitProcess(0)
        }
        findViewById<TextView>(R.id.debugData).apply {
            text = intent.getStringExtra(ERROR)
            movementMethod = ScrollingMovementMethod()
        }

    }
}