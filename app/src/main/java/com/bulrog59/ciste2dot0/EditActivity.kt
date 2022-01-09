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
import com.bulrog59.ciste2dot0.game.management.GameUtil
import com.bulrog59.ciste2dot0.gamedata.SceneData
import com.bulrog59.ciste2dot0.gamedata.SceneType
import com.fasterxml.jackson.databind.JsonNode


class EditActivity : AppCompatActivity() {

    private lateinit var gameDataWriter: GameDataWriter
    private val fieldValidator = FieldValidator(this)
    private var filePicker: CallBackActivityResult? = null
    private val gameMetaUtil = GameUtil(this)

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
                filePicker = UpdateInventoryEditor(this, gameDataWriter.gameData, position) {
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
            SceneType.ruleEngine -> {
                RuleEngineEditor(this, gameDataWriter.gameData, position) {
                    updateSceneOption(sceneData, it)
                }.apply { init() }
            }
            else -> Toast.makeText(this, getText(R.string.no_edit_mode), Toast.LENGTH_LONG).show()
        }
    }

    private fun selectSceneScreen(previousSceneID:Int,gameSceneToUpdate: (Int) -> Unit) {
        val itemPicker = ItemPicker(this)
        itemPicker.previousSelection =
            gameDataWriter.gameData.scenes.indexOf(gameDataWriter.gameData.scenes.findLast { previousSceneID == it.sceneId })
        itemPicker.init(
            R.string.select_scene_title,
            sceneDescriptions(gameDataWriter.gameData.scenes, this)
        ) { p ->
            gameSceneToUpdate(gameDataWriter.gameData.scenes[p].sceneId)
            sceneSelectionScreen()
        }
    }

    private fun deleteScene() {
        ItemPicker(this).init(
            R.string.select_element_to_delete,
            sceneDescriptions(gameDataWriter.gameData.scenes, this)
        ) { p ->
            val sceneIDToDelete = gameDataWriter.gameData.scenes[p].sceneId
            val errorItemUse = gameDataWriter.verifyCanDeleteAScene(sceneIDToDelete)
            if (errorItemUse.isEmpty()) {
                AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(R.string.delete_item_message)
                    .setPositiveButton(R.string.confirmation) { _, _ ->
                        gameDataWriter.deleteScene(sceneIDToDelete)
                        sceneSelectionScreen()
                    }
                    .setNegativeButton(R.string.denial) { _, _ -> sceneSelectionScreen() }
                    .show()
            } else {
                Toast.makeText(this, errorItemUse, Toast.LENGTH_LONG).show()
                sceneSelectionScreen()
            }

        }

    }


    private fun sceneSelectionScreen() {
        val scenesDescription = sceneDescriptions(gameDataWriter.gameData.scenes, this)
        setContentView(R.layout.editor_scene_selection)

        val recyclerView = findViewById<RecyclerView>(R.id.scene_selection_menu)
        recyclerView.adapter = MenuSelectorAdapter(
            scenesDescription,
            RecyclerView.NO_POSITION
        ) { p -> setEditorForScene(p) }
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.add_scene_button).setOnClickListener { sceneCreationScreen() }
        findViewById<Button>(R.id.edit_start_scene).setOnClickListener {
            selectSceneScreen(gameDataWriter.gameData.starting) { s ->
                gameDataWriter.apply {
                    this.updateStartingScene(
                        s
                    )
                }
            }
        }
        findViewById<Button>(R.id.back_button_scene).setOnClickListener {
            selectSceneScreen(gameDataWriter.gameData.backButtonScene) { s ->
                gameDataWriter.apply {
                    this.updateBackButtonScene(
                        s
                    )
                }
            }
        }
        findViewById<Button>(R.id.delete_scene_button).setOnClickListener { deleteScene() }
        findViewById<Button>(R.id.meta_data_edit_button).setOnClickListener { gameMetaEdition() }
        findViewById<Button>(R.id.clear_resource_button).setOnClickListener { clearUnusedResource() }


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
        var positionSelected = RecyclerView.NO_POSITION
        setContentView(R.layout.editor_new_scene)
        val recyclerView = findViewById<RecyclerView>(R.id.scene_type_selection)
        val sceneTypeSelector = MenuSelectorAdapter(
            SceneType.values().map { v -> getText(v.description).toString() },
            RecyclerView.NO_POSITION
        ) { positionSelected = it }

        findViewById<Button>(R.id.create_scene_button).setOnClickListener {
            if (positionSelected != RecyclerView.NO_POSITION) {
                addNewSceneToGameData(SceneType.values()[positionSelected])
            } else {
                Toast.makeText(this, R.string.element_not_selected, Toast.LENGTH_LONG).show()
            }

        }

        recyclerView.adapter = sceneTypeSelector
        recyclerView.layoutManager = LinearLayoutManager(this)

    }

    private fun gameMetaEdition() {

        setContentView(R.layout.editor_game_meta)
        findViewById<AutoCompleteTextView>(R.id.game_language_input).setAdapter(
            ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                GameUtil.languages
            )
        )
        val gameMeta = gameDataWriter.gameData.gameMetaData
        gameMeta?.description?.apply { findViewById<EditText>(R.id.description_text).setText(this) }
        gameMeta?.language?.apply {
            findViewById<AutoCompleteTextView>(R.id.game_language_input).setText(
                this
            )
        }
        gameMeta?.name.apply { findViewById<EditText>(R.id.menu_title_input).setText(this) }
        gameMeta?.location.apply { findViewById<EditText>(R.id.game_location_input).setText(this) }

        findViewById<Button>(R.id.game_meta_button).setOnClickListener {
            if (!gameMetaUtil.errorInGameMetaFields()) {
                gameDataWriter.updateGameMetaData(
                    gameMetaUtil.createGameMetaDataForMetaDataEditScreen(
                        gameDataWriter.gameData.gameMetaData?.id
                    )
                )
            }
            sceneSelectionScreen()
        }

    }

    private fun clearUnusedResource() {
        AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setMessage(R.string.confirmation_delete_resource)
            .setPositiveButton(R.string.confirmation) { _, _ ->
                gameDataWriter.clearUnusedFiles()
                sceneSelectionScreen()
            }
            .setNegativeButton(R.string.denial) { _, _ -> }
            .show()
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
            .setMessage(R.string.quit)
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