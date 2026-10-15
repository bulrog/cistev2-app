package com.bulrog59.ciste2dot0.game.management

import android.util.Log
import org.apache.commons.compress.archivers.zip.ZipFile
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


/**
 * UnzipUtils class extracts files and sub-directories of a standard zip file to
 * a destination directory.
 *
 */
object ZipUtils {
    /**
     * @param zipFilePath
     * @param destDirectory
     * @throws IOException
     */
    @Throws(IOException::class)
    fun unzip(zipFilePath: File, destDirectory: String) {
        val destDir = File(destDirectory)
        destDir.mkdirs()
        val canonicalDest = destDir.canonicalPath + File.separator
        // commons-compress is used instead of java.util.zip.ZipFile because Android 14+ rejects
        // entries whose name starts with "/" (published games contain such entries).
        ZipFile(zipFilePath).use { zip ->
            zip.entries.asSequence().forEach { entry ->
                val relativeName = entry.name.trimStart('/')
                if (relativeName.isEmpty()) {
                    return@forEach
                }
                val target = File(destDir, relativeName)
                if (!target.canonicalPath.startsWith(canonicalDest)) {
                    throw IOException("Invalid zip entry path: ${entry.name}")
                }
                if (entry.isDirectory) {
                    target.mkdirs()
                } else {
                    target.parentFile?.mkdirs()
                    zip.getInputStream(entry).use { input ->
                        extractFile(input, target.path)
                    }
                }
            }
        }
    }

    /**
     * Extracts a zip entry (file entry)
     * @param inputStream
     * @param destFilePath
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun extractFile(inputStream: InputStream, destFilePath: String) {
        val bos = BufferedOutputStream(FileOutputStream(destFilePath))
        val bytesIn = ByteArray(BUFFER_SIZE)
        var read: Int
        while (inputStream.read(bytesIn).also { read = it } != -1) {
            bos.write(bytesIn, 0, read)
        }
        bos.close()
    }

    fun zipAll(directory: String, zipFile: String): Long {
        val sourceFile = File(directory)

        ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use { zos ->
            zos.use {
                zipFiles(it, sourceFile, "")
            }
        }
        return File(zipFile).length()
    }

    private fun zipFiles(zipOut: ZipOutputStream, sourceFile: File, parentDirPath: String) {

        val folderContent=sourceFile.listFiles()
        if (folderContent!=null){
            val data = ByteArray(2048)
            for (f in folderContent) {

                if (f.isDirectory) {
                    val entry = ZipEntry(f.name + File.separator)
                    entry.time = f.lastModified()
                    entry.isDirectory
                    entry.size = f.length()

                    Log.i("zip", "Adding Directory: " + f.name)
                    zipOut.putNextEntry(entry)

                    //Call recursively to add files within this directory
                    zipFiles(zipOut, f, f.name)
                } else {

                    if (!f.name.contains(".zip")) { //If folder contains a file with extension ".zip", skip it
                        FileInputStream(f).use { fi ->
                            BufferedInputStream(fi).use { origin ->
                                val path = if (parentDirPath.isEmpty()) f.name else parentDirPath + File.separator + f.name
                                Log.i("zip", "Adding file: $path")
                                val entry = ZipEntry(path)
                                entry.time = f.lastModified()
                                entry.isDirectory
                                entry.size = f.length()
                                zipOut.putNextEntry(entry)
                                while (true) {
                                    val readBytes = origin.read(data)
                                    if (readBytes == -1) {
                                        break
                                    }
                                    zipOut.write(data, 0, readBytes)
                                }
                            }
                        }
                    } else {
                        zipOut.closeEntry()
                        zipOut.close()
                    }
                }
            }
        }

    }

    /**
     * Size of the buffer to read/write data
     */
    private const val BUFFER_SIZE = 4096

}