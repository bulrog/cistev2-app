package com.bulrog59.ciste2dot0.editor

import android.net.Uri

interface CallBackActivityResult {
    fun callBack(uri: Uri?, requestCode: Int)
}