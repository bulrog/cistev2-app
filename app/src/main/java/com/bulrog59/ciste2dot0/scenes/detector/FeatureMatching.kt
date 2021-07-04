package com.bulrog59.ciste2dot0.scenes.detector

import android.os.Environment
import android.widget.TextView
import org.opencv.core.*
import org.opencv.features2d.*
import org.opencv.imgcodecs.Imgcodecs
import java.util.*
import kotlin.collections.ArrayList

class FeatureMatching {
    private lateinit var imgObject: Mat
    private var keypointsObject = MatOfKeyPoint()
    private var descriptorsObject = Mat()

    val sift = SIFT.create()

    fun setImageObject(image: Mat): Mat {
        val downloadFolder =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/"

        imgObject = image
        sift.detectAndCompute(imgObject, Mat(), keypointsObject, descriptorsObject)
        val out = Mat()
        Features2d.drawKeypoints(imgObject, keypointsObject, out)
        val fileName = downloadFolder + UUID.randomUUID().toString()
        Imgcodecs.imwrite("$fileName.KP.jpg", out)

        Imgcodecs.imwrite("$fileName.jpg", out)
        return out

    }

    fun featureMatching(
        imgScene: Mat,
        textToUpdate: TextView
    ) {
        val keypointsScene = MatOfKeyPoint()
        val descriptorsScene = Mat()
        sift.detectAndCompute(imgScene, Mat(), keypointsScene, descriptorsScene)


        if (keypointsObject.height() == 0) {
            return
        }

        val matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED)
        val knnMatches: List<MatOfDMatch> = ArrayList()
        matcher.knnMatch(descriptorsScene, descriptorsObject, knnMatches, 2)
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
        val goodMatches = MatOfDMatch()
        goodMatches.fromList(listOfGoodMatches)

        textToUpdate.setText(
            "good matches:" + listOfGoodMatches.size + " on " + keypointsObject.height() + " features which means:" + String.format(
                "%2f",
                (listOfGoodMatches.size.toDouble() / keypointsObject.height() * 100)
            ) + "%"
        )

    }
}