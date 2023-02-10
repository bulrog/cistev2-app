package com.bulrog59.ciste2dot0.editor.utils

import android.app.Activity
import android.app.AlertDialog
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
import java.io.InputStream

class FilePicker(val activity: Activity) : CallBackActivityResult {

    private var doneCallBack: (fileName: String) -> Unit = {}
    private var fileUri: Uri? = null
    private var newFile = false
    private val resourceFinder = ResourceManager(activity)
    var previousFileName: String? = null


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
            val fileName = getFileName(uri)
            fileName?.apply {
                activity.findViewById<TextView>(R.id.selected_file_name).text = fileName
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
        val files = resourceFinder.listResourceOfType(filePickerType)
        if (files.isEmpty()) {
            Toast.makeText(activity, R.string.no_item_to_select, Toast.LENGTH_LONG).show()
            return
        }
        val itemPicker = ItemPicker(activity)
        previousFileName?.apply {
            itemPicker.previousSelection = files.indexOf(this)
        }
        itemPicker.init(
            R.string.select_picture_text_title,
            files
        ) { p -> doneCallBack(files[p]) }
    }

    private fun copyFile(ios: InputStream?, fileName: String) {
        ios?.use {
            IOUtils.copy(ios, resourceFinder.getOutputStreamForFile(fileName))
            doneCallBack(fileName)
        }
    }

    private fun nextButton() {
        if (fileUri != null) {
            fileUri?.apply {
                if (newFile) {
                    val fileName = getFileName(this)
                    val ios = activity.contentResolver.openInputStream(this)
                    fileName?.apply {
                        val filesMatchingWithoutExtension =
                            resourceFinder.getFileWithMatchingNameWithoutExtension(this)
                        if (filesMatchingWithoutExtension.isNotEmpty() &&
                            filesMatchingWithoutExtension.any { this != it }
                        ) {
                            Toast.makeText(
                                activity,
                                "${activity.getText(R.string.file_different_extension_error)}${
                                    filesMatchingWithoutExtension.filter { this != it }
                                        .joinToString()
                                }",
                                Toast.LENGTH_LONG
                            ).show()
                            return
                        }
                        if (resourceFinder.fileExists(this)) {
                            AlertDialog.Builder(activity)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setMessage(R.string.file_overwrite)
                                .setPositiveButton(R.string.confirmation) { _, _ ->
                                    copyFile(ios, this)
                                }
                                .setNegativeButton(R.string.denial) { _, _ -> }
                                .show()
                        } else {
                            copyFile(ios, this)
                        }

                    }

                }

            }
        } else {
            val fileName = activity.findViewById<TextView>(R.id.selected_file_name).text
            if (fileName.isNullOrEmpty()) {
                Toast.makeText(activity, R.string.file_not_selected, Toast.LENGTH_LONG).show()
            } else {
                doneCallBack(fileName.toString())
            }
        }


    }


    fun init(
        titleText: Int,
        filePickerType: FilePickerType,
        previousItem: String?,
        doneCallBack: (fileName: String) -> Unit
    ) {
        fileUri = null
        newFile = false
        previousFileName = null

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