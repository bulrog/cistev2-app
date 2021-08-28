package com.bulrog59.ciste2dot0.game.management

import android.content.Context
import java.io.File
import java.util.*

class GameDataLoader(private val context: Context) {
    fun gameIsAvailable(gameID: UUID): Boolean {
        val folderForGameData = File(context.filesDir.absolutePath + FOLDER_FOR_GAME_DATA + gameID)
        return folderForGameData.exists() && folderForGameData.isDirectory
    }

    companion object {
        val FOLDER_FOR_GAME_DATA = "/gameData/"
    }
}