package com.bulrog59.ciste2dot0.scenes.update_inventory

import com.bulrog59.ciste2dot0.gamedata.Item

data class UpdateInventoryOptions(
    val itemsToAdd: List<Item>,
    val itemIdsToRemove: List<Int>,
    val nextScene: Int
)
