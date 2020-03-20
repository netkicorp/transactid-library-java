package com.netki.util

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.FileNotFoundException

internal class FilesUtilTest {

    @Test
    fun `Get the files from certificates resources folder`() {
        val files = FilesUtil.getFilesFromDirectory("certificates")

        assert(files.isNotEmpty())
    }

    @Test
    fun `Get files from a non existing folder`() {
        Assertions.assertThrows(FileNotFoundException::class.java) {
            FilesUtil.getFilesFromDirectory("random")
        }
    }
}
