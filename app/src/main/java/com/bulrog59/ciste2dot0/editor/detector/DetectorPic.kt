package com.bulrog59.ciste2dot0.editor.detector

import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageAnalysis
import com.bulrog59.ciste2dot0.camera.util.CameraManager

class DetectorPic( activity: AppCompatActivity,
                val fileName:String,
                val done:()->Unit):CameraManager(activity) {
    override fun initPicAnalyzer(): ImageAnalysis.Analyzer {
        return PictureTaker(activity,fileName ,this,done)
    }
}