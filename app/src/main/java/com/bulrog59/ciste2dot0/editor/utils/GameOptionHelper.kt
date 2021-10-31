package com.bulrog59.ciste2dot0.editor.utils

import android.app.Activity
import android.content.Context
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.gamedata.Item
import com.bulrog59.ciste2dot0.gamedata.SceneData
import com.bulrog59.ciste2dot0.gamedata.SceneType
import com.bulrog59.ciste2dot0.scenes.inventory.Combination
import com.bulrog59.ciste2dot0.scenes.inventory.InventoryOptions
import com.bulrog59.ciste2dot0.scenes.update_inventory.UpdateInventoryOptions
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.treeToValue

class GameOptionHelper {
    companion object {
        val om = ObjectMapper().apply { registerModule(KotlinModule()) }
        inline fun <reified T> getSceneOptions(gameData: GameData, scenePosition: Int): T? {
            val options = gameData.scenes[scenePosition].options
            return if (options.isEmpty) null else om.treeToValue<T>(options)
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
                "${it.sceneId}:${it.name ?: "none"} (${context.getText(it.sceneType.description)})"
            }
        }

        fun <T> convertToJsonNode(data: T): JsonNode {
            return om.readTree(
                om.writeValueAsString(
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
                .mapNotNull { om.treeToValue<UpdateInventoryOptions>(it.options)?.itemsToAdd }
                .flatten()

        }

        fun getOtherCombinationList(gameData: GameData, scenePosition: Int): List<Combination> {
            return gameData.scenes
                .filter { it.sceneId != gameData.scenes[scenePosition].sceneId
                        && it.sceneType == SceneType.inventory
                        && !it.options.isEmpty }
                .mapNotNull { om.treeToValue<InventoryOptions>(it.options)?.combinations }
                .flatten()
        }
    }
}