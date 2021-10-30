package com.bulrog59.ciste2dot0.editor

import android.app.Activity
import android.widget.Button
import android.widget.EditText
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
        //TODO: flatmap does not work as I need to deserialize the object and get the list of items to add
        return gameData.scenes.filter { it.sceneType == SceneType.updateInventory }
            .flatMap { itemsToAdd }
    }

    private fun getItemName(previousItem: Item?, done: (Item) -> Unit) {
        activity.setContentView(R.layout.editor_entity_name)
        val textField = activity.findViewById<EditText>(R.id.menu_title_input)
        previousItem?.apply {
            textField.setText(this.name)
        }
        activity.findViewById<Button>(R.id.next_button_entity).setOnClickListener {
            val name = textField.text.toString()
            if (name.isNullOrEmpty()) {
                Toast.makeText(activity, R.string.empty_field_error, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            getPic(previousItem, name, done)
        }

    }

    private fun getPic(previousItem: Item?, name: String, done: (Item) -> Unit) {
        FilePicker(activity).init(
            R.string.select_picture_text_title,
            FilePickerType.image,
            previousItem?.picture
        ) {
            val id = (getItemList().map { it.id }.maxOrNull() ?: 0) + 1
            done(Item(id, name, it))

        }
    }

    private fun itemToAddEdit(previousItem: Item?, done: (Item) -> Unit) {
        getItemName(previousItem, done)
    }

    private fun removeItemSelection(previousItem: Item?, done: (Item) -> Unit) {
        val itemPicker = ItemPicker(activity)
        val items = getItemList().filter { i-> !itemsToAdd.contains(i) }
        previousItem?.apply {
            itemPicker.previousSelection = items.indexOf(this)
        }
        if (items.isEmpty()){
            Toast.makeText(activity,R.string.no_item_to_select,Toast.LENGTH_LONG).show()
            return
        }
        itemPicker.init(R.string.item_removal_help_text, items.map { it.name }) { done(items[it]) }

    }


    fun init() {
        activity.setContentView(R.layout.editor_update_inventory)
        activity.findViewById<Button>(R.id.add_menu_button).setOnClickListener {
            ListEditor(activity, itemsToAdd, { l -> l.map { it.name } }, this::itemToAddEdit, { r ->
                itemsToAdd = r
                init()
            }).init()
        }
        activity.findViewById<Button>(R.id.delete_menu_item_button).setOnClickListener {
            //TODO: to review as cannot edit an existing element
            ListEditor(
                activity,
                itemIdsToRemove,
                { l -> l.map { it.name } },
                this::removeItemSelection,
                { r ->
                    itemIdsToRemove = r
                    init()
                }).init()

        }

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


        activity.findViewById<Button>(R.id.next_button_entity).setOnClickListener {
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