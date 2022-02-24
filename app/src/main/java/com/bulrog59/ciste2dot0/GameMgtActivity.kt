package com.bulrog59.ciste2dot0

import android.app.AlertDialog
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.game.management.GameListAdapter
import com.bulrog59.ciste2dot0.game.management.GameUtil
import com.bulrog59.ciste2dot0.game.management.GameUtil.Companion.mapper
import com.bulrog59.ciste2dot0.game.management.GamesDataManager
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.gamedata.SceneData
import com.bulrog59.ciste2dot0.gamedata.SceneType
import java.util.*
import kotlin.system.exitProcess


class GameMgtActivity : AppCompatActivity() {

    private val gameMetaUtil = GameUtil(this)

    private val gamesUnderTransfer: MutableList<UUID> = mutableListOf()
    private var gameListAdapter: GameListAdapter? = null

    fun addGameUnderTransfer(gameID: UUID) {
        gamesUnderTransfer.add(gameID)
    }

    fun removeGameUnderTransfer(gameID: UUID) {
        gamesUnderTransfer.remove(gameID)
    }

    fun isUnderTransfer(gameID: UUID): Boolean {
        return gamesUnderTransfer.contains(gameID)
    }

    fun reviewIfAbortPossibleTransfer(done: () -> Unit) {
        if (gamesUnderTransfer.size > 0) {
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
        gameListAdapter = GameListAdapter(this)
        recyclerView.adapter = gameListAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        findViewById<ImageButton>(R.id.close_game).setOnClickListener {
            reviewIfAbortPossibleTransfer {
                finish()
                exitProcess(0)
            }

        }
        findViewById<ImageButton>(R.id.game_help).setOnClickListener {
            AlertDialog.Builder(this)
            .setMessage(R.string.game_help)
                .setCancelable(false)
                .setPositiveButton("OK") { _, _ ->  }.create().show()

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
                    mapper.createObjectNode(),
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
                GameUtil.languages
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

    override fun onResume() {
        super.onResume()
        gameListAdapter?.getListOfGames()
    }

}