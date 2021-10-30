package com.bulrog59.ciste2dot0

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.editor.utils.FieldValidator
import com.bulrog59.ciste2dot0.game.management.GamesDataManager
import com.bulrog59.ciste2dot0.game.management.GameMetaData
import com.bulrog59.ciste2dot0.game.management.GameListAdapter
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.gamedata.SceneData
import com.bulrog59.ciste2dot0.gamedata.SceneType
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

class GameMgtActivity : AppCompatActivity() {
    private val fieldValidator = FieldValidator(this)
    private val languages =
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


    private fun errorInNewGameFields(): Boolean {
        var error=fieldValidator.notEmptyField(R.id.menu_title_input)
        error=fieldValidator.maxSizeField(R.id.menu_title_input)|| error
        error=fieldValidator.notEmptyField(R.id.game_location_input)|| error
        error=fieldValidator.inList(R.id.game_language_input, languages)|| error
        return error
    }

    private fun createGameFromFields() {
        val name = findViewById<EditText>(R.id.menu_title_input).text.toString()
        val language = findViewById<TextView>(R.id.game_language_input).text.toString()
        val description =
            findViewById<TextView>(R.id.pic_optional_text).text.toString()
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
        GamesDataManager(this).createGame(gameData)
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