package com.bulrog59.ciste2dot0

import android.app.Activity
import android.net.Uri
import com.bulrog59.ciste2dot0.editor.utils.FilePickerType
import com.bulrog59.ciste2dot0.game.management.GamesDataManager.Companion.FOLDER_FOR_GAME_DATA
import java.io.*
import java.lang.IllegalStateException
import java.nio.file.Files

class ResourceManager(activity: Activity) {
    private val id = activity.intent.getStringExtra(GAME_ID)
    private val resources = activity.resources
    private val rootFolder: String = if (id == null) {
        StringBuilder()
            .append("android.resource://")
            .append(activity.packageName).append(File.separator)
            .append("raw")
            .append(File.separator)
            .toString()
    } else {
        "${activity.filesDir.absolutePath}/$FOLDER_FOR_GAME_DATA/$id/"
    }

    fun getFileWithMatchingNameWithoutExtension(name: String): List<String> {
        return File(rootFolder).listFiles()
            ?.filter { it.nameWithoutExtension == File(name).nameWithoutExtension }?.map { it.name }
            ?: emptyList()
    }

    private fun findFileWithNameWithoutExtension(name: String): String {
        val matchingFiles = getFileWithMatchingNameWithoutExtension(name)
        if (matchingFiles.size != 1) {
            throw IllegalStateException("cannot find a unique file in the folder$rootFolder for name:$name")
        }
        return matchingFiles.first()
    }

    fun getUri(resourceName: String): Uri {
        if (id == null) {
            return Uri.parse(rootFolder + resourceName)
        }
        return Uri.parse(rootFolder + findFileWithNameWithoutExtension(resourceName))
    }

    private inline fun <reified T : Class<*>> T.getId(resourceName: String): Int {
        return try {
            val idField = getDeclaredField(resourceName)
            idField.getInt(idField)
        } catch (e: Exception) {
            throw IllegalArgumentException("the picture to match with name $resourceName does not exist!")
        }

    }

    fun getStreamFromUri(resourceName: String): InputStream {
        return if (id == null) {
            resources.openRawResource(R.raw::class.java.getId(resourceName))
        } else {
            FileInputStream(File(getUri(resourceName).toString()))
        }
    }


    fun fileExists(fileName: String): Boolean {
        return File("$rootFolder$fileName").exists()
    }

    fun getLocationForFile(fileName: String):String {
        return "$rootFolder$fileName"
    }

    fun getOutputStreamForFile(fileName: String): OutputStream {
        return FileOutputStream(File(getLocationForFile(fileName)))
    }

    fun getOutputStreamFromURI(uri: String): OutputStream? {
        return if (id == null) {
            null
        } else {
            FileOutputStream(File(getUri(uri).toString()))
        }
    }

    fun deleteResource(fileName: String) {
        File("$rootFolder$fileName").delete()
    }

    fun listResourceOfType(filePickerType: FilePickerType): List<String> {

        return File(rootFolder).listFiles()?.filter {
            Files.probeContentType(it.toPath()).startsWith(filePickerType.name)
        }?.map { it.name } ?: emptyList()
    }

    companion object {
        const val GAME_ID = "game_id"
        const val GAME_RESOURCE_NAME = "game"
    }


}