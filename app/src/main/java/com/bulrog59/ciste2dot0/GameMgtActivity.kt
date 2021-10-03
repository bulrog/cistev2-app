package com.bulrog59.ciste2dot0

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.game.management.GameDataManager
import com.bulrog59.ciste2dot0.game.management.GameMetaData
import com.bulrog59.ciste2dot0.game.management.GameListAdapter
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.gamedata.SceneData
import com.bulrog59.ciste2dot0.gamedata.SceneType
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

class GameMgtActivity : AppCompatActivity() {
    val languages =
        HashSet(Locale.getAvailableLocales().map { it.displayLanguage }).sorted()


    private fun gameSelectionScreen() {
        setContentView(R.layout.game_management)
        val recyclerView = findViewById<RecyclerView>(R.id.games_list)
        recyclerView.adapter = GameListAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        findViewById<ImageButton>(R.id.close_game).setOnClickListener {
            finish()
            System.exit(0)
        }
        findViewById<ImageButton>(R.id.new_game).setOnClickListener {
            gameCreationScreen()
        }
    }

    private fun validateField(
        fieldId: Int,
        validator: (String) -> Boolean,
        errorMessage: String
    ): Boolean {
        val field = findViewById<EditText>(fieldId)
        val error = validator(field.text.toString())
        if (error) {
            field.error = errorMessage
        }
        return error
    }

    private fun errorInNewGameFields(): Boolean {
        var error = validateField(
            R.id.game_title_input,
            { v -> v.isEmpty() || v.length > 255 },
            "field cannot be empty or longer than 255 characters"
        )
        error = validateField(
            R.id.game_language_input,
            { v -> !languages.contains(v) },
            "please select a language from the list"
        ) || error
        return validateField(
            R.id.game_location_input,
            String::isEmpty,
            "field cannot be empty"
        ) || error
    }

    private fun createGameFromFields(){
        val name = findViewById<EditText>(R.id.game_title_input).text.toString()
        val language = findViewById<TextView>(R.id.game_language_input).text.toString()
        val description =
            findViewById<TextView>(R.id.game_description_input).text.toString()
        val location = findViewById<TextView>(R.id.game_location_input).text.toString()
        val id = UUID.randomUUID()
        val gameMetaData = GameMetaData(
            name = name,
            language = language,
            description = description,
            location = location,
            id = id,
            sizeInMB = null
        )
        val gameData = GameData(
            scenes = listOf(
                SceneData(
                    0,
                    SceneType.exit,
                    ObjectMapper().createObjectNode(),
                    "exit"
                )
            ),
            gameMetaData = gameMetaData,
            backButtonScene = 0,
            starting = 0
        )
        GameDataManager(this).createGame(gameData)
        gameSelectionScreen()
    }

    private fun gameCreationScreen() {
        setContentView(R.layout.editor_new_game)
        findViewById<AutoCompleteTextView>(R.id.game_language_input).setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                languages
            )
        )



        findViewById<Button>(R.id.create_game).setOnClickListener {
            if (!errorInNewGameFields()) {
                createGameFromFields()
            }

        }
    }

    override fun onBackPressed() {
        gameSelectionScreen()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this));
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        gameSelectionScreen()


    }
}