package com.bulrog59.ciste2dot0

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.editor.MenuSelectorAdapter
import com.bulrog59.ciste2dot0.game.management.GameDataLoader
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.gamedata.SceneType

class EditActivity : AppCompatActivity() {

    private lateinit var gameData:GameData

    private fun setEditorForScene(position:Int){
        when(gameData.scenes[position].sceneType){
            else -> Toast.makeText(this,getText(R.string.no_edit_mode),Toast.LENGTH_LONG).show()
        }
    }


    private fun sceneSelectionScreen(){
        val scenesDescription = gameData.scenes.map {
            "${it.sceneId}:${it.name ?: "none"} (${getText(it.sceneType.description)})"
        }

        setContentView(R.layout.editor_scene_selection)

        val recyclerView = findViewById<RecyclerView>(R.id.scene_selection_menu)
        recyclerView.adapter = MenuSelectorAdapter(scenesDescription) {
                p -> setEditorForScene(p) }
        recyclerView.layoutManager = LinearLayoutManager(this)
        findViewById<Button>(R.id.add_scene_button).setOnClickListener { sceneCreationScreen() }
    }

    private fun sceneCreationScreen(){
        setContentView(R.layout.editor_new_scene)
        val recyclerView = findViewById<RecyclerView>(R.id.scene_type_selection)
        //TODO: manage when select an item that it got highlighted: https://stackoverflow.com/questions/27194044/how-to-properly-highlight-selected-item-on-recyclerview
        recyclerView.adapter = MenuSelectorAdapter(SceneType.values().map { v-> getText(v.description).toString() }) {//TODO
                 }
        recyclerView.layoutManager = LinearLayoutManager(this)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this))
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        gameData= GameDataLoader(this).loadGameDataFromIntent()

        sceneSelectionScreen()

    }
}