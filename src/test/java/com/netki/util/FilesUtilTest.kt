package com.netki.util

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File
import java.io.FileNotFoundException

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class FilesUtilTest {

    @Test
    fun `Get the files from certificates resources directory`() {
        val files = FilesUtil.getFilesFromDirectory("src/test/resources/certificates")

        assert(files.isNotEmpty())
    }

    @Test
    fun `Get files from an empty directory`() {
        val path = "src/test/resources/certificates/empty"
        val file = File(path)
        file.mkdir()
        val exception = Assertions.assertThrows(FileNotFoundException::class.java) {
            FilesUtil.getFilesFromDirectory(path)
        }

        assertTrue(exception.message?.contains("empty") ?: false)
    }

    @Test
    fun `Get files from a non existing directory`() {
        val exception = Assertions.assertThrows(FileNotFoundException::class.java) {
            FilesUtil.getFilesFromDirectory("not/existing/path")
        }

        assertTrue(exception.message?.contains("not found") ?: false)
    }

    @Test
    fun `Get files from a non directory`() {
        val path = "src/test/resources/certificates/certificate_chain_netki_ca.pem"
        val file = File(path)
        file.mkdir()
        val exception = Assertions.assertThrows(FileNotFoundException::class.java) {
            FilesUtil.getFilesFromDirectory(path)
        }

        assertTrue(exception.message?.contains("is not a directory") ?: false)
    }
}
