package com.bulrog59.ciste2dot0.editor.detector

import android.graphics.Bitmap
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.ResourceManager
import com.bulrog59.ciste2dot0.camera.util.CameraManager
import com.bulrog59.ciste2dot0.camera.util.ConvertPicture
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs

class PictureTaker(
    val activity: AppCompatActivity,
    private val fileName: String,
    private val cameraManager: CameraManager,
    private val callBack: () -> Unit

) : ImageAnalysis.Analyzer {
    private var capture = false
    private val resourceManager = ResourceManager(activity)

    companion object {
        const val PIC_EXTENSION=".jpg"
    }

    init {
        activity.findViewById<TextView>(R.id.maximumFound).visibility = View.INVISIBLE
        activity.findViewById<ProgressBar>(R.id.detectorValue).visibility = View.INVISIBLE

        val button = activity.findViewById<Button>(R.id.camera_capture_button)
        button.visibility = View.VISIBLE
        button.setOnClickListener { capture = true }
    }


    override fun analyze(imageProxy: ImageProxy) {

        if (capture) {
            val img = ConvertPicture.getPicture(imageProxy)
            Imgcodecs.imwrite(resourceManager.getLocationForFile("$fileName$PIC_EXTENSION"),img)
            activity.runOnUiThread {
                cameraManager.stopCamera()
                callBack()
            }

        }

        imageProxy.close()
    }

}