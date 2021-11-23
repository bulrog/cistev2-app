package com.bulrog59.ciste2dot0

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.editor.*
import com.bulrog59.ciste2dot0.editor.utils.CallBackActivityResult
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper.Companion.sceneDescriptions
import com.bulrog59.ciste2dot0.editor.utils.FieldValidator
import com.bulrog59.ciste2dot0.editor.utils.ItemPicker
import com.bulrog59.ciste2dot0.editor.utils.MenuSelectorAdapter
import com.bulrog59.ciste2dot0.game.management.GameDataWriter
import com.bulrog59.ciste2dot0.gamedata.SceneData
import com.bulrog59.ciste2dot0.gamedata.SceneType
import com.fasterxml.jackson.databind.JsonNode
import java.util.*


class EditActivity : AppCompatActivity() {

    private lateinit var gameDataWriter: GameDataWriter
    private val fieldValidator = FieldValidator(this)
    private var filePicker: CallBackActivityResult? = null

    //TODO: as also in GameMgtActivity to put it in a companion object
    private val languages =
        HashSet(Locale.getAvailableLocales().map { it.displayLanguage }).sorted()


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
            SceneType.updateInventory -> {
                UpdateInventoryEditor(this, gameDataWriter.gameData, position) {
                    updateSceneOption(sceneData, it)
                }.apply { init() }
            }
            SceneType.inventory -> {
                InventoryEditor(this, gameDataWriter.gameData, position) {
                    updateSceneOption(sceneData, it)
                }.apply { init() }
            }
            SceneType.detector -> {
                filePicker = DetectorEditor(this, gameDataWriter.gameData, position) {
                    updateSceneOption(sceneData, it)
                }.apply { init() }
            }
            //TODO: add editor for the rule engine
            else -> Toast.makeText(this, getText(R.string.no_edit_mode), Toast.LENGTH_LONG).show()
        }
    }

    private fun selectStartingSceneScreen() {
        val itemPicker = ItemPicker(this)
        itemPicker.previousSelection =
            gameDataWriter.gameData.scenes.indexOf(gameDataWriter.gameData.scenes.findLast { gameDataWriter.gameData.starting == it.sceneId })
        itemPicker.init(
            R.string.select_start_scene_title,
            sceneDescriptions(gameDataWriter.gameData.scenes, this)
        ) { p ->
            gameDataWriter.apply { this.updateStartingScene(this.gameData.scenes[p].sceneId) }
            sceneSelectionScreen()
        }
    }

    private fun deleteScene() {
        ItemPicker(this).init(
            R.string.select_element_to_delete,
            sceneDescriptions(gameDataWriter.gameData.scenes, this)
        ) { p ->
            AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(R.string.delete_item_message)
                .setPositiveButton(R.string.confirmation) { _, _ ->
                    gameDataWriter.apply { this.deleteScene(this.gameData.scenes[p].sceneId) }
                    sceneSelectionScreen()
                }
                .setNegativeButton(R.string.denial) { _, _ -> sceneSelectionScreen() }
                .show()
        }

    }


    private fun sceneSelectionScreen() {
        //TODO: also add an icon to copy the game
        val scenesDescription = sceneDescriptions(gameDataWriter.gameData.scenes, this)
        setContentView(R.layout.editor_scene_selection)

        val recyclerView = findViewById<RecyclerView>(R.id.scene_selection_menu)
        recyclerView.adapter = MenuSelectorAdapter(scenesDescription) { p -> setEditorForScene(p) }
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.add_scene_button).setOnClickListener { sceneCreationScreen() }
        findViewById<Button>(R.id.edit_start_scene).setOnClickListener { selectStartingSceneScreen() }
        findViewById<Button>(R.id.delete_scene_button).setOnClickListener { deleteScene() }
        findViewById<Button>(R.id.meta_data_edit_button).setOnClickListener { gameMetaEdition() }


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

    private fun gameMetaEdition() {
        //TODO: to finish implementation for game meta edition
        setContentView(R.layout.editor_game_meta)
        findViewById<AutoCompleteTextView>(R.id.game_language_input).setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                languages
            )
        )
        val gameMeta = gameDataWriter.gameData.gameMetaData
        gameMeta?.description?.apply { findViewById<EditText>(R.id.description_text).setText(this) }
        gameMeta?.language?.apply { findViewById<AutoCompleteTextView>(R.id.game_language_input).setText(this) }
        gameMeta?.name.apply { findViewById<EditText>(R.id.menu_title_input).setText(this) }
        gameMeta?.location.apply { findViewById<EditText>(R.id.game_location_input).setText(this) }


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