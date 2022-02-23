package com.bulrog59.ciste2dot0

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import kotlin.system.exitProcess

class DisclaimerActivity : AppCompatActivity() {

    private lateinit var pref: SharedPreferences

    companion object {
        val CISTE_PREF = "ciste_pref"
        val APPROVED_DISCLAIMER = "approved_disclaimer"
    }

    private fun approveDisclaimer() {
        pref.edit().putBoolean(APPROVED_DISCLAIMER, true).apply()
        launchedLoginActivity()
    }

    private fun launchedLoginActivity() {
        startActivity(Intent(this, SignInActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pref = getSharedPreferences(CISTE_PREF, 0)
        if (!pref.getBoolean(APPROVED_DISCLAIMER, false)) {
            setContentView(R.layout.disclaimer)

            val resourceId = if (Locale.getDefault().equals(Locale.FRENCH)) {
                R.raw.disclaimer_fr
            } else {
                R.raw.disclaimer_en
            }

            findViewById<TextView>(R.id.disclaimer_text).text =
                resources.openRawResource(resourceId).bufferedReader().use { it.readText() }

            findViewById<Button>(R.id.accept_button).setOnClickListener {
                approveDisclaimer()
            }

            findViewById<Button>(R.id.decline_button).setOnClickListener {
                finish()
                exitProcess(0)
            }
        } else {
            launchedLoginActivity()

        }

    }

}