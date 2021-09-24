package com.bulrog59.ciste2dot0

import android.app.Activity
import android.net.Uri
import com.bulrog59.ciste2dot0.game.management.GameDataManager.Companion.FOLDER_FOR_GAME_DATA
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.lang.IllegalStateException

class ResourceFinder(activity: Activity, id: String?, localFolder: File) {
    private val useResource = id == null
    private val resources=activity.resources
    private val rootFolder: String = if (useResource) {
        StringBuilder()
            .append("android.resource://")
            .append(activity.packageName).append(File.separator)
            .append("raw")
            .append(File.separator)
            .toString()
    } else {
        "${localFolder.absolutePath}/$FOLDER_FOR_GAME_DATA/$id/"
    }

    private fun findFileWithName(name:String):String {
        val matchingFiles=File(rootFolder).listFiles()?.filter { it.name.startsWith("$name.") }
        if (matchingFiles?.size!=1){
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

    inline fun <reified T : Class<*>> T.getId(resourceName: String): Int {
        return try {
            val idField = getDeclaredField(resourceName)
            idField.getInt(idField)
        } catch (e: Exception) {
            throw IllegalArgumentException("the picture to match with name $resourceName does not exist!")
        }

    }

    fun getStreamFromUri(resourceName: String) : InputStream {
        return  if (useResource){
                resources.openRawResource(R.raw::class.java.getId(resourceName))
        }
        else{
            FileInputStream(File(getUri(resourceName).toString()))
        }
    }


}