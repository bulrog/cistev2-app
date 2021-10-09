package com.bulrog59.ciste2dot0.editor

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat.startActivityForResult
import com.bulrog59.ciste2dot0.R


class PicMusicEditor(val activity: Activity) : FilePicker {
    val filePickerType=FilePickerType.image
    var fileContent:ByteArray?=null


    override fun callBack(uri: Uri?, requestCode: Int) {

        if (uri != null) {

            getFileName(uri)?.apply { activity.findViewById<TextView>(R.id.selected_file_name).text=getFileName(uri) }

            val ios=activity.getContentResolver().openInputStream(uri)
            if (ios!=null){
                fileContent = ByteArray(ios.available())

            }
        }
    }

    private fun getFileName(uri: Uri): String? {
        var fileName: String? = null
        val cursor: Cursor? = activity.contentResolver.query(uri, null, null, null, null)
        cursor?.apply {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                fileName =
                    cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))

            }
            cursor.close()
        }
        return fileName
    }

    private fun selectFile() {

        var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        chooseFile.type = "${filePickerType.name}/*"
        chooseFile = Intent.createChooser(chooseFile, "Choose a file")
        startActivityForResult(activity, chooseFile, filePickerType.code, null)
    }



    fun createScene() {
        activity.setContentView(R.layout.editor_file_picker)
        activity.findViewById<TextView>(R.id.upload_file_title).text =
            activity.getText(R.string.select_picture_text_title)
        activity.findViewById<Button>(R.id.select_file_button).setOnClickListener {
            selectFile()
        }

    }

}