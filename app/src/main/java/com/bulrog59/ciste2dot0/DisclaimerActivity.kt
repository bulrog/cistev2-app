package com.bulrog59.ciste2dot0

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.system.exitProcess

class DisclaimerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.disclaimer)

        val resourceId = if (Locale.getDefault().equals(Locale.FRENCH)) {
            R.raw.disclaimer_fr
        } else {
            R.raw.disclaimer_en
        }

        findViewById<TextView>(R.id.disclaimer_text).text =
            resources.openRawResource(resourceId).bufferedReader().use { it.readText() }

        findViewById<Button>(R.id.accept_button).setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        findViewById<Button>(R.id.decline_button).setOnClickListener {
            finish()
            exitProcess(0)
        }
    }
}

