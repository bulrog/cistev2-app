package com.bulrog59.ciste2dot0.editor

import android.app.Activity
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper.Companion.convertToJsonNode
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper.Companion.getItemList
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper.Companion.getItemPickerNextScene
import com.bulrog59.ciste2dot0.editor.utils.ItemPicker
import com.bulrog59.ciste2dot0.editor.utils.ListEditor
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.scenes.inventory.Combination
import com.bulrog59.ciste2dot0.scenes.inventory.InventoryOptions
import com.fasterxml.jackson.databind.JsonNode

class InventoryEditor(
    //TODO: make them private on other editors:
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


    private fun getSecondItem(
        firstItemPosition: Int,
        combination: Combination?,
        done: (Combination) -> Unit
    ) {
        val allItems= getItemList(gameData)
        val remainingItems =
            allItems.filter { item -> item.id != allItems[firstItemPosition].id }
        ItemPicker(activity).init(
            R.string.second_item_selection_title,
            remainingItems.map { it.name }) { secondItem ->

            //TODO: verify if combination is not existing already else refuse it
            getItemPickerNextScene<InventoryEditor>(
                activity,
                gameData,
                scenePosition,
                { combination?.nextScene }) {
                done(Combination(allItems[firstItemPosition].id, remainingItems[secondItem].id, it))
            }
        }


    }

    private fun editCombination(combination: Combination?, done: (Combination) -> Unit) {
        ItemPicker(activity).init(
            R.string.first_item_selection_title,
            getItemList(gameData).map { it.name }) {
            getSecondItem(it, combination, done)
        }
    }

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
        }.init()
    }
}