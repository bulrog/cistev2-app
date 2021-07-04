package com.bulrog59.ciste2dot0.scenes.detector

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.bulrog59.ciste2dot0.CisteActivity
import com.bulrog59.ciste2dot0.R
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgcodecs.Imgcodecs

class PictureDetector(
    detectorOption: DetectorOption,
    private val cisteActivity: CisteActivity
) : ImageAnalysis.Analyzer {
    var capture: Boolean = false
    val featureMatching = FeatureMatching(detectorOption,cisteActivity)



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
        capture = true
    }




    override fun analyze(imageProxy: ImageProxy) {


        val img = getPicture(imageProxy)
        val result=featureMatching.featureMatching(img)
        /*if (capture) {
            val objectWithKp=featureMatching.setImageObject(img)
            capture=false
            val bmp = Bitmap.createBitmap(objectWithKp.cols(), objectWithKp.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(objectWithKp, bmp);
            cisteActivity.runOnUiThread {
                cisteActivity.findViewById<ImageView>(R.id.imageMinion).setImageBitmap(bmp)
            }

        } else {
            featureMatching.featureMatching(img, cisteActivity.findViewById(R.id.maximumFound))
        }*/
        if (result>0){
            cisteActivity.setScene(result)
        }

        imageProxy.close()
    }
}