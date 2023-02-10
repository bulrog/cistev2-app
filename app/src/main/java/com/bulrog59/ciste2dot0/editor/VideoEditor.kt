package com.bulrog59.ciste2dot0.editor

import android.app.Activity
import android.net.Uri
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.editor.utils.CallBackActivityResult
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper.Companion.convertToJsonNode
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper.Companion.gamePreviousElement
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper.Companion.getItemPickerNextScene
import com.bulrog59.ciste2dot0.editor.utils.FilePicker
import com.bulrog59.ciste2dot0.editor.utils.FilePickerType
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.scenes.video.VideoOption
import com.fasterxml.jackson.databind.JsonNode

class VideoEditor(
    private val activity: Activity,
    private val gameData: GameData,
    private val scenePosition: Int,
    private val done: (JsonNode) -> Unit
) : CallBackActivityResult {
    private var filePicker = FilePicker(activity)
    private var videoName = gamePreviousElement<String, VideoOption>(
        gameData,
        scenePosition
    ) { it?.videoName }

    override fun callBack(uri: Uri?, requestCode: Int) {
        filePicker.callBack(uri, requestCode)
    }

    private fun getNextScene() {
        getItemPickerNextScene<VideoOption>(
            activity,
            gameData,
            scenePosition,
            { it?.nextScene }) { p ->
            done(
                convertToJsonNode(
                    VideoOption(
                        videoName!!, p
                    )

                )
            )
        }
    }

    fun createScene() {
        filePicker.previousFileName=videoName
        filePicker.init(
            R.string.video_select_text,
            FilePickerType.video,
            gamePreviousElement<String, VideoOption>(gameData, scenePosition) { it?.videoName }
        ) { p ->
            videoName = p
            getNextScene()
        }
    }
}