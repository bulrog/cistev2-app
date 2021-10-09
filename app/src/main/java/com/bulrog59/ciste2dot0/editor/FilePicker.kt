package com.bulrog59.ciste2dot0.editor

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.ResourceFinder
import org.apache.commons.io.IOUtils

class FilePicker(val activity: Activity) : CallBackActivityResult {

    var doneCallBack: (fileName: String) -> Unit = {}
    var fileContent: ByteArray? = null
    var fileUri: Uri? = null
    val resourceFinder = ResourceFinder(activity)


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

    override fun callBack(uri: Uri?, requestCode: Int) {

        if (uri != null) {
            fileUri = uri
            getFileName(uri)?.apply {
                activity.findViewById<TextView>(R.id.selected_file_name).text = getFileName(uri)
            }
        }
    }

    private fun selectFile(filePickerType: FilePickerType) {

        var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        chooseFile.type = "${filePickerType.name}/*"
        //TODO: revoir ou le titre s'affiche
        chooseFile = Intent.createChooser(chooseFile, "Choose a file")
        ActivityCompat.startActivityForResult(activity, chooseFile, filePickerType.code, null)
    }

    fun nextButton() {
        fileUri?.apply {
            val ios = activity.contentResolver.openInputStream(this)
            val fileName=getFileName(this)
            fileName?.apply{
                ios?.use {
                    IOUtils.copy(ios,resourceFinder.getOutputStreamForFile(this))
                    doneCallBack(this)
                    return

                }
            }
            Toast.makeText(activity,R.string.issue_to_copy_file,Toast.LENGTH_LONG)

        }


        Toast.makeText(activity, R.string.file_not_selected, Toast.LENGTH_LONG).show()

}


fun init(
    titleText: Int,
    filePickerType: FilePickerType,
    doneCallBack: (fileName: String) -> Unit
) {
    this.doneCallBack = doneCallBack
    activity.setContentView(R.layout.editor_file_picker)
    activity.findViewById<TextView>(R.id.upload_file_title).setText(titleText)
    //TODO: add also a button to select an existing image
    activity.findViewById<Button>(R.id.select_file_button).setOnClickListener {
        selectFile(filePickerType)
    }
    activity.findViewById<Button>(R.id.select_file_next_button).setOnClickListener {
        nextButton()

    }
}
}