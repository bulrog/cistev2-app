package com.bulrog59.ciste2dot0

import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.game.management.GameListAdapter
import java.util.*

class GameMgtActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this));
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setContentView(R.layout.game_management)
        val recyclerView = findViewById<RecyclerView>(R.id.games_list)
        recyclerView.adapter = GameListAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        findViewById<ImageButton>(R.id.close_game).setOnClickListener {
            finish()
            System.exit(0)
        }
        findViewById<ImageButton>(R.id.new_game).setOnClickListener {
            setContentView(R.layout.editor_new_game)
            val languages =
                HashSet(Locale.getAvailableLocales().map { it.displayLanguage }).sorted()
            findViewById<AutoCompleteTextView>(R.id.game_language_input).setAdapter(
                ArrayAdapter(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    languages
                )
            )


        }

    }
}