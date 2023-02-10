package com.bulrog59.ciste2dot0.editor

import android.app.Activity
import android.widget.Toast
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper.Companion.convertToJsonNode
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper.Companion.getItemDescription
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper.Companion.getItemList
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper.Companion.getItemPickerNextScene
import com.bulrog59.ciste2dot0.editor.utils.ItemPicker
import com.bulrog59.ciste2dot0.editor.utils.ListEditor
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.gamedata.Item
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
        return getItemList(gameData).findLast { it.id == itemID }?.let { getItemDescription(it) } ?: ""
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


    private fun combinationAlreadyExists(
        combination: Combination,
        allCombination: List<Combination>
    ): Boolean {
        return allCombination.filter { c -> (c.id1 == combination.id1 && c.id2 == combination.id2) || (c.id2 == combination.id1 && c.id1 == combination.id2) }
            .count() > 1
    }

    private fun areSomeCombinationsRedundant(combinations: List<Combination>): Boolean {
        return combinations.any { c -> combinationAlreadyExists(c, combinations) }
    }

    private fun getSecondItem(
        firstItemPosition: Int,
        combination: Combination?,
        done: (Combination) -> Unit
    ) {
        val allItems = getItemList(gameData)
        val remainingItems =
            allItems.filter { item -> item.id != allItems[firstItemPosition].id }
        val itemPicker=ItemPicker(activity)
        combination?.apply {
            itemPicker.previousSelection=remainingItems.indexOf(remainingItems.findLast { it.id==combination.id2 })
        }

        itemPicker.init(
            R.string.second_item_selection_title,
            remainingItems.map { getItemDescription(it) }) { secondItem ->
            val firstItemId = allItems[firstItemPosition].id
            val secondItemId = remainingItems[secondItem].id

            getItemPickerNextScene<InventoryOptions>(
                activity,
                gameData,
                scenePosition,
                { combination?.nextScene }) {
                done(Combination(firstItemId, secondItemId, it))
            }

        }

    }

    private fun editCombination(combination: Combination?, done: (Combination) -> Unit) {
        val allItems=getItemList(gameData)
        val itemPicker=ItemPicker(activity)
        combination?.apply {
            itemPicker.previousSelection=allItems.indexOf(allItems.findLast { it.id==combination.id1 })
        }
        itemPicker.init(
            R.string.first_item_selection_title,
            allItems.map { getItemDescription(it) }) {
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
            if (areSomeCombinationsRedundant(it)) {
                Toast.makeText(activity, R.string.combination_exists_error, Toast.LENGTH_LONG)
                    .show()
                return@ListEditor
            }
            done(convertToJsonNode(InventoryOptions(it)))
        }.init()
    }
}