package com.bulrog59.ciste2dot0.game.management

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.ResourceManager
import com.bulrog59.ciste2dot0.editor.utils.FilePickerType
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper
import com.bulrog59.ciste2dot0.game.management.GameUtil.Companion.mapper
import com.bulrog59.ciste2dot0.game.management.GameUtil.Companion.retrieveOption
import com.bulrog59.ciste2dot0.game.management.GamesDataManager.Companion.MAX_SIZE_IN_MB
import com.bulrog59.ciste2dot0.gamedata.*
import com.bulrog59.ciste2dot0.scenes.detector.DetectorOption
import com.bulrog59.ciste2dot0.scenes.inventory.InventoryOptions
import com.bulrog59.ciste2dot0.scenes.menu.MenuOptions
import com.bulrog59.ciste2dot0.scenes.pic.PicMusicOption
import com.bulrog59.ciste2dot0.scenes.rules.RulesOptions
import com.bulrog59.ciste2dot0.scenes.update_inventory.UpdateInventoryOptions
import com.bulrog59.ciste2dot0.scenes.video.VideoOption
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.*

class GameDataWriter(val activity: Activity) {
    private val resourceFinder = ResourceManager(activity)
    private val folderGame = activity.filesDir.absolutePath + GamesDataManager.FOLDER_FOR_GAME_DATA
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
                .replace(Regex("VAR_SIZE"), (size / 1e6).toInt().toString())
                .replace(Regex("MAX_SIZE"), MAX_SIZE_IN_MB.toString())
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

    private fun filterUnusedImageFromInventoryUpdate(allImages: List<String>): List<String> {
        return filterUnusedFrom<UpdateInventoryOptions>(
            allImages,
            SceneType.updateInventory
        ) { o ->
            o.itemsToAdd.map { it.picture }
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
        return filterUnusedImageFromInventoryUpdate(
            filterUnusedImageFromDetector(
                filterUnusedImageFromPicMusicOptions(
                    allImages)))
    }

    fun clearUnusedFiles() {
        val unusedResources = mutableListOf<String>()
        unusedResources.addAll(filterUnusedImages(resourceFinder.listResourceOfType(FilePickerType.image)))
        unusedResources.addAll(filterUnusedVideo(resourceFinder.listResourceOfType(FilePickerType.video)))
        unusedResources.addAll(filterUnusedMusic(resourceFinder.listResourceOfType(FilePickerType.audio)))
        unusedResources.forEach { r -> resourceFinder.deleteResource(r) }

    }


    private fun reportGameSize(gameId: UUID) {


        //put limit on unzipped data as faster to check and also quite similar as video/pic... are already compressed:
        val gameSize = FileUtils.sizeOfDirectory(File("$folderGame$gameId"))

        Toast.makeText(
            activity,
            makeSizeString(activity, R.string.game_size_info, gameSize),
            Toast.LENGTH_LONG
        ).show()

    }

    private fun updateGameDataWithScenes(scenesData: List<SceneData>) {

        gameData = GameData(
            gameData.starting,
            scenesData.sortedByDescending { it.sceneId },
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

    fun updateBackButtonScene(backButtonSceneId: Int) {
        gameData = GameData(
            gameData.starting,
            gameData.scenes,
            backButtonSceneId,
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
                    mapper.createObjectNode(),
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

    private fun getWhereInUseFrom(
        sceneType: SceneType,
        itemInUseChecker: (sceneData: SceneData) -> Boolean
    ): String {
        return gameData.scenes
            .filter { it.sceneType == sceneType }
            .filter { itemInUseChecker(it) }
            .joinToString(",") { GameOptionHelper.getSceneDescription(it) }
    }

    private fun getInventoriesWhereItemInUse(itemID: Int): String {
        return getWhereInUseFrom(SceneType.inventory) { sceneData ->
            retrieveOption<InventoryOptions>(sceneData).combinations.any { it.id1 == itemID || it.id2 == itemID }
        }
    }


    private fun getRuleEngineWhereItemInUse(itemID: Int): String {
        return getWhereInUseFrom(SceneType.ruleEngine) {
            retrieveOption<RulesOptions>(it).rules.any { rule -> rule.itemIds.contains(itemID) }
        }
    }


    fun verifyIfItemIsUsed(item: Item): String {
        val listOfScenesInUsed =
            getInventoriesWhereItemInUse(item.id) + "," + getRuleEngineWhereItemInUse(item.id)

        if ("," != listOfScenesInUsed) {
            return activity.getText(R.string.item_in_use_error)
                .replace(Regex("VAR_ITEM"), item.name)
                .replace(Regex("VAR_SCENES"), listOfScenesInUsed)
        }

        return ""
    }

    fun verifyIfSomeItemsAreUsed(sceneDataToDelete: SceneData): String {
        if (sceneDataToDelete.sceneType != SceneType.updateInventory) {
            return ""
        }
        val updateInvOptions = retrieveOption<UpdateInventoryOptions>(sceneDataToDelete)
        val errorMessage =
            updateInvOptions.itemsToAdd
                .map { verifyIfItemIsUsed(it) }
                .filter { it.isNotEmpty() }
                .joinToString(",")
        if (errorMessage.isEmpty()) {
            return ""
        }

        return "$errorMessage. "
    }

    private inline fun <reified T> verifyIfSceneIsUsedAsNextSceneForType(
        sceneType: SceneType,
        sceneIDToDelete: Int,
        getNextScene: (T) -> List<Int>
    ): String {
        return gameData.scenes.filter { sceneData ->
            sceneData.sceneType == sceneType
                    && sceneData.sceneId != sceneIDToDelete
                    && !sceneData.options.isEmpty
        }
            .filter { getNextScene(retrieveOption(it)).contains(sceneIDToDelete) }
            .joinToString(",") { GameOptionHelper.getSceneDescription(it) }

    }

    private fun verifyUsedByPicMusicOption(sceneIDToDelete: Int): String {
        return verifyIfSceneIsUsedAsNextSceneForType<PicMusicOption>(
            SceneType.picMusic,
            sceneIDToDelete
        ) { listOf(it.nextScene) }
    }

    private fun verifyUsedByVideoOption(sceneIDToDelete: Int): String {
        return verifyIfSceneIsUsedAsNextSceneForType<VideoOption>(
            SceneType.video,
            sceneIDToDelete
        ) { listOf(it.nextScene) }
    }

    private fun verifyUsedByRulesOptions(sceneIDToDelete: Int): String {
        return verifyIfSceneIsUsedAsNextSceneForType<RulesOptions>(
            SceneType.ruleEngine, sceneIDToDelete
        ) { r ->
            mutableListOf<Int>().apply {
                addAll(r.rules.map { it.nextScene })
                add(r.defaultScene)
            }
        }
    }

    private fun verifyUsedByInventoryOptions(sceneIDToDelete: Int): String {
        return verifyIfSceneIsUsedAsNextSceneForType<InventoryOptions>(
            SceneType.inventory,
            sceneIDToDelete
        )
        { it.combinations.map { c -> c.nextScene } }
    }

    private fun verifyUsedByUpdateInventoryOptions(sceneIDToDelete: Int): String {
        return verifyIfSceneIsUsedAsNextSceneForType<UpdateInventoryOptions>(
            SceneType.updateInventory,
            sceneIDToDelete
        )
        { listOf(it.nextScene) }
    }

    private fun verifyUsedByDetectorOption(sceneIDToDelete: Int): String {
        return verifyIfSceneIsUsedAsNextSceneForType<DetectorOption>(
            SceneType.detector,
            sceneIDToDelete
        ) { d -> d.pic2Scene.map { it.value } }
    }

    private fun verifyUsedByMenuOptions(sceneIDToDelete: Int): String {
        return verifyIfSceneIsUsedAsNextSceneForType<MenuOptions>(
            SceneType.menu,
            sceneIDToDelete
        ) { m -> m.menuItems.map { it.nextScene } }
    }

    private fun verifyIfSceneIsUsedAsNextScene(sceneIDToDelete: Int): String {
        val scenesInUsed = listOf(
            verifyUsedByPicMusicOption(sceneIDToDelete),
            verifyUsedByVideoOption(sceneIDToDelete),
            verifyUsedByRulesOptions(sceneIDToDelete),
            verifyUsedByInventoryOptions(sceneIDToDelete),
            verifyUsedByUpdateInventoryOptions(sceneIDToDelete),
            verifyUsedByDetectorOption(sceneIDToDelete),
            verifyUsedByMenuOptions(sceneIDToDelete)
        ).filter { it.isNotEmpty() }.joinToString(",")

        if (scenesInUsed.isNotEmpty()) {
            return "${activity.getText(R.string.cannot_delete_scene)}$scenesInUsed"
        }
        return ""
    }

    private fun verifySceneIsNotStartingScene(sceneIDToDelete: Int): String {
        return if (sceneIDToDelete == gameData.starting) activity.getString(R.string.cannot_delete_starting_scene) else ""
    }

    private fun verifySceneIsNotBackButtonScene(sceneIDToDelete: Int): String {
        return if (sceneIDToDelete == gameData.backButtonScene) activity.getString(R.string.cannot_delete_scene_back_button) else ""
    }

    fun verifyCanDeleteAScene(sceneIDToDelete: Int): String {
        val sceneDataToDelete = gameData.scenes.filter { s -> s.sceneId == sceneIDToDelete }[0]
        //TODO: verify scene is back button scene
        var errorMessage = verifySceneIsNotStartingScene(sceneIDToDelete) +
                verifySceneIsNotBackButtonScene(sceneIDToDelete) +
                verifyIfSomeItemsAreUsed(sceneDataToDelete) +
                verifyIfSceneIsUsedAsNextScene(sceneDataToDelete.sceneId)
        return errorMessage
    }
}