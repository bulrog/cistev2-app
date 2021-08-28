package com.bulrog59.ciste2dot0.game.management

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.nio.file.Files
import java.util.*

class GameDataLoader(private val context: Context) {
    private val folderGame = context.filesDir.absolutePath + FOLDER_FOR_GAME_DATA
    private val storage = Firebase.storage


    fun gameIsAvailable(gameID: UUID): Boolean {
        val folderForGameData = File(folderGame + gameID)
        return folderForGameData.exists() && folderForGameData.isDirectory
    }

    fun loadGame(id: UUID?) {
        if (id == null || gameIsAvailable(id)) {
            return
        }
        if (!File(folderGame).exists()){
            Files.createDirectory(File(folderGame).toPath())
        }
        loadFileFireStore(id)

    }

    private fun loadFileFireStore(id: UUID) {
        val referenceData =
            storage.getReferenceFromUrl("$URL_FIRESTORE$id.zip")
        referenceData.getFile(File("$folderGame$id.zip"))
            .addOnSuccessListener {
                //TODO: unzip the file in a folder
                println("the file is downloaded, now need to unzip it")
            }
            .addOnFailureListener {
                //TODO: add toast message and put download button back
                println("something went wrong")
            }
    }

    companion object {
        private val FOLDER_FOR_GAME_DATA = "/gameData/"
        private val URL_FIRESTORE =
            "https://firebasestorage.googleapis.com/v0/b/cistes2dot0.appspot.com/o/"
    }
}