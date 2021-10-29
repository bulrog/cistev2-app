package com.bulrog59.ciste2dot0.editor

import android.app.Activity
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.scenes.menu.MenuItem
import com.bulrog59.ciste2dot0.scenes.menu.MenuOptions
import com.bulrog59.ciste2dot0.scenes.update_inventory.UpdateInventoryOptions
import com.fasterxml.jackson.databind.JsonNode

class UpdateInventoryEditor(
    val activity: Activity,
    val gameData: GameData,
    scenePosition: Int,
    val done: (JsonNode) -> Unit
) {

    private var updateInventoryOptions =
        GameOptionHelper.gamePreviousElement<UpdateInventoryOptions, UpdateInventoryOptions>(
            gameData,
            scenePosition
        ) { it }


    fun init() {
        activity.setContentView(R.layout.editor_update_inventory)

    }
}