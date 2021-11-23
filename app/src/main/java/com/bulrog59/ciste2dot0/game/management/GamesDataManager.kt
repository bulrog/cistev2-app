package com.bulrog59.ciste2dot0.game.management

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.bulrog59.ciste2dot0.R
import com.bulrog59.ciste2dot0.game.management.GameDataWriter.Companion.makeSizeString
import com.bulrog59.ciste2dot0.gamedata.GameData
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.lang.IllegalArgumentException
import java.nio.file.Files
import java.util.*

class GamesDataManager(val context: Context) {
    private val folderGame = context.filesDir.absolutePath + FOLDER_FOR_GAME_DATA
    private val storage = Firebase.storage
    private val mapper = ObjectMapper()
    private val GAMEDATA_FILE = "game.json"


    init {
        createFolderIfNotExists(folderGame)
        mapper.registerModule(KotlinModule())
    }

    fun addLocalGames(gamesMetaData: MutableList<GameMetaData>) {
        File(folderGame).listFiles()?.forEach { file ->
            if (file.isDirectory) {
                try {
                    val gameData = mapper.readValue(
                        File("${file.canonicalPath}/$GAMEDATA_FILE"),
                        GameData::class.java
                    )
                    if (!gamesMetaData.map { it.id }.contains(gameData.gameMetaData?.id)) {
                        gamesMetaData.add(gameData.gameMetaData!!)
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
        val zipSize=ZipUtils.zipAll("$folderGame${gameMetaData.id}", zipFileName)
        if (zipSize> MAX_SIZE_IN_MB*1e6){
            callOnFailure(IllegalArgumentException(makeSizeString(context,R.string.game_too_big,zipSize)))
        } else {
            storage.getReferenceFromUrl("${URL_FIRESTORE}/${gameMetaData.userId}/${gameMetaData.id}.zip")
                .putFile(Uri.fromFile(File(zipFileName)))
                .addOnProgressListener {callOnProgress(it.bytesTransferred,it.totalByteCount) }
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

    //TODO: to also add option to delete safely picture/video... and for that check if the game has the picture still in use
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

    companion object {
        const val FOLDER_FOR_GAME_DATA = "/gameData/"
        const val MAX_SIZE_IN_MB=100
        private const val URL_FIRESTORE =
            "https://firebasestorage.googleapis.com/v0/b/cistes2dot0.appspot.com/o/"
    }
}