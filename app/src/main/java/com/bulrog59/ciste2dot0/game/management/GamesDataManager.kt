package com.bulrog59.ciste2dot0.game.management

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.game.management.GameDataWriter.Companion.makeSizeString
import com.bulrog59.ciste2dot0.game.management.GameUtil.Companion.mapper
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.bulrog59.ciste2dot0.gamedata.SceneType
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.lang.IllegalArgumentException
import java.nio.file.Files
import java.util.*

class GamesDataManager(val context: Context) {
    private val folderGame = context.filesDir.absolutePath + FOLDER_FOR_GAME_DATA
    private val storage = Firebase.storage
    private val GAMEDATA_FILE = "game.json"


    init {
        createFolderIfNotExists(folderGame)
        mapper.registerModule(KotlinModule())
    }

    private fun readLocalGame(gameLocation: String): GameData {
        return mapper.readValue(
            File(gameLocation),
            GameData::class.java
        )
    }

    fun loadLocalGames() :MutableList<GameMetaData>{
        val gamesMetaData:MutableList<GameMetaData> = mutableListOf()

        File(folderGame).listFiles()?.forEach { file ->
            if (file.isDirectory) {
                try {
                    val gameData = readLocalGame("${file.canonicalPath}/$GAMEDATA_FILE")
                    gameData.gameMetaData?.apply {
                        gamesMetaData.add(this)
                    }

                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "got following error when reading game data:${e.message}, please report this to the developer",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
        }
        return gamesMetaData
    }


    fun gameIsAvailable(gameID: UUID): Boolean {
        val folderForGameData = File(folderGame + gameID)
        return folderForGameData.exists() && folderForGameData.isDirectory
    }

    private fun createFolderIfNotExists(folderName: String) {
        if (!File(folderName).exists()) {
            Files.createDirectory(File(folderName).toPath())
        }
    }


    fun shareGame(
        gameMetaData: GameMetaData,
        callOnProgress: (transferBytes: Long, totalBytes: Long) -> Unit,
        callOnFailure: (e: Exception) -> Unit,
        onSuccessAction: (Long) -> Unit
    ) {
        val zipFileName = "$folderGame${gameMetaData.id}.zip"
        val zipSize = ZipUtils.zipAll("$folderGame${gameMetaData.id}", zipFileName)
        if (zipSize > MAX_SIZE_IN_MB * 1e6) {
            callOnFailure(
                IllegalArgumentException(
                    makeSizeString(
                        context,
                        R.string.game_too_big,
                        zipSize
                    )
                )
            )
        } else {
            storage.getReferenceFromUrl("${URL_FIRESTORE}/${gameMetaData.userId}/${gameMetaData.id}.zip")
                .putFile(Uri.fromFile(File(zipFileName)))
                .addOnProgressListener { callOnProgress(it.bytesTransferred, it.totalByteCount) }
                .addOnFailureListener {
                    callOnFailure(it)
                }
                .addOnSuccessListener {
                    File(zipFileName).delete()
                    onSuccessAction(zipSize)
                }
        }

    }

    fun loadGame(
        id: UUID?,
        userID: String?,
        callOnProgress: (transferBytes: Long, totalBytes: Long) -> Unit,
        callOnFailure: (e: Exception) -> Unit,
        onSuccessAction: () -> Unit
    ) {
        if (id == null || userID == null || gameIsAvailable(id)) {
            return
        }

        loadFileFireStore(id, userID, callOnProgress, callOnFailure, onSuccessAction)

    }

    fun createGame(gameData: GameData) {
        gameData.gameMetaData?.apply {
            val gameLocation = "$folderGame${this.id}"
            createFolderIfNotExists(gameLocation)
            mapper.writeValue(File("$gameLocation/$GAMEDATA_FILE"), gameData)
        }


    }

    private fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) {
            fileOrDirectory.listFiles()?.apply {
                for (child in this) deleteRecursive(
                    child
                )
            }

        }
        fileOrDirectory.delete()
    }

    fun eraseLocalGame(id: UUID?) {
        if (id == null || !gameIsAvailable(id)) {
            return
        }

        deleteRecursive(File("$folderGame$id"))

    }

    private fun loadFileFireStore(
        id: UUID,
        userID: String,
        callOnProgress: (transferBytes: Long, totalBytes: Long) -> Unit,
        callOnFailure: (e: Exception) -> Unit,
        callOnSuccess: () -> Unit
    ) {
        val localZipFile = File("$folderGame$id.zip")
        val referenceData =
            storage.getReferenceFromUrl("$URL_FIRESTORE$userID/$id.zip")
        referenceData.getFile(localZipFile)
            .addOnSuccessListener {
                ZipUtils.unzip(localZipFile, "$folderGame$id")
                localZipFile.delete()
                callOnSuccess()
            }
            .addOnProgressListener {
                callOnProgress(it.bytesTransferred, it.totalByteCount)
            }
            .addOnFailureListener {
                callOnFailure(it)
            }
    }

    fun verifyGame(id: UUID?): String? {
        id?.apply {
            val gameData = readLocalGame("$folderGame$id/$GAMEDATA_FILE")
            val notConfiguredScenes = gameData.scenes
                .filter { s -> s.options.isEmpty }
                .filter { s -> s.sceneType != SceneType.debug && s.sceneType != SceneType.exit }
                .map { s -> s.name }
                .joinToString(",")
            if (notConfiguredScenes.isNotEmpty()){
                val errorNotConfigScene = context.getText(R.string.not_cfg_scene)
                return "$errorNotConfigScene$notConfiguredScenes"
            }
        }

        return null


    }

    companion object {
        const val FOLDER_FOR_GAME_DATA = "/gameData/"
        const val MAX_SIZE_IN_MB = 100
        private const val URL_FIRESTORE =
            "https://firebasestorage.googleapis.com/v0/b/cistes2dot0.appspot.com/o/"
    }
}