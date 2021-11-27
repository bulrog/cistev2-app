package com.bulrog59.ciste2dot0.scenes.debug

import android.content.pm.ActivityInfo
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.bulrog59.ciste2dot0.CisteActivity
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.scenes.Scene

class DebugScene(private val debugOptions: DebugOptions, private val cisteActivity: CisteActivity) :
    Scene {

    override fun shutdown() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun setup() {
        cisteActivity.setContentView(R.layout.scene_debug)
        val debugText="inventory:\n${cisteActivity.inventoryToString()}\ngame data:\n${cisteActivity.gameDataToString()}"
        cisteActivity.findViewById<TextView>(R.id.debugData).apply {
            text =debugText
            movementMethod = ScrollingMovementMethod()
        }
        cisteActivity.findViewById<Button>(R.id.goToScene).setOnClickListener {

            //TODO: to try if scene is not existing that it does not change the screen
            cisteActivity.setScene(Integer.parseInt(cisteActivity.findViewById<EditText>(R.id.sceneEntry).text.toString()))
        }
        cisteActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

    }

}