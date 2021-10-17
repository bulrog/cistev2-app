package com.bulrog59.ciste2dot0.editor

import android.app.Activity
import android.net.Uri
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.gamedata.SceneData
import com.bulrog59.ciste2dot0.scenes.pic.PicMusicOption
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule


class PicMusicEditor(val activity: Activity, val gameData: GameData, val scenePosition: Int, val done:(JsonNode)-> Unit) :
    CallBackActivityResult {
    val om = ObjectMapper().apply { registerModule(KotlinModule()) }
    val filePicker = FilePicker(activity)
    var picName: String? = null
    var audioName: String? = null
    var nextScene: Int? = null


    override fun callBack(uri: Uri?, requestCode: Int) {
        filePicker.callBack(uri, requestCode)
    }

    private fun getSceneOptions(): PicMusicOption? {
        val options = gameData.scenes[scenePosition].options
        return if (options.isEmpty) null else om.treeToValue<PicMusicOption>(options)
    }

    private fun <T> gamePreviousElement(getterFunction: (PicMusicOption?) -> T?): T? {
        return getterFunction(getSceneOptions())
    }

    private fun savePicMusic(){

        done(om.readTree(om.writeValueAsString(PicMusicOption(
            imageName = picName!!,
            musicName = audioName!!,
            loopMusic = activity.findViewById<Switch>(R.id.loop_music_switch).isChecked,
            nextScene = nextScene!!,
            optionalText = activity.findViewById<TextView>(R.id.pic_optional_text).text.toString()
        ))))
    }

    private fun getLastOptions() {
        activity.setContentView(R.layout.editor_pic_options)
        gamePreviousElement { it?.optionalText }?.apply {
            activity.findViewById<TextView>(R.id.pic_optional_text).text = this
        }
        gamePreviousElement { it?.loopMusic }?.apply {
            activity.findViewById<Switch>(R.id.loop_music_switch).isChecked = this
        }
        activity.findViewById<Button>(R.id.pic_music_save_button).setOnClickListener {
            savePicMusic()
        }

    }

    private fun getNextScene() {
        val otherScenes = mutableListOf<SceneData>().apply {
            addAll(gameData.scenes)
            removeAt(scenePosition)
        }
        ItemPicker(activity).init(
            R.string.next_scene_title,
            otherScenes.map { "${it.sceneId}:${it.name}" }) { p ->
            nextScene = p
            getLastOptions()
        }
    }

    private fun getAudio() {
        filePicker.init(
            R.string.select_audio_text_title,
            FilePickerType.audio,
            gamePreviousElement { it?.musicName }
        ) { p ->
            audioName = p
            getNextScene()
        }
    }

    private fun getPic() {
        filePicker.init(
            R.string.select_picture_text_title,
            FilePickerType.image,
            gamePreviousElement { it?.imageName }
        ) { p ->
            picName = p
            getAudio()
        }
    }

    fun createScene() {

        getPic()
    }

}