package com.bulrog59.ciste2dot0.scenes.menu

import android.content.pm.ActivityInfo
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bulrog59.ciste2dot0.CisteActivity
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.scenes.Scene

class MenuScene(
    private val menuOptions: MenuOptions,
    private val cisteActivity: CisteActivity
) : Scene {
    override fun shutdown() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun setup() {
        cisteActivity.setContentView(R.layout.scene_menu)
        cisteActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        val recyclerView = cisteActivity.findViewById<RecyclerView>(R.id.menu)
        recyclerView.adapter = MenuAdapter(menuOptions, cisteActivity)
        recyclerView.layoutManager = LinearLayoutManager(cisteActivity)
    }


}