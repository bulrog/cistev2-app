package com.bulrog59.ciste2dot0.scenes.get_item

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.bulrog59.ciste2dot0.CisteActivity
import com.bulrog59.ciste2dot0.scenes.Scene

class GetItemScene(
    private val getItemOptions: GetItemOptions,
    private val cisteActivity: CisteActivity
) :
    Scene {

    override fun shutdown() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun addItemToInventory() {
        cisteActivity.inventory.addItem(getItemOptions.item)
        cisteActivity.setScene(getItemOptions.nextScene)
    }
}