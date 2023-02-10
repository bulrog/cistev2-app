package com.bulrog59.ciste2dot0.scenes.detector

import org.opencv.core.Mat
import org.opencv.core.MatOfKeyPoint

data class PictureDescriptors(
    val imgObject: Mat,
    val keypointsObject: MatOfKeyPoint,
    val descriptorsObject: Mat
)
