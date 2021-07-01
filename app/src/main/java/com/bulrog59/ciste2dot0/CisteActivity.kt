package com.bulrog59.ciste2dot0

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.scenes.PicMusicOption
import com.bulrog59.ciste2dot0.scenes.PicMusicScene
import com.bulrog59.ciste2dot0.scenes.VideoOption
import com.bulrog59.ciste2dot0.scenes.VideoScene
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.treeToValue


class CisteActivity : AppCompatActivity() {
    private var currentScene: LifecycleObserver? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        val mapper = ObjectMapper()
        mapper.registerModule(KotlinModule())
        val ios= resources.openRawResource(R.raw.game)
        //TODO: to be continued:
        val gameData = mapper.readValue(
            ios,
            GameData::class.java
        )
        val picMusicOption=mapper.treeToValue<PicMusicOption>(gameData.scenes[0].options)


        super.onCreate(savedInstanceState)
        if (currentScene == null) {
            setScene(3)
        }


    }

    fun setScene(sceneId: Int) {
        currentScene?.apply { lifecycle.removeObserver(this) }
        when (sceneId) {
            1 -> {
                currentScene = VideoScene(VideoOption("trial", 2), this)
            }
            2 -> {
                finish()
                System.exit(0)
            }
            3 -> {
                currentScene = PicMusicScene(PicMusicOption("start_screen", "audio", true, 1), this)

            }
        }
        lifecycle.addObserver(currentScene!!)

    }
}