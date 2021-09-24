package com.bulrog59.ciste2dot0

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.bulrog59.ciste2dot0.game.management.GameDataLoader
import com.bulrog59.ciste2dot0.gamedata.GameData

class EditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this))
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val scenesDescription = GameDataLoader(this).loadGameDataFromIntent().scenes.map {
            "${it.sceneId}:${it.name ?: "none"}"
        }

        setContentView(R.layout.editor_scene_selection)
        ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            scenesDescription
        ).also { findViewById<Spinner>(R.id.scene_selection).adapter = it }


    }
}