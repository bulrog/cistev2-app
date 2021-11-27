package com.bulrog59.ciste2dot0

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bulrog59.ciste2dot0.game.management.GameDataLoader
import com.bulrog59.ciste2dot0.game.management.GameUtil.Companion.mapper
import com.bulrog59.ciste2dot0.game.management.GameUtil.Companion.retrieveOption
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
import kotlin.system.exitProcess


class CisteActivity : AppCompatActivity() {
    private var currentScene: Scene? = null
    private lateinit var gameData: GameData
    val inventory = Inventory()
    lateinit var gameDataLoader:GameDataLoader

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
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this))
        gameDataLoader=GameDataLoader(this)
        reviewPermissions()
        gameData = gameDataLoader.loadGameDataFromIntent()

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

    inline fun <reified U, T> loadScene(
        createScene: (U, CisteActivity) -> T,
        sceneData: SceneData
    ): T {
        return createScene(retrieveOption(sceneData), this)
    }

    private fun changeScene(sceneData: SceneData){
        currentScene?.apply {
            lifecycle.removeObserver(this)
            shutdown()
        }
        when (sceneData?.sceneType) {
            SceneType.video -> {
                currentScene = loadScene(::VideoScene, sceneData)
            }
            SceneType.exit -> {
                finish()
                exitProcess(0)
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
        }

        lifecycle.addObserver(currentScene!!)
    }

    fun setScene(sceneId: Int) {
        val sceneDataMatches = gameData.scenes.filter { it.sceneId == sceneId }
        if (sceneDataMatches.size > 1) {
            throw IllegalArgumentException("for scene $sceneId we have more than one match in the game data $gameData")
        }
        val sceneData = sceneDataMatches.getOrNull(0)
        if (sceneData!=null){
            changeScene(sceneData)
        } else {
            Toast.makeText(this,R.string.scene_not_exist,Toast.LENGTH_LONG).show()
        }

    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}