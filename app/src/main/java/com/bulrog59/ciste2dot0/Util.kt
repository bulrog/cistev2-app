package com.bulrog59.ciste2dot0

import android.net.Uri
import com.bulrog59.ciste2dot0.game.management.GameDataManager.Companion.FOLDER_FOR_GAME_DATA
import java.io.File
import java.util.*

class Util(packageName: String, id: String?, localFolder: File) {
    private val rootFolder: String = if (id == null) {
        StringBuilder()
            .append("android.resource://")
            .append(packageName).append(File.separator)
            .append("raw")
            .append(File.separator)
            .toString()
    } else {
        "${localFolder.absolutePath}/$FOLDER_FOR_GAME_DATA/$id/"
    }

    fun getUri(resourceName: String): Uri {
        return Uri.parse(rootFolder + resourceName);
    }


}