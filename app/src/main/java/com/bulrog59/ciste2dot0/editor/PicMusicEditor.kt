package com.bulrog59.ciste2dot0.editor

import android.app.Activity
import android.net.Uri
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.editor.utils.CallBackActivityResult
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper.Companion.convertToJsonNode
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper.Companion.gamePreviousElement
import com.bulrog59.ciste2dot0.editor.utils.FilePicker
import com.bulrog59.ciste2dot0.editor.utils.FilePickerType
import com.bulrog59.ciste2dot0.editor.utils.GameOptionHelper
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.scenes.pic.PicMusicOption
import com.fasterxml.jackson.databind.JsonNode


class PicMusicEditor(
    private val activity: Activity,
    private val gameData: GameData,
    private val scenePosition: Int,
    private val done: (JsonNode) -> Unit
) :
    CallBackActivityResult {
    private val filePicker = FilePicker(activity)
    private var picName = gamePreviousElement<String, PicMusicOption>(
        gameData,
        scenePosition
    ) { it?.imageName }
    private var audioName = gamePreviousElement<String, PicMusicOption>(
        gameData,
        scenePosition
    ) { it?.musicName }
    private var nextScene: Int? = null


    override fun callBack(uri: Uri?, requestCode: Int) {
        filePicker.callBack(uri, requestCode)
    }


    private fun savePicMusic() {

        done(
            convertToJsonNode(
                PicMusicOption(
                    imageName = picName!!,
                    musicName = audioName!!,
                    loopMusic = activity.findViewById<Switch>(R.id.loop_music_switch).isChecked,
                    nextScene = nextScene!!,
                    optionalText = activity.findViewById<TextView>(R.id.pic_optional_text).text.toString()
                )
            )
        )
    }

    private fun getLastOptions() {
        activity.setContentView(R.layout.editor_pic_options)
        gamePreviousElement<String, PicMusicOption>(
            gameData,
            scenePosition
        ) { it?.optionalText }?.apply {
            activity.findViewById<TextView>(R.id.pic_optional_text).text = this
        }
        gamePreviousElement<Boolean, PicMusicOption>(
            gameData,
            scenePosition
        ) { it?.loopMusic }?.apply {
            activity.findViewById<Switch>(R.id.loop_music_switch).isChecked = this
        }
        activity.findViewById<Button>(R.id.pic_music_save_button).setOnClickListener {
            savePicMusic()
        }

    }

    private fun getNextScene() {
        GameOptionHelper.getItemPickerNextScene<PicMusicOption>(
            activity,
            gameData,
            scenePosition,
            { it?.nextScene })
        { p ->
            nextScene = p
            getLastOptions()
        }
    }

    private fun getAudio() {
        filePicker.previousFileName = audioName
        filePicker.init(
            R.string.select_audio_text_title,
            FilePickerType.audio,
            gamePreviousElement<String, PicMusicOption>(gameData, scenePosition) { it?.musicName }
        ) { p ->
            audioName = p
            getNextScene()
        }
    }

    private fun getPic() {
        filePicker.previousFileName = picName
        filePicker.init(
            R.string.select_picture_text_title,
            FilePickerType.image,
            gamePreviousElement<String, PicMusicOption>(gameData, scenePosition) { it?.imageName }
        ) { p ->
            picName = p
            getAudio()
        }
    }

    fun createScene() {

        getPic()
    }

}