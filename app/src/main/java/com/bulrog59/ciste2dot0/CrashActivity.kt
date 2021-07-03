package com.bulrog59.ciste2dot0

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class CrashActivity : AppCompatActivity() {

    companion object{
        val ERROR="error"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_crash)

        findViewById<Button>(R.id.crash_button).setOnClickListener {
            finish()
            System.exit(0)
        }
        findViewById<TextView>(R.id.errorDetails).apply {
            setText(intent.getStringExtra(ERROR))
            setMovementMethod(ScrollingMovementMethod())
        }

    }
}