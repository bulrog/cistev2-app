package com.bulrog59.ciste2dot0.editor

import android.app.Activity
import android.net.Uri
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.editor.GameOptionHelper.Companion.convertToJsonNode
import com.bulrog59.ciste2dot0.editor.GameOptionHelper.Companion.gamePreviousElement
import com.bulrog59.ciste2dot0.editor.GameOptionHelper.Companion.om
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.gamedata.SceneData
import com.bulrog59.ciste2dot0.scenes.video.VideoOption
import com.fasterxml.jackson.databind.JsonNode

class VideoEditor(
    val activity: Activity,
    val gameData: GameData,
    val scenePosition: Int,
    val done: (JsonNode) -> Unit
) : CallBackActivityResult {
    private var filePicker = FilePicker(activity)
    private var videoName: String? = null
    override fun callBack(uri: Uri?, requestCode: Int) {
        filePicker.callBack(uri, requestCode)
    }

    private fun getNextScene() {
        val otherScenes = mutableListOf<SceneData>().apply {
            addAll(gameData.scenes)
            removeAt(scenePosition)
        }
        ItemPicker(activity).init(
            R.string.next_scene_title,
            otherScenes.map { "${it.sceneId}:${it.name}" }) { p ->
            done(
                convertToJsonNode(
                    VideoOption(
                        videoName!!, otherScenes[p].sceneId
                    )

                )
            )
        }
    }

    fun createScene() {
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