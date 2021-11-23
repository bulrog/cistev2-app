package com.bulrog59.ciste2dot0.editor

import android.app.Activity
import android.app.AlertDialog
import android.content.pm.ActivityInfo
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.ResourceManager
import com.bulrog59.ciste2dot0.editor.detector.DetectorPic
import com.bulrog59.ciste2dot0.editor.detector.PictureTaker.Companion.PIC_EXTENSION
import com.bulrog59.ciste2dot0.editor.utils.*
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper.Companion.getSceneDescription
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.scenes.detector.DetectorOption
import com.fasterxml.jackson.databind.JsonNode
import java.util.AbstractMap

class DetectorEditor(
    private val activity: AppCompatActivity,
    private val gameData: GameData,
    private val scenePosition: Int,
    private val done: (JsonNode) -> Unit
) : CallBackActivityResult {

    private val filePicker = FilePicker(activity)
    private val fieldValidator = FieldValidator(activity)

    private val options = GameOptionHelper.gamePreviousElement<Map<String, Int>, DetectorOption>(
        gameData,
        scenePosition
    ) { it?.pic2Scene } ?: emptyMap()

    private fun getItemText(pic2SceneEntries: List<Map.Entry<String, Int>>): List<String> {
        return pic2SceneEntries.map { "${it.key}->${getSceneDescription(gameData, it.value)}" }
    }

    override fun callBack(uri: Uri?, requestCode: Int) {
        filePicker.callBack(uri, requestCode)
    }

    private fun selectNextScene(
        picName: String,
        previousSceneEntry: Int?,
        done: (Map.Entry<String, Int>) -> Unit
    ) {
        GameOptionHelper.getItemPickerNextScene<DetectorOption>(
            activity,
            gameData,
            scenePosition,
            { previousSceneEntry }) { nextScene ->
            done(
                AbstractMap.SimpleEntry(picName, nextScene)
            )
        }
    }

    private fun proceedWithPicture(
        picName: String,
        previousNextScene: Int?,
        done: (Map.Entry<String, Int>) -> Unit
    ) {
        DetectorPic(activity, picName) {
            selectNextScene(picName, previousNextScene, done)
        }.init()

    }

    private fun editMenuItem(
        previousItem: Map.Entry<String, Int>?,
        done: (Map.Entry<String, Int>) -> Unit
    ) {
        activity.setContentView(R.layout.editor_detector)
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val keepSamePicButton = activity.findViewById<Button>(R.id.keep_same_picture)
        val previousItemTextField = activity.findViewById<TextView>(R.id.previous_file_name)

        previousItem?.apply {
            previousItemTextField.text = this.key
            keepSamePicButton.setOnClickListener {
                selectNextScene(this.key, this.value, done)
            }
        } ?: run {
            keepSamePicButton.visibility = View.INVISIBLE
        }
        activity.findViewById<Button>(R.id.replace_pic).setOnClickListener {

            if (!fieldValidator.onlyDigitsAndCharacters(R.id.detector_file_name)) {
                val picName =
                    activity.findViewById<EditText>(R.id.detector_file_name).text.toString()
                if (ResourceManager(activity).fileExists("$picName$PIC_EXTENSION")) {
                    AlertDialog.Builder(activity)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage(R.string.file_overwrite)
                        .setPositiveButton(R.string.confirmation) { _, _ ->
                            proceedWithPicture(picName, previousItem?.value, done)
                        }
                        .setNegativeButton(R.string.denial, null)
                        .show()
                } else {
                    DetectorPic(activity, picName) {
                        selectNextScene(picName, previousItem?.value, done)
                    }.init()
                }
            }
        }
    }

    fun init() {
        ListEditor(
            activity,
            options.entries.toList(),
            this::getItemText,
            this::editMenuItem
        ) { entries ->
            if (entries.map { it.key }.distinct().size != entries.size) {
                Toast.makeText(activity, R.string.duplicate_pic, Toast.LENGTH_LONG).show()
            } else {
                done(GameOptionHelper.convertToJsonNode(DetectorOption(entries.associate {
                    Pair(it.key, it.value)
                })))
            }

        }.init()
    }
}