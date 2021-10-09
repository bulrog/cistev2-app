package com.bulrog59.ciste2dot0

import android.app.Activity
import android.net.Uri
import com.bulrog59.ciste2dot0.editor.FilePickerType
import com.bulrog59.ciste2dot0.game.management.GamesDataManager.Companion.FOLDER_FOR_GAME_DATA
import java.io.*
import java.lang.IllegalStateException
import java.nio.file.Files

class ResourceManager(activity: Activity) {
    private val id = activity.intent.getStringExtra(GAME_ID)
    private val resources=activity.resources
    private val rootFolder: String = if (id==null) {
        StringBuilder()
            .append("android.resource://")
            .append(activity.packageName).append(File.separator)
            .append("raw")
            .append(File.separator)
            .toString()
    } else {
        "${activity.filesDir.absolutePath}/$FOLDER_FOR_GAME_DATA/$id/"
    }

    private fun findFileWithName(name:String):String {
        val matchingFiles=File(rootFolder).listFiles()?.filter { it.name.startsWith("$name.") }
        if (matchingFiles?.size!=1){
            throw IllegalStateException("cannot find a unique file in the folder$rootFolder for name:$name")
        }
        return matchingFiles.first().name
    }

    fun getUri(resourceName: String): Uri {
        if (id==null){
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
        return  if (id==null){
                resources.openRawResource(R.raw::class.java.getId(resourceName))
        }
        else{
            FileInputStream(File(getUri(resourceName).toString()))
        }
    }

    fun getOutputStreamForFile(fileName: String):OutputStream{
        return FileOutputStream(File("$rootFolder$fileName"))
    }

    fun getOutputStreamFromURI(uri:String):OutputStream?{
        return if (id==null){
            null
        }else{
            FileOutputStream(File(getUri(uri).toString()))
        }
    }

    fun listFileOfType(filePickerType: FilePickerType):List<String>{
        return File(rootFolder).listFiles().filter {
           Files.probeContentType(it.toPath()).startsWith(filePickerType.name)
        }.map { it.name }
    }

    companion object {
        val GAME_ID = "game_id"
        val GAME_RESOURCE_NAME="game"
    }


}