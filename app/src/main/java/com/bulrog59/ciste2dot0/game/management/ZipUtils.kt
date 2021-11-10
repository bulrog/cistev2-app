package com.bulrog59.ciste2dot0.game.management

import android.util.Log
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
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
        if (!destDir.exists()) {
            destDir.mkdir()
        }
        ZipFile(zipFilePath).use { zip ->

            zip.entries().asSequence().forEach { entry ->

                zip.getInputStream(entry).use { input ->


                    val filePath = destDirectory + File.separator + entry.name

                    if (!entry.isDirectory) {
                        // if the entry is a file, extracts it
                        extractFile(input, filePath)
                    } else {
                        // if the entry is a directory, make the directory
                        val dir = File(filePath)
                        dir.mkdir()
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

    fun zipAll(directory: String, zipFile: String) {
        val sourceFile = File(directory)

        ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile))).use {
            it.use {
                zipFiles(it, sourceFile, "")
            }
        }
    }

    private fun zipFiles(zipOut: ZipOutputStream, sourceFile: File, parentDirPath: String) {

        val data = ByteArray(2048)

        for (f in sourceFile.listFiles()) {

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
                            val path = parentDirPath + File.separator + f.name
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

    /**
     * Size of the buffer to read/write data
     */
    private const val BUFFER_SIZE = 4096

}