package com.bulrog59.ciste2dot0.editor.utils

import android.app.Activity
import android.content.Context
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.game.management.GameUtil.Companion.mapper
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.gamedata.Item
import com.bulrog59.ciste2dot0.gamedata.SceneData
import com.bulrog59.ciste2dot0.gamedata.SceneType
import com.bulrog59.ciste2dot0.scenes.update_inventory.UpdateInventoryOptions
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.treeToValue

class GameOptionHelper {
    companion object {
        inline fun <reified T> getSceneOptions(gameData: GameData, scenePosition: Int): T? {
            val options = gameData.scenes[scenePosition].options
            return if (options.isEmpty) null else mapper.treeToValue<T>(options)
        }

        inline fun <T, reified O> gamePreviousElement(
            gameData: GameData,
            scenePosition: Int,
            getterFunction: (O?) -> T?
        ): T? {
            return getterFunction(getSceneOptions(gameData, scenePosition))
        }

        fun sceneDescriptions(scenes: List<SceneData>, context: Context): List<String> {
            return scenes.map {
                "${getSceneDescription(it)} (${context.getText(it.sceneType.description)})"
            }
        }

        fun getSceneDescription(scene: SceneData):String{
            return "${scene.sceneId}:${scene.name?: "none"}"
        }

        fun getSceneDescription(gameData: GameData,sceneId:Int):String{
            val scene = gameData.scenes.filter { it.sceneId == sceneId }[0]
            return getSceneDescription(scene)

        }

        fun <T> convertToJsonNode(data: T): JsonNode {
            return mapper.readTree(
                mapper.writeValueAsString(
                    data
                )
            )
        }

        inline fun <reified T> getItemPickerNextScene(
            activity: Activity,
            gameData: GameData,
            scenePosition: Int,
            getNextScene: (T?) -> Int?,
            crossinline done: (Int) -> Unit
        ) {
            val otherScenes = mutableListOf<SceneData>().apply {
                addAll(gameData.scenes)
                removeAt(scenePosition)
            }

            val itemPicker = ItemPicker(activity)
            gamePreviousElement<Int, T>(
                gameData,
                scenePosition
            ) {
                getNextScene(it)
            }?.apply {
                itemPicker.previousSelection =
                    otherScenes.indexOf(otherScenes.find { s -> s.sceneId == this })
            }
            itemPicker.init(
                R.string.next_scene_title,
                sceneDescriptions(otherScenes, activity)
            ) { p ->
                done(otherScenes[p].sceneId)
            }
        }

        fun getItemList(gameData: GameData): List<Item> {
            return gameData.scenes.filter { it.sceneType == SceneType.updateInventory }
                .filter { !it.options.isEmpty }
                .mapNotNull { mapper.treeToValue<UpdateInventoryOptions>(it.options)?.itemsToAdd }
                .flatten()

        }
    }
}