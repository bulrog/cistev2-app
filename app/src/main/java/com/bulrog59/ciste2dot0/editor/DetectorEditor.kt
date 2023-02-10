package com.bulrog59.ciste2dot0.editor

import android.net.Uri
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.editor.detector.DetectorListEditor
import com.bulrog59.ciste2dot0.editor.utils.*
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.scenes.detector.DetectorOption
import com.fasterxml.jackson.databind.JsonNode

class DetectorEditor(
    private val activity: AppCompatActivity,
    private val gameData: GameData,
    private val scenePosition: Int,
    private val done: (JsonNode) -> Unit
) :CallBackActivityResult{

    private var options = GameOptionHelper.gamePreviousElement<Map<String, Int>, DetectorOption>(
        gameData,
        scenePosition
    ) { it?.pic2Scene } ?: emptyMap()

    private var detectorListEditor: DetectorListEditor?=null
    private var skipScene:Int?=null
    fun init(){

        activity.setContentView(R.layout.editor_detector_main)
        activity.findViewById<Button>(R.id.scanner_list_button).setOnClickListener {
            DetectorListEditor(activity,gameData,scenePosition,options){
                options=it
                init()
            }.start()
        }
        activity.findViewById<Button>(R.id.skip_scanner_button).setOnClickListener {
            GameOptionHelper.getItemPickerNextScene<DetectorOption>(
                activity,
                gameData,
                scenePosition,
                { it?.skipScene })
            { p ->
                skipScene = p
                init()
            }
        }

        activity.findViewById<Button>(R.id.scanner_main_exit).setOnClickListener {
            done(GameOptionHelper.convertToJsonNode(DetectorOption(options,skipScene)))
        }
    }

    override fun callBack(uri: Uri?, requestCode: Int) {
        detectorListEditor?.callBack(uri, requestCode)
    }

}