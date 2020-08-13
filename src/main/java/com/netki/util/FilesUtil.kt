package com.netki.util

import java.io.File
import java.io.FileNotFoundException

object FilesUtil {

    /**
     * Get the list of files in a directory.
     *
     * @param directoryPath path of directory.
     * @return list of files.
     * @throws FileNotFoundException in case the directory does not exist.
     */
    fun getFilesFromDirectory(directoryPath: String): List<File> {
        val folder = File(directoryPath)
        return when (folder.exists()) {
            true -> folder.listFiles()?.toList() ?: emptyList()
            else -> throw FileNotFoundException("Directory $directoryPath not found")
        }
    }
}
