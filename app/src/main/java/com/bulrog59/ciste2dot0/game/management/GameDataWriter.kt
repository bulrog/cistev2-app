package com.bulrog59.ciste2dot0.game.management

import android.app.Activity
import com.bulrog59.ciste2dot0.ResourceFinder
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.gamedata.SceneData
import com.bulrog59.ciste2dot0.gamedata.SceneType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule

class GameDataWriter(activity: Activity) {
    private val resourceFinder=ResourceFinder(activity)
    private val mapper=ObjectMapper().apply { registerModule(KotlinModule()) }
    var gameData= GameDataLoader(activity).loadGameDataFromIntent()

    private fun saveGameData(){
        mapper.writeValue(resourceFinder.getOutputStreamFromURI(),gameData)
    }

    fun addNewSceneToGameData(sceneType: SceneType, sceneName:String) {
        val maxSceneId = gameData.scenes.map(SceneData::sceneId).maxOrNull() ?: 0
        val sceneData = mutableListOf<SceneData>().apply {
            addAll(gameData.scenes)
            add(
                SceneData(
                    maxSceneId + 1,
                    sceneType,
                    ObjectMapper().createObjectNode(),
                    sceneName
                )
            )
        }
        gameData = GameData(
            gameData.starting,
            sceneData,
            gameData.backButtonScene,
            gameData.gameMetaData
        )
        saveGameData()
    }
}