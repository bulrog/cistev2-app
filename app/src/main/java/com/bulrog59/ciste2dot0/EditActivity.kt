package com.bulrog59.ciste2dot0

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.editor.MenuSelectorAdapter
import com.bulrog59.ciste2dot0.game.management.GameDataLoader

class EditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this))
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val scenesDescription = GameDataLoader(this).loadGameDataFromIntent().scenes.map {
            "${it.sceneId}:${it.name ?: "none"}"
        }

        setContentView(R.layout.editor_scene_selection)
        val recyclerView = findViewById<RecyclerView>(R.id.scene_selection_menu)
        recyclerView.adapter = MenuSelectorAdapter(scenesDescription)
        recyclerView.layoutManager = LinearLayoutManager(this)

    }
}