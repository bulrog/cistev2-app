package com.bulrog59.ciste2dot0

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.gamedata.SceneType
import com.bulrog59.ciste2dot0.scenes.PicMusicOption
import com.bulrog59.ciste2dot0.scenes.PicMusicScene
import com.bulrog59.ciste2dot0.scenes.VideoOption
import com.bulrog59.ciste2dot0.scenes.VideoScene
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.treeToValue
import org.opencv.android.OpenCVLoader


class CisteActivity : AppCompatActivity() {
    private var currentScene: LifecycleObserver? = null
    private lateinit var gameData:GameData
    private val mapper=ObjectMapper()

    private fun loadGameData(){
        mapper.registerModule(KotlinModule())
        val ios= resources.openRawResource(R.raw.game)
        gameData = mapper.readValue(
            ios,
            GameData::class.java
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadGameData()
        initOpenCV()

        if (currentScene == null) {
            setScene(gameData.starting)
        }


    }

    private fun initOpenCV() {
        //TODO: manage correctly errors:
        if (OpenCVLoader.initDebug()) {
            Log.d("check", "openCV ok")
        } else {
            Log.d("check", "openCV not ok")
        }
    }

    fun setScene(sceneId: Int) {
        currentScene?.apply { lifecycle.removeObserver(this) }
        //TODO: to add if multiple matches to throw an error
        //TODO: to review how to manage error handling on activity level
        val sceneData=gameData.scenes.find { it.sceneId==sceneId }
        //TODO: review if null how to manage:
        when (sceneData!!.sceneType) {
            SceneType.video -> {
                //TODO: revioew if get null how to handle it, same for the other one (PicMusicScene):
                val options=mapper.treeToValue<VideoOption>(sceneData.options)
                currentScene = VideoScene(options!!, this)
            }
            SceneType.exit -> {
                finish()
                System.exit(0)
            }
            SceneType.picMusic -> {
                val options=mapper.treeToValue<PicMusicOption>(sceneData.options)
                currentScene = PicMusicScene(options!!, this)

            }
        }
        lifecycle.addObserver(currentScene!!)

    }
}