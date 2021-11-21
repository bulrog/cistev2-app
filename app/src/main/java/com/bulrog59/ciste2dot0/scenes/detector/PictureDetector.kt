package com.bulrog59.ciste2dot0.scenes.detector

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.bulrog59.ciste2dot0.CisteActivity
import com.bulrog59.ciste2dot0.camera.util.CameraManager
import com.bulrog59.ciste2dot0.camera.util.ConvertPicture


class PictureDetector(
    detectorOption: DetectorOption,
    private val cisteActivity: CisteActivity,
    private val cameraManager: CameraManager
) : ImageAnalysis.Analyzer {
    private val featureMatching = FeatureMatching(detectorOption, cisteActivity)



    override fun analyze(imageProxy: ImageProxy) {

        val img = ConvertPicture.getPicture(imageProxy)
        val result = featureMatching.featureMatching(img)

        if (result > 0) {
            cisteActivity.runOnUiThread {
                cameraManager.stopCamera()
                cisteActivity.setScene(result)
            }

        }

        imageProxy.close()
    }

}