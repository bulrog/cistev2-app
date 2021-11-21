package com.bulrog59.ciste2dot0.camera.util

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.bulrog59.ciste2dot0.R
import java.lang.IllegalStateException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

abstract class CameraManager(val activity: AppCompatActivity) {
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var picAnalyzer:ImageAnalysis.Analyzer

    abstract fun initPicAnalyzer(): ImageAnalysis.Analyzer

    private fun startCamera() {
        picAnalyzer=initPicAnalyzer()
        cameraExecutor = Executors.newSingleThreadExecutor()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(activity)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(activity.findViewById<PreviewView>(R.id.viewFinder).surfaceProvider)
                }



            val imageAnalyzer = ImageAnalysis.Builder()
                .build()
                .apply {
                    setAnalyzer(
                        cameraExecutor,
                        picAnalyzer
                    )
                }


            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()

            cameraProvider.bindToLifecycle(
                activity, cameraSelector, preview, imageAnalyzer
            )

        }, ContextCompat.getMainExecutor(activity))
    }

    fun init() {
        activity.setContentView(R.layout.camera_screen)
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        startCamera()

    }

    fun stopCamera() {
        cameraExecutor.shutdown()
        if (cameraExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
            println("camera is shutdown")
        } else {
            throw IllegalStateException("cannot shutdown the camera executor, please inform the developer")
        }

    }

}