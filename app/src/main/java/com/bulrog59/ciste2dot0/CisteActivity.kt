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
import java.lang.IllegalArgumentException


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
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this));
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
        val sceneDataMatches=gameData.scenes.filter { it.sceneId==sceneId }
        if (sceneDataMatches.size>1){
            throw IllegalArgumentException("for scene $sceneId we have more than one match in the game data $gameData")
        }
        val sceneData= sceneDataMatches.getOrNull(0)
        when (sceneData?.sceneType) {
            SceneType.video -> {
                Integer.parseInt("sjbbdhcbdc");
                //TODO: review if get null how to handle it, same for the other one (PicMusicScene):
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
            null -> {
                throw IllegalAccessException("the scene id:$sceneId does not exist in the game data:$gameData")
            }
        }
        lifecycle.addObserver(currentScene!!)

    }
}