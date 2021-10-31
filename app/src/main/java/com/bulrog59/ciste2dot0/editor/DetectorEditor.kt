package com.bulrog59.ciste2dot0.editor

import android.app.Activity
import android.net.Uri
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.editor.utils.*
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper.Companion.getSceneDescription
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.scenes.detector.DetectorOption
import com.fasterxml.jackson.databind.JsonNode
import java.util.AbstractMap

class DetectorEditor(
    private val activity: Activity,
    private val gameData: GameData,
    private val scenePosition: Int,
    private val done: (JsonNode) -> Unit
) : CallBackActivityResult {

    private val filePicker = FilePicker(activity)

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


    private fun editMenuItem(
        previousItem: Map.Entry<String, Int>?,
        done: (Map.Entry<String, Int>) -> Unit
    ) {
        filePicker.init(R.string.ref_pic, FilePickerType.image, previousItem?.key) { picName ->
            //TODO: need to compress the picture as issue when read back (app freeze when detector starts)
            GameOptionHelper.getItemPickerNextScene<DetectorOption>(
                activity,
                gameData,
                scenePosition,
                { previousItem?.value }) { nextScene ->
                done(
                    AbstractMap.SimpleEntry(picName, nextScene)
                )
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
            done(GameOptionHelper.convertToJsonNode(DetectorOption(entries.associate {
                Pair(it.key, it.value)
            }, false)))
        }.init()
    }
}