package com.bulrog59.ciste2dot0

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.gamedata.Inventory
import com.bulrog59.ciste2dot0.gamedata.SceneData
import com.bulrog59.ciste2dot0.gamedata.SceneType
import com.bulrog59.ciste2dot0.scenes.Scene
import com.bulrog59.ciste2dot0.scenes.debug.DebugScene
import com.bulrog59.ciste2dot0.scenes.detector.DetectorScene
import com.bulrog59.ciste2dot0.scenes.update_inventory.UpdateInventoryScene
import com.bulrog59.ciste2dot0.scenes.inventory.InventoryScene
import com.bulrog59.ciste2dot0.scenes.menu.MenuScene
import com.bulrog59.ciste2dot0.scenes.pic.PicMusicScene
import com.bulrog59.ciste2dot0.scenes.rules.RulesScene
import com.bulrog59.ciste2dot0.scenes.video.VideoScene
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.treeToValue
import org.opencv.android.OpenCVLoader
import java.io.File
import java.io.FileInputStream


class CisteActivity : AppCompatActivity() {
    private var currentScene: Scene? = null
    private lateinit var gameData: GameData
    lateinit var fileFinder: FileFinder
    val mapper = ObjectMapper()
    val inventory = Inventory()
    var gameId: String?=null

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {


                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun loadGameData() {
        mapper.registerModule(KotlinModule())

        val ios = if (gameId != null) {
            FileInputStream(
                File(fileFinder.getUri("game").toString())
            )
        } else {
            resources.openRawResource(R.raw.game)
        }
        gameData = mapper.readValue(
            ios,
            GameData::class.java
        )
    }

    fun gameDataToString(): String {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(gameData)
    }

    fun inventoryToString(): String {
        return inventory.toString()
    }


    override fun onBackPressed() {
        setScene(gameData.backButtonScene)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameId= intent.getStringExtra(GAME_ID)
        fileFinder = FileFinder(this.packageName, gameId, filesDir)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this));
        reviewPermissions()
        loadGameData()
        initOpenCV()

        if (currentScene == null) {
            setScene(gameData.starting)
        }
    }

    private fun reviewPermissions() {
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun initOpenCV() {
        if (!OpenCVLoader.initDebug()) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("OpenCV is required to run this application and is not supported on this device!")
                .setCancelable(false)
                .setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                    finish()
                    System.exit(10)
                }).create().show()
        }
    }

    inline fun <reified T> retrieveOption(sceneData: SceneData): T {
        return mapper.treeToValue<T>(sceneData.options)
            ?: throw IllegalArgumentException("options is null and cannot for a video type for the scene: $sceneData")
    }

    inline fun <reified U, T> loadScene(
        createScene: (U, CisteActivity) -> T,
        sceneData: SceneData
    ): T {
        return createScene(retrieveOption(sceneData), this)
    }

    fun setScene(sceneId: Int) {
        currentScene?.apply {
            lifecycle.removeObserver(this)
            shutdown()
        }
        val sceneDataMatches = gameData.scenes.filter { it.sceneId == sceneId }
        if (sceneDataMatches.size > 1) {
            throw IllegalArgumentException("for scene $sceneId we have more than one match in the game data $gameData")
        }
        val sceneData = sceneDataMatches.getOrNull(0)
        when (sceneData?.sceneType) {
            SceneType.video -> {
                currentScene = loadScene(::VideoScene, sceneData)
            }
            SceneType.exit -> {
                finish()
                System.exit(0)
            }
            SceneType.picMusic -> {
                currentScene = loadScene(::PicMusicScene, sceneData)

            }
            SceneType.detector -> {
                currentScene = loadScene(::DetectorScene, sceneData)
            }
            SceneType.updateInventory -> {
                currentScene = loadScene(::UpdateInventoryScene, sceneData)
            }
            SceneType.ruleEngine -> {
                currentScene = loadScene(::RulesScene, sceneData)
            }
            SceneType.debug -> {
                currentScene = loadScene(::DebugScene, sceneData)
            }
            SceneType.inventory -> {
                currentScene = loadScene(::InventoryScene, sceneData)
            }
            SceneType.menu -> {
                currentScene = loadScene(::MenuScene, sceneData)
            }
            null -> {
                throw IllegalAccessException("the scene id:$sceneId does not exist in the game data:$gameData")
            }
        }

        lifecycle.addObserver(currentScene!!)
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        val GAME_ID = "game_id"
    }
}