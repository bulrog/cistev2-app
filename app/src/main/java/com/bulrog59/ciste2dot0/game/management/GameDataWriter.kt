package com.bulrog59.ciste2dot0.game.management

import android.app.Activity
import com.bulrog59.ciste2dot0.ResourceManager
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.gamedata.SceneData
import com.bulrog59.ciste2dot0.gamedata.SceneType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule

class GameDataWriter(activity: Activity) {
    private val resourceFinder=ResourceManager(activity)
    private val mapper=ObjectMapper().apply { registerModule(KotlinModule()) }
    var gameData= GameDataLoader(activity).loadGameDataFromIntent()

    private fun saveGameData(){
        mapper.writeValue(resourceFinder.getOutputStreamFromURI(ResourceManager.GAME_RESOURCE_NAME),gameData)
    }

    private fun updateGameDataWithScenes(scenesData: List<SceneData>){

        gameData = GameData(
            gameData.starting,
            scenesData.sortedBy { it.sceneId},
            gameData.backButtonScene,
            gameData.gameMetaData
        )
        saveGameData()

    }

    fun updateStartingScene(startSceneId:Int){
        gameData = GameData(
            startSceneId,
            gameData.scenes,
            gameData.backButtonScene,
            gameData.gameMetaData
        )
        saveGameData()
    }

    fun deleteScene(sceneId:Int){
       updateGameDataWithScenes(gameData.scenes.filter { it.sceneId!=sceneId })
    }

    fun addNewSceneToGameData(sceneType: SceneType, sceneName:String) {
        val maxSceneId = gameData.scenes.map(SceneData::sceneId).maxOrNull() ?: 0
        val scenesData = mutableListOf<SceneData>().apply {
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
        updateGameDataWithScenes(scenesData)
    }

    fun addOrUpdateSceneData(sceneData: SceneData) {
        val scenesData = mutableListOf<SceneData>().apply {
            addAll(gameData.scenes.filter { it.sceneId != sceneData.sceneId })
            add(sceneData)
        }
        updateGameDataWithScenes(scenesData)

    }
}