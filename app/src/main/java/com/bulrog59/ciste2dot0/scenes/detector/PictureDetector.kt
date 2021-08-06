package com.bulrog59.ciste2dot0.scenes.detector

import android.annotation.SuppressLint
import android.os.Environment
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.bulrog59.ciste2dot0.CisteActivity
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgcodecs.Imgcodecs
import java.util.*

class PictureDetector(
    private val detectorOption: DetectorOption,
    private val cisteActivity: CisteActivity,
    private val detectorScene: DetectorScene
) : ImageAnalysis.Analyzer {
    private var capture: Boolean = false
    private val featureMatching = FeatureMatching(detectorOption, cisteActivity)


    //TODO: review how to remove this lint:
    @SuppressLint("UnsafeOptInUsageError")
    fun getPicture(imageProxy: ImageProxy): Mat {
        //TODO: review how to manage if format is not ok:
        //Here the format is ImageFormat.YUV_420_888 (format 0x23=35) which is a "Multi-plane Android YUV 420 format"
        //when we take image capture then we get a JPEG (0x100=256)
        //so we need to convert the buffer to jpeg
        val jpg2 = ConvertPicture().NV21toJPEG(
            ConvertPicture().YUV420toNV21(imageProxy.image!!),
            imageProxy.getWidth(),
            imageProxy.getHeight(),
            100
        )
        val img = Imgcodecs.imdecode(MatOfByte(*jpg2), Imgcodecs.IMREAD_UNCHANGED)
        val rotatedImg = Mat()
        Core.rotate(img, rotatedImg, Core.ROTATE_90_CLOCKWISE)
        return rotatedImg
    }

    fun takePhoto() {
        if (detectorOption.allow_capture)
            capture = true
    }


    override fun analyze(imageProxy: ImageProxy) {

        val img = getPicture(imageProxy)
        val result = featureMatching.featureMatching(img)
        if (capture) {
            capture = false
            val fileName = UUID.randomUUID().toString()
            //TODO: review as deprecated:
            val dir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/"

            Imgcodecs.imwrite("$dir$fileName.jpg", img)
            val out = Mat()
            val kpSize = featureMatching.giveImageKpAmount(img, out)

            Imgcodecs.imwrite(
                "$dir$fileName"+"_"+"$kpSize.jpg",
                out
            )

            Imgcodecs.imwrite("$dir$fileName"+"_equalize.jpg",featureMatching.equalizeImage(img))
        }

        if (result > 0) {
            cisteActivity.runOnUiThread {
                detectorScene.stopAndSetScene(result)
            }

        }

        imageProxy.close()
    }
}