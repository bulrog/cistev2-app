package com.bulrog59.ciste2dot0.editor

import android.app.Activity
import android.app.AlertDialog
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.editor.GameOptionHelper.Companion.convertToJsonNode
import com.bulrog59.ciste2dot0.editor.GameOptionHelper.Companion.gamePreviousElement
import com.bulrog59.ciste2dot0.editor.GameOptionHelper.Companion.sceneDescriptions
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.scenes.menu.MenuItem
import com.bulrog59.ciste2dot0.scenes.menu.MenuOptions
import com.fasterxml.jackson.databind.JsonNode

class MenuEditor(
    val activity: Activity,
    val gameData: GameData,
    scenePosition: Int,
    val done: (JsonNode) -> Unit
) {

    private var menuItems = gamePreviousElement<List<MenuItem>, MenuOptions>(
        gameData,
        scenePosition
    ) { it?.menuItems } ?: emptyList()

    private fun getMenuItemsText(gameData: GameData, menuItems: List<MenuItem>): List<String> {
        return menuItems.map { menuItem ->
            val nextScene = gameData.scenes.filter { it.sceneId == menuItem.nextScene }[0]
            "${menuItem.buttonText}->${nextScene.sceneId}:${nextScene.name}"
        }
    }

    private fun editMenuItem(existingItem: MenuItem?, done: (MenuItem) -> Unit) {
        var menuTitle: String?
        activity.setContentView(R.layout.editor_menu_title)
        val menuTitleField = activity.findViewById<EditText>(R.id.menu_title_input)
        existingItem?.apply {
            menuTitleField.setText(buttonText)
        }
        activity.findViewById<Button>(R.id.exit_button_menu_title).setOnClickListener {
            menuTitle = menuTitleField.text.toString()
            if (menuTitle.isNullOrEmpty()) {
                Toast.makeText(activity, R.string.empty_field_error, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            ItemPicker(activity).init(
                R.string.next_scene_title,
                sceneDescriptions(gameData.scenes, activity)
            ) {
                done(MenuItem(menuTitle!!, gameData.scenes[it].sceneId))
            }
        }
    }

    private fun updateMenuItems(
        previousMenuItem: MenuItem?,
        updater: (MutableList<MenuItem>, MenuItem) -> Unit
    ) {
        editMenuItem(previousMenuItem) { m ->
            menuItems =
                mutableListOf<MenuItem>().apply { addAll(menuItems) }.also { updater(it, m) }
            selectMenuToEditOrAdd()
        }
    }

    private fun deleteMenuItem() {
        ItemPicker(activity).init(
            R.string.select_element_to_delete,
            getMenuItemsText(gameData, menuItems)
        ) {
            AlertDialog.Builder(activity)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage(R.string.delete_item_message)
                .setPositiveButton(R.string.confirmation) { _, _ ->
                    menuItems = mutableListOf<MenuItem>().apply {
                        addAll(menuItems)
                        removeAt(it)
                    }
                    selectMenuToEditOrAdd()
                }
                .setNegativeButton(R.string.denial) { _, _ -> selectMenuToEditOrAdd() }
                .show()

        }

    }

    private fun selectMenuToEditOrAdd() {
        activity.setContentView(R.layout.editor_menu)
        val recyclerView = activity.findViewById<RecyclerView>(R.id.item_menu_selection)
        recyclerView.adapter =
            MenuSelectorAdapter(getMenuItemsText(gameData, menuItems)) { p ->
                updateMenuItems(menuItems[p]) { l, m -> l[p] = m }
            }
        recyclerView.layoutManager = LinearLayoutManager(activity)
        activity.findViewById<Button>(R.id.add_menu_button).setOnClickListener {
            updateMenuItems(null) { l, m -> l.add(m) }
        }

        activity.findViewById<Button>(R.id.delete_menu_item_button).setOnClickListener {
            deleteMenuItem()
        }
        activity.findViewById<Button>(R.id.exit_button_menu_title).setOnClickListener {
            done(convertToJsonNode(MenuOptions(menuItems)))
        }

    }

    fun init() {
        selectMenuToEditOrAdd()

    }

}