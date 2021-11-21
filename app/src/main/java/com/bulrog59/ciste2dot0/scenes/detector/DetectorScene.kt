package com.bulrog59.ciste2dot0.scenes.detector

import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.bulrog59.ciste2dot0.CisteActivity
import com.bulrog59.ciste2dot0.camera.util.CameraManager
import com.bulrog59.ciste2dot0.scenes.Scene

class DetectorScene(private val detectorOption: DetectorOption, private val cisteActivity: CisteActivity) : Scene,CameraManager(cisteActivity) {

    override fun initPicAnalyzer():ImageAnalysis.Analyzer {
        return PictureDetector(detectorOption, cisteActivity, this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun setup() {
        super.init()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    override fun shutdown() {
        super.stopCamera()
    }

}