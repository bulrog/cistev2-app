package com.bulrog59.ciste2dot0.editor

import android.app.Activity
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper.Companion.convertToJsonNode
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper.Companion.getItemList
import com.bulrog59.ciste2dot0.editor.utils.ListEditor
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.scenes.inventory.Combination
import com.bulrog59.ciste2dot0.scenes.inventory.InventoryOptions
import com.fasterxml.jackson.databind.JsonNode

class InventoryEditor(
    private val activity: Activity,
    private val gameData: GameData,
    private val scenePosition: Int,
    private val done: (JsonNode) -> Unit
) {

    private fun getItemName(itemID: Int): String {
        return getItemList(gameData).findLast { it.id == itemID }?.name ?: ""
    }

    private fun getSceneName(sceneId: Int): String {
        return gameData.scenes.findLast { s -> s.sceneId == sceneId }?.name ?: ""
    }

    private fun getItemText(combinations: List<Combination>): List<String> {
        return combinations.map { c ->
            "${getItemName(c.id1)}/${getItemName(c.id2)}->${c.nextScene}:${
                getSceneName(
                    c.nextScene
                )
            }"
        }
    }

    private fun editCombination(combination: Combination?, done: (Combination) -> Unit) {
        //TODO: implement
    }

    private var combinations =
        GameOptionHelper.gamePreviousElement<List<Combination>, InventoryOptions>(
            gameData,
            scenePosition
        ) { it?.combinations } ?: emptyList()

    fun init() {

        val combinations =
            GameOptionHelper.gamePreviousElement<List<Combination>, InventoryOptions>(
                gameData,
                scenePosition
            ) { it?.combinations } ?: emptyList()

        ListEditor(
            activity,
            combinations,
            this::getItemText,
            this::editCombination
        ) {
            done(convertToJsonNode(InventoryOptions(it)))
        }
    }
}