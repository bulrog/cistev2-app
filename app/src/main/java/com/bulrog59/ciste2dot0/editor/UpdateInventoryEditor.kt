package com.bulrog59.ciste2dot0.editor

import android.app.Activity
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.editor.GameOptionHelper.Companion.convertToJsonNode
import com.bulrog59.ciste2dot0.editor.GameOptionHelper.Companion.gamePreviousElement
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.gamedata.Item
import com.bulrog59.ciste2dot0.gamedata.SceneType
import com.bulrog59.ciste2dot0.scenes.update_inventory.UpdateInventoryOptions
import com.fasterxml.jackson.databind.JsonNode

class UpdateInventoryEditor(
    val activity: Activity,
    val gameData: GameData,
    scenePosition: Int,
    val done: (JsonNode) -> Unit
) {
    private var itemsToAdd =
        gamePreviousElement<List<Item>, UpdateInventoryOptions>(
            gameData,
            scenePosition
        ) { it?.itemsToAdd } ?: emptyList()
    private var itemIdsToRemove =
        gamePreviousElement<List<Item>, UpdateInventoryOptions>(
            gameData,
            scenePosition
        ) { getItemList().filter { i -> it?.itemIdsToRemove!!.contains(i.id) } } ?: emptyList()
    private var nextScene = gamePreviousElement<Int, UpdateInventoryOptions>(
        gameData,
        scenePosition
    ) { it?.nextScene }

    private fun getItemList(): List<Item> {
        return gameData.scenes.filter { it.sceneType == SceneType.updateInventory }
            .flatMap { itemsToAdd }
    }

    private fun editItem(previousItem: Item?, done: (Item) -> Unit) {
        //TODO: implement item creation
    }

    private fun removeItem(previousItem: Item?, done: (Item) -> Unit) {
        //TODO: implement item creation
    }


    fun init() {
        activity.setContentView(R.layout.editor_update_inventory)
        activity.findViewById<Button>(R.id.add_menu_button).setOnClickListener {
            ListEditor(activity, itemsToAdd, { l -> l.map { it.name } }, this::editItem, { r ->
                itemsToAdd = r
                init()
            }).init()
        }
        activity.findViewById<Button>(R.id.delete_menu_item_button).setOnClickListener {
            ListEditor(
                activity,
                itemIdsToRemove,
                { l -> l.map { it.name } },
                this::removeItem,
                { r ->
                    itemIdsToRemove = r
                    init()
                })

        }

        //TODO: change menu selector to highlight already selected scene (done) and review for other places where it is used!
        val r = activity.findViewById<RecyclerView>(R.id.next_scene_update_inventory)
        val menuSelector = MenuSelectorAdapter(
            GameOptionHelper.sceneDescriptions(
                gameData.scenes,
                activity
            )
        ) { p ->
            nextScene = gameData.scenes[p].sceneId
        }
        nextScene?.apply {
            menuSelector.positionSelected =
                gameData.scenes.indexOf(gameData.scenes.find { s -> s.sceneId == nextScene })
        }
        r.adapter = menuSelector
        r.layoutManager = LinearLayoutManager(activity)


        activity.findViewById<Button>(R.id.exit_button_menu_title).setOnClickListener {
            if (nextScene == null) {
                Toast.makeText(activity, R.string.element_not_selected, Toast.LENGTH_LONG).show()
            } else {
                done(
                    convertToJsonNode(
                        UpdateInventoryOptions(
                            itemsToAdd,
                            itemIdsToRemove.map { it.id },
                            nextScene!!
                        )
                    )
                )
            }

        }


    }
}