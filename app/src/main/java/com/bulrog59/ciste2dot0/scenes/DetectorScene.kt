package com.bulrog59.ciste2dot0.scenes

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.bulrog59.ciste2dot0.CisteActivity
import com.bulrog59.ciste2dot0.R
import java.util.concurrent.ExecutorService

class DetectorScene(val detectorOption: DetectorOption, val cisteActivity: CisteActivity) : Scene {

    private lateinit var cameraExecutor: ExecutorService

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun setup() {
        cisteActivity.setContentView(R.layout.view_camera)

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun shutdown(){
//        cameraExecutor.shutdown()
    }

    private fun startCamera() {}



}