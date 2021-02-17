package com.netki.util

import java.io.File
import java.io.FileNotFoundException

internal object FilesUtil {

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
            true -> {
                val listFiles = folder.listFiles()?.toList() ?: throw FileNotFoundException("File $directoryPath is not a directory")
                if (listFiles.isEmpty()) {
                    throw FileNotFoundException("Directory $directoryPath empty")
                } else {
                    listFiles
                }
            }
            else -> throw FileNotFoundException("Directory $directoryPath not found")
        }
    }
}
