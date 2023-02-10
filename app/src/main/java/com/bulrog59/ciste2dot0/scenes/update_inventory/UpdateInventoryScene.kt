package com.bulrog59.ciste2dot0.scenes.update_inventory

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.bulrog59.ciste2dot0.CisteActivity
import com.bulrog59.ciste2dot0.scenes.Scene

class UpdateInventoryScene(
    private val updateInventoryOptions: UpdateInventoryOptions,
    private val cisteActivity: CisteActivity
) :
    Scene {

    override fun shutdown() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun updateInventory() {
        updateInventoryOptions.itemsToAdd.forEach{cisteActivity.inventory.addItem(it)}
        for (i in updateInventoryOptions.itemIdsToRemove) {
            cisteActivity.inventory.removeItem(i)
        }

        cisteActivity.setScene(updateInventoryOptions.nextScene)
    }
}