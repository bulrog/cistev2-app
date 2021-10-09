package com.bulrog59.ciste2dot0.editor

import android.app.Activity
import android.net.Uri
import com.bulrog59.ciste2dot0.R


class PicMusicEditor(val activity: Activity) : CallBackActivityResult {
    val filePicker=FilePicker(activity)

    override fun callBack(uri: Uri?, requestCode: Int) {
        filePicker.callBack(uri,requestCode)
    }

    fun createScene() {
        filePicker.init(R.string.select_picture_text_title,FilePickerType.image) { }

    }

}