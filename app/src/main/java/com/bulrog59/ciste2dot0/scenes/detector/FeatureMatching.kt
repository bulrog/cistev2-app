package com.bulrog59.ciste2dot0.scenes.detector

import android.widget.ProgressBar
import android.widget.TextView
import com.bulrog59.ciste2dot0.CisteActivity
import com.bulrog59.ciste2dot0.R
import org.opencv.core.*
import org.opencv.features2d.DescriptorMatcher
import org.opencv.features2d.Features2d
import org.opencv.features2d.SIFT
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.util.*
import kotlin.collections.ArrayList


class FeatureMatching(detectorOption: DetectorOption, private val cisteActivity: CisteActivity) {
    private val pic2Scene = HashMap<PictureDescriptors, Int>()
    private val sift = SIFT.create()
    private val CAPTURE_DEFAULT_HEIGHT=640.0
    private val CAPTURE_DEFAULT_WIDTH=480.0


    init {
        detectorOption.pic2Scene.map {


            val ios = cisteActivity.gameDataLoader.getStreamFromUri(it.key)

            val targetArray = ByteArray(ios.available())

            ios.read(targetArray)
            ios.close()
            val originalImg = Imgcodecs.imdecode(MatOfByte(*targetArray), Imgcodecs.IMREAD_UNCHANGED)
            val img = Mat()

            val sz = Size(CAPTURE_DEFAULT_WIDTH, CAPTURE_DEFAULT_HEIGHT)
            Imgproc.resize( originalImg, img, sz );

            val keypointsObject = MatOfKeyPoint()
            val descriptorsObject = Mat()
            sift.detectAndCompute(equalizeImage(img), Mat(), keypointsObject, descriptorsObject)
            if (keypointsObject.height() == 0) {
                throw IllegalStateException("the picture:${it.key} does not have key points found so it cannot be used")
            }

            pic2Scene.put(PictureDescriptors(img, keypointsObject, descriptorsObject), it.value)
        }
    }



    fun giveImageKpAmount(img: Mat, out: Mat): Int {
        val kp = MatOfKeyPoint()
        sift.detectAndCompute(equalizeImage(img), Mat(), kp, Mat())
        Features2d.drawKeypoints(img, kp, out)
        return kp.height()
    }

    fun equalizeImage(img: Mat) :Mat {
         val clahe = Imgproc.createCLAHE()
        val destImage = Mat(img.height(), img.width(), CvType.CV_8UC4)
        val imgGray=Mat()
        Imgproc.cvtColor(img,imgGray,Imgproc.COLOR_BGR2GRAY)
        clahe.apply(imgGray,destImage)
        return destImage
    }

    private fun matchOnEntry(
        detectorImage: PictureDescriptors,
        detectorRef: PictureDescriptors
    ): Int {
        val matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED)
        val knnMatches: List<MatOfDMatch> = ArrayList()
        matcher.knnMatch(
            detectorImage.descriptorsObject,
            detectorRef.descriptorsObject,
            knnMatches,
            2
        )
        val ratioThresh = 0.75f
        val listOfGoodMatches: MutableList<DMatch> = ArrayList()
        for (i in knnMatches.indices) {
            if (knnMatches[i].rows() > 1) {
                val matches = knnMatches[i].toArray()
                if (matches[0].distance < ratioThresh * matches[1].distance) {
                    listOfGoodMatches.add(matches[0])
                }
            }
        }

        return listOfGoodMatches.size
    }


    fun featureMatching(
        imgScene: Mat
    ): Int {
        val keypointsScene = MatOfKeyPoint()
        val descriptorsScene = Mat()
        sift.detectAndCompute(equalizeImage(imgScene), Mat(), keypointsScene, descriptorsScene)
        val detectorReference = PictureDescriptors(imgScene, keypointsScene, descriptorsScene)
        var max = 0
        for ((d, s) in pic2Scene) {
            val actual = matchOnEntry(detectorReference, d)
            if (actual > max) {
                max = actual
            }
            if (max > 100) {
                return s
            }
        }
            cisteActivity.runOnUiThread {
                var valueProgress=max
                if (valueProgress>100){
                    valueProgress=100
                }
                cisteActivity.findViewById<ProgressBar>(R.id.detectorValue)?.progress = valueProgress
                cisteActivity.findViewById<TextView>(R.id.maximumFound)?.text="${cisteActivity.resources.getText(R.string.matching_text)}:${max}"
            }
        return -1


    }
}