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
import com.bulrog59.ciste2dot0.ResourceManager
import org.apache.commons.io.IOUtils

class FilePicker(val activity: Activity) : CallBackActivityResult {

    var doneCallBack: (fileName: String) -> Unit = {}
    var fileUri: Uri? = null
    var newFile = false
    val resourceFinder = ResourceManager(activity)


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
            newFile = true
            fileUri = uri
            getFileName(uri)?.apply {
                activity.findViewById<TextView>(R.id.selected_file_name).text = getFileName(uri)
            }
        }
    }

    private fun uploadFile(filePickerType: FilePickerType) {

        var chooseFile = Intent(Intent.ACTION_GET_CONTENT)
        chooseFile.type = "${filePickerType.name}/*"
        chooseFile = Intent.createChooser(chooseFile, "Choose a file")
        ActivityCompat.startActivityForResult(activity, chooseFile, filePickerType.code, null)
    }

    private fun selectFile(filePickerType: FilePickerType) {
        newFile = false
        val files = resourceFinder.listFileOfType(filePickerType)
        if (files.isEmpty()) {
            Toast.makeText(activity, R.string.no_item_to_select, Toast.LENGTH_LONG).show()
            return
        }
        ItemPicker(activity).init(
            R.string.select_picture_text_title,
            files
        ) { p-> doneCallBack(files[p]) }
    }

    fun nextButton() {
        fileUri?.apply {
            if (newFile) {
                val ios = activity.contentResolver.openInputStream(this)
                val fileName = getFileName(this)
                fileName?.apply {
                    ios?.use {
                        IOUtils.copy(ios, resourceFinder.getOutputStreamForFile(this))
                        doneCallBack(this)
                        return

                    }
                }
                Toast.makeText(activity, R.string.issue_to_copy_file, Toast.LENGTH_LONG)
            }

        }
        val fileName=activity.findViewById<TextView>(R.id.selected_file_name).text
        if (fileName.isNullOrEmpty()){
            Toast.makeText(activity, R.string.file_not_selected, Toast.LENGTH_LONG).show()
        }
        else {
            doneCallBack(fileName.toString())
        }

    }


    fun init(
        titleText: Int,
        filePickerType: FilePickerType,
        previousItem: String?,
        doneCallBack: (fileName: String) -> Unit
    ) {
        this.doneCallBack = doneCallBack
        activity.setContentView(R.layout.editor_file_picker)
        activity.findViewById<TextView>(R.id.upload_file_title).setText(titleText)
        previousItem?.apply {
            activity.findViewById<TextView>(R.id.selected_file_name).text =
                previousItem
        }

        activity.findViewById<Button>(R.id.upload_file_button).setOnClickListener {
            uploadFile(filePickerType)
        }


        activity.findViewById<Button>(R.id.select_file_button).setOnClickListener {
            selectFile(filePickerType)
        }

        activity.findViewById<Button>(R.id.select_file_next_button).setOnClickListener {
            nextButton()

        }
    }
}