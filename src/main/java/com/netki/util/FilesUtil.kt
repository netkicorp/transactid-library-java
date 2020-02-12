package com.netki.util

import java.io.File
import java.io.FileNotFoundException

object FilesUtil {

    /**
     * Get the list of files in a directory inside the resource folder.
     *
     * @param directoryName of the directory inside the resources folder.
     * @return list of files.
     * @throws FileNotFoundException in case the directory does not exist.
     */
    fun getFilesFromDirectory(directoryName: String): List<File> {
        val loader = Thread.currentThread().contextClassLoader
        val url = loader.getResource(directoryName)
        return url?.let {
            File(it.path).listFiles()?.toList() ?: emptyList()
        } ?: throw FileNotFoundException("Directory $directoryName not found in resources folder")
    }
}
