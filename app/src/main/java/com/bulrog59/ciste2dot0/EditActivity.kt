package com.bulrog59.ciste2dot0

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.editor.FieldValidator
import com.bulrog59.ciste2dot0.editor.MenuSelectorAdapter
import com.bulrog59.ciste2dot0.game.management.GameDataLoader
import com.bulrog59.ciste2dot0.game.management.GameDataWriter
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.gamedata.SceneData
import com.bulrog59.ciste2dot0.gamedata.SceneType
import com.fasterxml.jackson.databind.ObjectMapper

class EditActivity : AppCompatActivity() {

    private lateinit var gameDataWriter: GameDataWriter
    private val fieldValidator = FieldValidator(this)

    private fun setEditorForScene(position: Int) {
        when (gameDataWriter.gameData.scenes[position].sceneType) {
            else -> Toast.makeText(this, getText(R.string.no_edit_mode), Toast.LENGTH_LONG).show()
        }
    }


    private fun sceneSelectionScreen() {
        val scenesDescription = gameDataWriter.gameData.scenes.map {
            "${it.sceneId}:${it.name ?: "none"} (${getText(it.sceneType.description)})"
        }

        setContentView(R.layout.editor_scene_selection)

        val recyclerView = findViewById<RecyclerView>(R.id.scene_selection_menu)
        recyclerView.adapter = MenuSelectorAdapter(scenesDescription) { p -> setEditorForScene(p) }
        recyclerView.layoutManager = LinearLayoutManager(this)
        findViewById<Button>(R.id.add_scene_button).setOnClickListener { sceneCreationScreen() }
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
            if (sceneTypeSelector.positionSelected!=RecyclerView.NO_POSITION){
                addNewSceneToGameData(SceneType.values()[sceneTypeSelector.positionSelected])
            }
            else {
                Toast.makeText(this,R.string.element_not_selected,Toast.LENGTH_LONG).show()
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
}