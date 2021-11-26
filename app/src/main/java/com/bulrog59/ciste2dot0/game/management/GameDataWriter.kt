package com.bulrog59.ciste2dot0.game.management

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.ResourceManager
import com.bulrog59.ciste2dot0.editor.utils.FilePickerType
import com.bulrog59.ciste2dot0.game.management.GameUtil.Companion.retrieveOption
import com.bulrog59.ciste2dot0.game.management.GamesDataManager.Companion.MAX_SIZE_IN_MB
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.gamedata.SceneData
import com.bulrog59.ciste2dot0.gamedata.SceneType
import com.bulrog59.ciste2dot0.scenes.detector.DetectorOption
import com.bulrog59.ciste2dot0.scenes.pic.PicMusicOption
import com.bulrog59.ciste2dot0.scenes.video.VideoOption
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.File
import java.util.*

class GameDataWriter(val activity: Activity) {
    private val resourceFinder = ResourceManager(activity)
    private val folderGame = activity.filesDir.absolutePath + GamesDataManager.FOLDER_FOR_GAME_DATA
    private val mapper = ObjectMapper().apply { registerModule(KotlinModule()) }
    var gameData = GameDataLoader(activity).loadGameDataFromIntent()

    private fun saveGameData() {
        mapper.writeValue(
            resourceFinder.getOutputStreamFromURI(ResourceManager.GAME_RESOURCE_NAME),
            gameData
        )
        gameData.gameMetaData?.id?.apply { reportGameSize(this) }
    }

    companion object {
        fun makeSizeString(context: Context, stringTemplate: Int, size: Long): String {
            return context.getText(stringTemplate)
                .replace(Regex("VARSIZE"), (size / 1e6).toInt().toString())
                .replace(Regex("MAXSIZE"), MAX_SIZE_IN_MB.toString())
        }
    }


    private inline fun <reified T> filterUnusedFrom(
        allResources: List<String>,
        sceneType: SceneType,
        getResourceFromOption: (T) -> Collection<String>
    ): List<String> {
        val picMusicPictures = gameData.scenes
            .filter { s -> !s.options.isEmpty && s.sceneType == sceneType }
            .flatMap { s -> getResourceFromOption(retrieveOption(s)) }
        return allResources.filter { i -> !picMusicPictures.contains(i) }
    }

    private fun filterUnusedImageFromPicMusicOptions(allImages: List<String>): List<String> {
        return filterUnusedFrom<PicMusicOption>(
            allImages,
            SceneType.picMusic
        ) { o -> listOf(o.imageName) }
    }

    private fun filterUnusedImageFromDetector(allImages: List<String>): List<String> {
        return filterUnusedFrom<DetectorOption>(
            allImages,
            SceneType.detector
        ) { o ->
            o.pic2Scene.keys
        }
    }

    private fun filterUnusedVideo(allVideo: List<String>): List<String> {
        return filterUnusedFrom<VideoOption>(
            allVideo,
            SceneType.video
        ) { o -> listOf(o.videoName) }
    }

    private fun filterUnusedMusic(allMusic: List<String>): List<String> {
        return filterUnusedFrom<PicMusicOption>(
            allMusic,
            SceneType.picMusic
        ) { o -> listOf(o.musicName) }
    }

    private fun filterUnusedImages(allImages: List<String>): List<String> {
        return filterUnusedImageFromDetector(filterUnusedImageFromPicMusicOptions(allImages))
    }

    fun clearUnusedFiles() {
        val unusedResources = mutableListOf<String>()
        unusedResources.addAll(filterUnusedImages(resourceFinder.listResourceOfType(FilePickerType.image)))
        unusedResources.addAll(filterUnusedVideo(resourceFinder.listResourceOfType(FilePickerType.video)))
        unusedResources.addAll(filterUnusedMusic(resourceFinder.listResourceOfType(FilePickerType.audio)))
        unusedResources.forEach { r-> resourceFinder.deleteResource(r) }

    }


    private fun reportGameSize(gameId: UUID) {
        val zipFileName = "${folderGame}dummy.zip"
        val zipSize = ZipUtils.zipAll("$folderGame$gameId", zipFileName)
        File(zipFileName).delete()

        Toast.makeText(
            activity,
            makeSizeString(activity, R.string.game_size_info, zipSize),
            Toast.LENGTH_LONG
        ).show()

    }

    private fun updateGameDataWithScenes(scenesData: List<SceneData>) {

        gameData = GameData(
            gameData.starting,
            scenesData.sortedBy { it.sceneId },
            gameData.backButtonScene,
            gameData.gameMetaData
        )
        saveGameData()

    }

    fun updateStartingScene(startSceneId: Int) {
        gameData = GameData(
            startSceneId,
            gameData.scenes,
            gameData.backButtonScene,
            gameData.gameMetaData
        )
        saveGameData()
    }

    fun updateGameMetaData(gameMetaData: GameMetaData) {
        gameData = GameData(
            gameData.starting,
            gameData.scenes,
            gameData.backButtonScene,
            gameMetaData
        )
        saveGameData()
    }

    fun deleteScene(sceneId: Int) {
        updateGameDataWithScenes(gameData.scenes.filter { it.sceneId != sceneId })
    }

    fun addNewSceneToGameData(sceneType: SceneType, sceneName: String) {
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