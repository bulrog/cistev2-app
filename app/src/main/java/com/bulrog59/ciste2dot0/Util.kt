package com.bulrog59.ciste2dot0

import android.net.Uri
import java.io.File

class Util {
    val rootFolder:String

    constructor(packageName: String) {
        this.rootFolder = StringBuilder ()
            .append ("android.resource://")
            .append (packageName).append (File.separator)
            .append ("raw")
            .append (File.separator)
            .toString()

    }


    fun getUri(resourceName:String):Uri{

            return Uri.parse (rootFolder+resourceName);


    }

}