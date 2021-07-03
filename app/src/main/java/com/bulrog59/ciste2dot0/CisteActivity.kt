package com.bulrog59.ciste2dot0

import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.gamedata.SceneData
import com.bulrog59.ciste2dot0.gamedata.SceneType
import com.bulrog59.ciste2dot0.scenes.PicMusicScene
import com.bulrog59.ciste2dot0.scenes.VideoScene
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.treeToValue
import org.opencv.android.OpenCVLoader


class CisteActivity : AppCompatActivity() {
    private var currentScene: LifecycleObserver? = null
    private lateinit var gameData: GameData
    private val mapper = ObjectMapper()

    private fun loadGameData() {
        mapper.registerModule(KotlinModule())
        val ios = resources.openRawResource(R.raw.game)
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
        if (!OpenCVLoader.initDebug()) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("OpenCV is required to run this application and is not supported on this device!")
                .setCancelable(false)
                .setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->
                    finish()
                    System.exit(10)
                }).create().show()
        }
    }

    inline fun <reified T> retrieveOption(mapper: ObjectMapper, sceneData: SceneData): T {
        return mapper.treeToValue<T>(sceneData.options)
            ?: throw IllegalArgumentException("options is null and cannot for a video type for the scene: $sceneData")
    }

    inline fun <reified U, T> loadScene(
        createScene: (U, CisteActivity) -> T,
        mapper: ObjectMapper,
        sceneData: SceneData,
        cisteActivity: CisteActivity
    ): T {
        return createScene(retrieveOption(mapper, sceneData), cisteActivity)
    }

    fun setScene(sceneId: Int) {
        currentScene?.apply { lifecycle.removeObserver(this) }
        val sceneDataMatches = gameData.scenes.filter { it.sceneId == sceneId }
        if (sceneDataMatches.size > 1) {
            throw IllegalArgumentException("for scene $sceneId we have more than one match in the game data $gameData")
        }
        val sceneData = sceneDataMatches.getOrNull(0)
        when (sceneData?.sceneType) {
            SceneType.video -> {
                currentScene = loadScene(::VideoScene, mapper, sceneData, this)
            }
            SceneType.exit -> {
                finish()
                System.exit(0)
            }
            SceneType.picMusic -> {
                currentScene = loadScene(::PicMusicScene, mapper, sceneData, this)

            }
            null -> {
                throw IllegalAccessException("the scene id:$sceneId does not exist in the game data:$gameData")
            }
        }
        lifecycle.addObserver(currentScene!!)

    }
}