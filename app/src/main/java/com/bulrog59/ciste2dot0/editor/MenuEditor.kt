package com.bulrog59.ciste2dot0.editor

import android.app.Activity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper.Companion.convertToJsonNode
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper.Companion.gamePreviousElement
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper.Companion.sceneDescriptions
import com.bulrog59.ciste2dot0.editor.utils.ItemPicker
import com.bulrog59.ciste2dot0.editor.utils.ListEditor
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
        activity.setContentView(R.layout.editor_entity_name)
        val menuTitleField = activity.findViewById<EditText>(R.id.menu_title_input)
        existingItem?.apply {
            menuTitleField.setText(buttonText)
        }
        activity.findViewById<Button>(R.id.next_button_entity).setOnClickListener {
            menuTitle = menuTitleField.text.toString()
            if (menuTitle.isNullOrEmpty()) {
                Toast.makeText(activity, R.string.empty_field_error, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val itemPicker = ItemPicker(activity)
            existingItem?.apply {
                itemPicker.previousSelection=gameData.scenes.indexOf(gameData.scenes.findLast { it.sceneId==this.nextScene })
            }

            itemPicker.init(
                R.string.next_scene_title,
                sceneDescriptions(gameData.scenes, activity)
            ) {
                done(MenuItem(menuTitle!!, gameData.scenes[it].sceneId))
            }
        }
    }

    fun init() {
        ListEditor(
            activity,
            menuItems,
            { l -> getMenuItemsText(gameData, l) },
            this::editMenuItem,
            { i -> done(convertToJsonNode(MenuOptions(i))) }).init()

    }

}