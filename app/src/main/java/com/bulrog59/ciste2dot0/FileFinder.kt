package com.bulrog59.ciste2dot0

import android.net.Uri
import com.bulrog59.ciste2dot0.game.management.GameDataManager.Companion.FOLDER_FOR_GAME_DATA
import java.io.File
import java.lang.IllegalStateException
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.PathMatcher
import java.util.*

class FileFinder(packageName: String, id: String?, localFolder: File) {
    private val useResource = id == null
    private val rootFolder: String = if (useResource) {
        StringBuilder()
            .append("android.resource://")
            .append(packageName).append(File.separator)
            .append("raw")
            .append(File.separator)
            .toString()
    } else {
        "${localFolder.absolutePath}/$FOLDER_FOR_GAME_DATA/$id/"
    }

    private fun findFileWithName(name:String):String {
        val matchingFiles=File(rootFolder).listFiles().filter { it.name.startsWith("$name.") }
        if (matchingFiles.size!=1){
            throw IllegalStateException("cannot find a unique file in the folder$rootFolder for name:$name")
        }
        return matchingFiles.first().name
    }

    fun getUri(resourceName: String): Uri {
        if (useResource){
            return Uri.parse(rootFolder + resourceName);
        }
        return Uri.parse(rootFolder + findFileWithName(resourceName));
    }


}