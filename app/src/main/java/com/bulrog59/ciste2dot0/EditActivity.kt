package com.bulrog59.ciste2dot0

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.editor.*
import com.bulrog59.ciste2dot0.editor.GameOptionHelper.Companion.sceneList
import com.bulrog59.ciste2dot0.game.management.GameDataWriter
import com.bulrog59.ciste2dot0.gamedata.SceneData
import com.bulrog59.ciste2dot0.gamedata.SceneType
import com.fasterxml.jackson.databind.JsonNode


class EditActivity : AppCompatActivity() {

    private lateinit var gameDataWriter: GameDataWriter
    private val fieldValidator = FieldValidator(this)
    private var filePicker: CallBackActivityResult? = null


    private fun updateSceneOption(sceneData: SceneData, option: JsonNode) {
        gameDataWriter.addOrUpdateSceneData(
            SceneData(
                sceneData.sceneId,
                sceneData.sceneType,
                option,
                sceneData.name
            )
        )
        sceneSelectionScreen()
    }

    private fun setEditorForScene(position: Int) {
        val sceneData = gameDataWriter.gameData.scenes[position]
        when (sceneData.sceneType) {
            SceneType.picMusic -> {
                filePicker = PicMusicEditor(this, gameDataWriter.gameData, position) {
                    updateSceneOption(sceneData, it)
                }.apply { createScene() }
            }
            SceneType.video -> {
                filePicker = VideoEditor(this, gameDataWriter.gameData, position) {
                    updateSceneOption(sceneData, it)
                }.apply { createScene() }
            }
            SceneType.menu -> {
                MenuEditor(this, gameDataWriter.gameData, position) {
                    updateSceneOption(sceneData, it)
                }.apply { init() }
            }
            else -> Toast.makeText(this, getText(R.string.no_edit_mode), Toast.LENGTH_LONG).show()
        }
    }

    private fun selectStartingSceneScreen() {
        ItemPicker(this).init(R.string.select_start_scene_title, sceneList(gameDataWriter.gameData,this)) { p ->
            gameDataWriter.apply { this.updateStartingScene(this.gameData.scenes[p].sceneId) }
            sceneSelectionScreen()
        }
    }


    private fun sceneSelectionScreen() {
        val scenesDescription = sceneList(gameDataWriter.gameData,this)
        //TODO: to add also a button next to the scene selection to edit the game metadata
        setContentView(R.layout.editor_scene_selection)

        val recyclerView = findViewById<RecyclerView>(R.id.scene_selection_menu)
        recyclerView.adapter = MenuSelectorAdapter(scenesDescription) { p -> setEditorForScene(p) }
        recyclerView.layoutManager = LinearLayoutManager(this)
        findViewById<Button>(R.id.add_scene_button).setOnClickListener { sceneCreationScreen() }
        findViewById<Button>(R.id.edit_start_scene).setOnClickListener { selectStartingSceneScreen() }
    }

    private fun addNewSceneToGameData(sceneType: SceneType) {
        var error = fieldValidator.notEmptyField(R.id.scene_title_input)
        error = fieldValidator.maxSizeField(R.id.scene_title_input) || error
        if (error) {
            return
        }
        gameDataWriter.addNewSceneToGameData(
            sceneType,
            findViewById<TextView>(R.id.scene_title_input).text.toString()
        )
        sceneSelectionScreen()
    }

    private fun sceneCreationScreen() {
        setContentView(R.layout.editor_new_scene)
        val recyclerView = findViewById<RecyclerView>(R.id.scene_type_selection)
        val sceneTypeSelector = MenuSelectorAdapter(
            SceneType.values().map { v -> getText(v.description).toString() }) {}

        findViewById<Button>(R.id.create_scene_button).setOnClickListener {
            if (sceneTypeSelector.positionSelected != RecyclerView.NO_POSITION) {
                addNewSceneToGameData(SceneType.values()[sceneTypeSelector.positionSelected])
            } else {
                Toast.makeText(this, R.string.element_not_selected, Toast.LENGTH_LONG).show()
            }

        }
        recyclerView.adapter = sceneTypeSelector
        recyclerView.layoutManager = LinearLayoutManager(this)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this))
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        gameDataWriter = GameDataWriter(this)
        sceneSelectionScreen()

    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(R.string.quit_editor_mode)
            .setPositiveButton(R.string.confirmation) { _, _ ->
                super.onBackPressed()

            }
            .setNegativeButton(R.string.denial, null)
            .show()
    }


    //TODO: deprecated so need to review how to manage
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.apply { filePicker?.callBack(data.data, requestCode) }
    }

}