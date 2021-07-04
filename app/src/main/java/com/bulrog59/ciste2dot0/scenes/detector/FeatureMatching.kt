package com.bulrog59.ciste2dot0.scenes.detector

import android.os.Environment
import android.widget.TextView
import com.bulrog59.ciste2dot0.CisteActivity
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.Util
import org.opencv.core.*
import org.opencv.features2d.*
import org.opencv.imgcodecs.Imgcodecs
import java.util.*
import kotlin.collections.ArrayList

class FeatureMatching {
    private val pic2Scene = HashMap<DetectorReference, Int>()
    private val sift = SIFT.create()


    constructor(detectorOption: DetectorOption, cisteActivity: CisteActivity) {
        detectorOption.pic2Scene.map {
            //TODO: cannot read the image so to review the read raw resource:
            cisteActivity.resources.openRawResource(R.raw.detector)
            val img = Imgcodecs.imread(Util(cisteActivity.packageName).getUri(it.key).toString(),Imgcodecs.IMREAD_COLOR)


            val keypointsObject = MatOfKeyPoint()
            val descriptorsObject = Mat()
            sift.detectAndCompute(img, Mat(), keypointsObject, descriptorsObject)
            pic2Scene.put(DetectorReference(img, keypointsObject, descriptorsObject), it.value)
        }

    }


    /*fun setImageObject(image: Mat): Mat {
        val downloadFolder =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/"

        imgObject = image
        sift.detectAndCompute(imgObject, Mat(), keypointsObject, descriptorsObject)
        val out = Mat()
        Features2d.drawKeypoints(imgObject, keypointsObject, out)
        val fileName = downloadFolder + UUID.randomUUID().toString() + "_" + keypointsObject.size()
        Imgcodecs.imwrite("$fileName.KP.jpg", out)

        Imgcodecs.imwrite("$fileName.jpg", imgObject)
        return out

    }*/

    private fun matchOnEntry(detectorImage: DetectorReference,detectorRef: DetectorReference):Boolean{
        val matcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED)
        val knnMatches: List<MatOfDMatch> = ArrayList()
        matcher.knnMatch(detectorImage.descriptorsObject, detectorRef.descriptorsObject, knnMatches, 2)
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
        if (listOfGoodMatches.size>100){
            return true
        }

        return false


    }

    fun featureMatching(
        imgScene: Mat
    ): Int {
        val keypointsScene = MatOfKeyPoint()
        val descriptorsScene = Mat()
        sift.detectAndCompute(imgScene, Mat(), keypointsScene, descriptorsScene)
        val detectorReference=DetectorReference(imgScene,keypointsScene,descriptorsScene)
        for ((d,s) in pic2Scene){
            if (matchOnEntry(detectorReference,d)){
                return s
            }
        }
        return -1


        //TODO: to review
        /*if (keypointsObject.height() == 0) {
            return
        }*/


    }
}