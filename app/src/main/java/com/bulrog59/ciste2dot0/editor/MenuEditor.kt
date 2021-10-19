package com.bulrog59.ciste2dot0.editor

import android.app.Activity
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.editor.GameOptionHelper.Companion.gamePreviousElement
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.scenes.menu.MenuItem
import com.bulrog59.ciste2dot0.scenes.menu.MenuOptions
import com.fasterxml.jackson.databind.JsonNode

class MenuEditor(
    val activity: Activity,
    val gameData: GameData,
    val scenePosition: Int,
    val done: (JsonNode) -> Unit
) {
    private fun getMenuItemText(gameData: GameData, menuItem: MenuItem): String {
        val nextScene = gameData.scenes.filter { it.sceneId == menuItem.nextScene }[0]
        return "${menuItem.buttonText}->${nextScene.sceneId}:${nextScene.name}"
    }

    fun init() {
        activity.setContentView(R.layout.editor_menu)
        val menuItems = gamePreviousElement<List<MenuItem>, MenuOptions>(
            gameData,
            scenePosition
        ) { it?.menuItems }
        val recyclerView = activity.findViewById<RecyclerView>(R.id.existing_menu_selection)
        recyclerView.adapter =
            MenuSelectorAdapter(menuItems?.map { getMenuItemText(gameData, it) } ?: emptyList()) {
                //TODO
            }
        recyclerView.layoutManager = LinearLayoutManager(activity)
        activity.findViewById<Button>(R.id.add_menu_button).setOnClickListener {
            //TODO
        }


    }

}