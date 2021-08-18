package com.bulrog59.ciste2dot0.scenes.inventory

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.CisteActivity
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.gamedata.Inventory
import com.bulrog59.ciste2dot0.scenes.Scene

class InventoryScene(val cisteActivity: CisteActivity, val inventory: Inventory) : Scene{


    override fun shutdown() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun setup(){
        cisteActivity.setContentView(R.layout.inventory)
        val recyclerView=cisteActivity.findViewById<RecyclerView>(R.id.inventory)
        recyclerView.adapter=InventoryAdapter(inventory)
        recyclerView.layoutManager=LinearLayoutManager(cisteActivity)
    }
}