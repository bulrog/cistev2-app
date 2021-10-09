package com.bulrog59.ciste2dot0.editor

import android.net.Uri

interface FilePicker {
    fun callBack(uri: Uri?, requestCode: Int)
}