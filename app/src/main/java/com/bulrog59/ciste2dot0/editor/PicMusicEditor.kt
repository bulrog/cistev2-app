package com.bulrog59.ciste2dot0.editor

import android.app.Activity
import android.net.Uri
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.scenes.pic.PicMusicOption
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule


class PicMusicEditor(val activity: Activity, val options: JsonNode) : CallBackActivityResult {
    val om=ObjectMapper().apply { registerModule(KotlinModule()) }
    val filePicker=FilePicker(activity)

    override fun callBack(uri: Uri?, requestCode: Int) {
        filePicker.callBack(uri,requestCode)
    }

    private fun getPreviousPicName():String?{
        return if (options.isEmpty) null else om.treeToValue<PicMusicOption>(options)?.imageName
    }
    private fun getPreviousAudioName():String?{
        return if (options.isEmpty) null else om.treeToValue<PicMusicOption>(options)?.musicName
    }

    fun createScene() {
        var picName:String?=null
        var audioName:String?=null

        filePicker.init(R.string.select_picture_text_title,FilePickerType.image,getPreviousPicName()) {p-> picName=p
            filePicker.init(R.string.select_audio_text_title,FilePickerType.audio,getPreviousAudioName()) {p-> audioName=p }
        }


    }

}