package com.bulrog59.ciste2dot0.scenes.detector

import android.widget.Button
import android.widget.TextView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.bulrog59.ciste2dot0.CisteActivity
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.scenes.Scene
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class DetectorScene(val detectorOption: DetectorOption, val cisteActivity: CisteActivity) : Scene {

    private lateinit var cameraExecutor: ExecutorService

    private fun startCamera() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(cisteActivity)

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(cisteActivity.findViewById<PreviewView>(R.id.viewFinder).surfaceProvider)
                }

            val textToUpdate = cisteActivity.findViewById<TextView>(R.id.maximumFound)
            val picDetector = PictureDetector(textToUpdate, cisteActivity)
            // Set up the listener for take photo button
            cisteActivity.findViewById<Button>(R.id.camera_capture_button)
                .setOnClickListener { picDetector.takePhoto() }

            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .apply {
                    setAnalyzer(
                        cameraExecutor,
                        picDetector
                    )
                }


            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()

            cameraProvider.bindToLifecycle(
                cisteActivity, cameraSelector, preview, imageAnalyzer
            )

        }, ContextCompat.getMainExecutor(cisteActivity))
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun setup() {
        cisteActivity.setContentView(R.layout.view_camera)
        startCamera()

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun shutdown() {
        cameraExecutor.shutdown()
    }


}