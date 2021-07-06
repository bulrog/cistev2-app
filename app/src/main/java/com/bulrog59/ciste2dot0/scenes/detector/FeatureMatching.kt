package com.bulrog59.ciste2dot0.scenes.detector

import android.widget.TextView
import com.bulrog59.ciste2dot0.CisteActivity
import com.bulrog59.ciste2dot0.R
import org.opencv.core.*
import org.opencv.features2d.*
import org.opencv.imgcodecs.Imgcodecs
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.util.*
import kotlin.collections.ArrayList

class FeatureMatching {
    private val pic2Scene = HashMap<PictureDescriptors, Int>()
    private val sift = SIFT.create()
    private lateinit var cisteActivity: CisteActivity
    private var updateUI = true


    inline fun <reified T : Class<*>> T.getId(resourceName: String): Int {
        return try {
            val idField = getDeclaredField(resourceName)
            idField.getInt(idField)
        } catch (e: Exception) {
            throw IllegalArgumentException("the picture to match with name $resourceName does not exist!")
        }

    }

    constructor(detectorOption: DetectorOption, cisteActivity: CisteActivity) {
        detectorOption.pic2Scene.map {
            this.cisteActivity = cisteActivity
            val ios = cisteActivity.resources.openRawResource(R.raw::class.java.getId(it.key))
            val targetArray = ByteArray(ios.available())

            ios.read(targetArray)
            ios.close()
            val img = Imgcodecs.imdecode(MatOfByte(*targetArray), Imgcodecs.IMREAD_UNCHANGED)


            val keypointsObject = MatOfKeyPoint()
            val descriptorsObject = Mat()
            sift.detectAndCompute(img, Mat(), keypointsObject, descriptorsObject)
            if (keypointsObject.height() == 0) {
                throw IllegalStateException("the picture:${it.key} does not have key points found so it cannot be used")
            }

            pic2Scene.put(PictureDescriptors(img, keypointsObject, descriptorsObject), it.value)
        }

    }


    fun giveImageWithKp(img: Mat, out: Mat): Int {
        val kp = MatOfKeyPoint()
        sift.detectAndCompute(img, Mat(), kp, Mat())
        Features2d.drawKeypoints(img, kp, out)
        return kp.height()
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
        sift.detectAndCompute(imgScene, Mat(), keypointsScene, descriptorsScene)
        val detectorReference = PictureDescriptors(imgScene, keypointsScene, descriptorsScene)
        var max = 0
        for ((d, s) in pic2Scene) {
            val actual = matchOnEntry(detectorReference, d)
            if (actual > max) {
                max = actual
            }
            if (max > 100) {
                //avoid crash of app due to shutdown of scene while other one is loading:
                updateUI = false
                return s
            }
        }
        if (updateUI) {
            cisteActivity.runOnUiThread {
                cisteActivity.findViewById<TextView>(R.id.maximumFound)
                    .setText("Matching:${max}")
            }
        }
        return -1


    }
}