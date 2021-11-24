package com.bulrog59.ciste2dot0

import android.app.AlertDialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.game.management.GamesDataManager
import com.bulrog59.ciste2dot0.game.management.GameListAdapter
import com.bulrog59.ciste2dot0.game.management.GameMetaUtil
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.gamedata.SceneData
import com.bulrog59.ciste2dot0.gamedata.SceneType
import com.fasterxml.jackson.databind.ObjectMapper
import kotlin.system.exitProcess

class GameMgtActivity : AppCompatActivity() {

    private val gameMetaUtil=GameMetaUtil(this)
    private var gameUnderTransfer = 0

    fun increaseGameUnderTransfer() {
        gameUnderTransfer++
    }

    fun decreaseGameUnderTransfer() {
        gameUnderTransfer--
    }

    fun reviewIfAbortPossibleTransfer(done: () -> Unit) {
        if (gameUnderTransfer > 0) {
            AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(R.string.transfer_abort)
                .setPositiveButton(R.string.confirmation) { _, _ ->
                    done()
                }
                .setNegativeButton(R.string.denial) { _, _ -> }
                .show()
        } else {
            done()
        }
    }

    private fun gameSelectionScreen() {
        setContentView(R.layout.game_management)
        val recyclerView = findViewById<RecyclerView>(R.id.games_list)
        recyclerView.adapter =
            GameListAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        findViewById<ImageButton>(R.id.close_game).setOnClickListener {
            reviewIfAbortPossibleTransfer {
                finish()
                exitProcess(0)
            }

        }
        findViewById<ImageButton>(R.id.new_game).setOnClickListener {
            gameCreationScreen()
        }

    }

    private fun createGameFromFields() {

        val gameData = GameData(
            scenes = listOf(
                SceneData(
                    0,
                    SceneType.exit,
                    ObjectMapper().createObjectNode(),
                    "exit"
                )
            ),
            gameMetaData = gameMetaUtil.createGameMetaDataForMetaDataEditScreen(null),
            backButtonScene = 0,
            starting = 0
        )
        GamesDataManager(this).createGame(gameData)
        gameSelectionScreen()
    }


    private fun gameCreationScreen() {
        setContentView(R.layout.editor_game_meta)
        findViewById<AutoCompleteTextView>(R.id.game_language_input).setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                GameMetaUtil.languages
            )
        )

        findViewById<Button>(R.id.game_meta_button).setOnClickListener {
            if (!gameMetaUtil.errorInGameMetaFields()) {
                createGameFromFields()
            }

        }
    }

    override fun onBackPressed() {
        gameSelectionScreen()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this))
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        gameSelectionScreen()


    }
}