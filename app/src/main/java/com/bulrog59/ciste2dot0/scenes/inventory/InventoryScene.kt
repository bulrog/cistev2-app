package com.bulrog59.ciste2dot0.scenes.inventory

import android.content.pm.ActivityInfo
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.*
import com.bulrog59.ciste2dot0.CisteActivity
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.scenes.Scene

class InventoryScene(
    private val inventoryOptions: InventoryOptions,
    private val cisteActivity: CisteActivity
) : Scene {


    override fun shutdown() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun setup() {
        cisteActivity.setContentView(R.layout.scene_inventory)
        cisteActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val recyclerView = cisteActivity.findViewById<RecyclerView>(R.id.inventory)
        recyclerView.adapter = InventoryAdapter(inventoryOptions, cisteActivity)
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }
}