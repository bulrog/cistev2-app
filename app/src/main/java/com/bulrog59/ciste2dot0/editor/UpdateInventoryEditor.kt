package com.bulrog59.ciste2dot0.editor

import android.app.Activity
import android.net.Uri
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.editor.utils.*
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper.Companion.convertToJsonNode
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper.Companion.gamePreviousElement
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper.Companion.getItemList
import com.bulrog59.ciste2dot0.game.management.GameDataWriter
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.gamedata.Item
import com.bulrog59.ciste2dot0.scenes.update_inventory.UpdateInventoryOptions
import com.fasterxml.jackson.databind.JsonNode

class UpdateInventoryEditor(
    private val activity: Activity,
    private val gameData: GameData,
    scenePosition: Int,
    private val done: (JsonNode) -> Unit
) : CallBackActivityResult {


    companion object {
        private const val NO_ID = -1
    }

    private var filePicker: FilePicker? = null
    private val gameDataWriter=GameDataWriter(activity)

    override fun callBack(uri: Uri?, requestCode: Int) {
        filePicker?.callBack(uri, requestCode)
    }


    private var itemsToAdd =
        gamePreviousElement<List<Item>, UpdateInventoryOptions>(
            gameData,
            scenePosition
        ) { it?.itemsToAdd } ?: emptyList()
    private var itemIdsToRemove =
        gamePreviousElement<List<Item>, UpdateInventoryOptions>(
            gameData,
            scenePosition
        ) { getItemList(gameData).filter { i -> it?.itemIdsToRemove?.contains(i.id) ?: false } }
            ?: emptyList()
    private var nextScene = gamePreviousElement<Int, UpdateInventoryOptions>(
        gameData,
        scenePosition
    ) { it?.nextScene }


    private fun getItemName(previousItem: Item?, done: (Item) -> Unit) {
        activity.setContentView(R.layout.editor_entity_name)
        val textField = activity.findViewById<EditText>(R.id.menu_title_input)
        previousItem?.apply {
            textField.setText(this.name)
        }
        activity.findViewById<Button>(R.id.next_button_entity).setOnClickListener {
            val name = textField.text.toString()
            if (name.isEmpty()) {
                Toast.makeText(activity, R.string.empty_field_error, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            getPic(previousItem, name, done)
        }

    }

    private fun getPic(previousItem: Item?, name: String, done: (Item) -> Unit) {
        filePicker = FilePicker(activity).apply {
            init(
                R.string.select_picture_text_title,
                FilePickerType.image,
                previousItem?.picture
            ) { pic ->

                //cannot set the ID as this function is called by the list editor which has its own mutated
                //item list, so will defer the set of the id when we get back the list from the list editor:
                done(Item(NO_ID, name, pic))

            }
        }

    }

    private fun itemToAddEdit(previousItem: Item?, done: (Item) -> Unit) {
        getItemName(previousItem, done)
    }

    private fun removeItemSelection(previousItem: Item?, done: (Item) -> Unit) {

        if (previousItem != null) {
            Toast.makeText(activity, R.string.item_removal_cannot_edit, Toast.LENGTH_LONG).show()
            return
        }
        val itemPicker = ItemPicker(activity)
        val items = getItemList(gameData).filter { i -> !itemsToAdd.contains(i) }
        if (items.isEmpty()) {
            Toast.makeText(activity, R.string.no_item_to_select, Toast.LENGTH_LONG).show()
            return
        }
        itemPicker.init(R.string.item_removal_help_text, items.map { it.name }) { done(items[it]) }

    }

    private fun updateItemsToAddWithIds(itemsFromListEditor: List<Item>) {
        val itemsToAddNew = mutableListOf<Item>()
        var maxItemIdInGame = (mutableListOf<Item>().apply {
            addAll(getItemList(gameData))
            addAll(itemsToAdd)
        }.map { it.id }.maxOrNull() ?: 0) + 1

        itemsFromListEditor.forEach {
            var itemID = it.id
            if (itemID == NO_ID) {
                itemID = maxItemIdInGame++
            }
            itemsToAddNew.add(Item(itemID, it.name, it.picture))
        }
        itemsToAdd = itemsToAddNew
    }


    fun init() {
        activity.setContentView(R.layout.editor_update_inventory)
        activity.findViewById<Button>(R.id.add_menu_button).setOnClickListener {
            ListEditor(activity, itemsToAdd, { l -> l.map { it.name } }, this::itemToAddEdit, { r ->
                updateItemsToAddWithIds(r)
                init()
            }).apply { validatorItemCanBeRemoved = { gameDataWriter.verifyIfItemIsUsed(it) } }
                .init()
        }
        activity.findViewById<Button>(R.id.delete_menu_item_button).setOnClickListener {
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