package com.bulrog59.ciste2dot0.game.management

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.ResourceManager
import com.bulrog59.ciste2dot0.game.management.GamesDataManager.Companion.MAX_SIZE_IN_MB
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.gamedata.SceneData
import com.bulrog59.ciste2dot0.gamedata.SceneType
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.File
import java.util.*

class GameDataWriter(val activity: Activity) {
    private val resourceFinder=ResourceManager(activity)
    private val folderGame = activity.filesDir.absolutePath + GamesDataManager.FOLDER_FOR_GAME_DATA
    private val mapper=ObjectMapper().apply { registerModule(KotlinModule()) }
    var gameData= GameDataLoader(activity).loadGameDataFromIntent()

    private fun saveGameData(){
        mapper.writeValue(resourceFinder.getOutputStreamFromURI(ResourceManager.GAME_RESOURCE_NAME),gameData)
        gameData.gameMetaData?.id?.apply { reportGameSize(this) }
    }
    companion object {
        fun makeSizeString(context: Context, stringTemplate:Int, size:Long):String{
            return context.getText(stringTemplate)
                .replace(Regex("VARSIZE"),(size/1e6).toInt().toString())
                .replace(Regex("MAXSIZE"),MAX_SIZE_IN_MB.toString())
        }
    }


    private fun reportGameSize(gameId: UUID){
        val zipFileName="${folderGame}dummy.zip"
        val zipSize=ZipUtils.zipAll("$folderGame$gameId", zipFileName)
        File(zipFileName).delete()

        Toast.makeText(activity,
            makeSizeString(activity,R.string.game_size_info,zipSize),
            Toast.LENGTH_LONG).show()

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

    fun updateGameMetaData(gameMetaData: GameMetaData){
        gameData= GameData(
            gameData.starting,
            gameData.scenes,
            gameData.backButtonScene,
            gameMetaData
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