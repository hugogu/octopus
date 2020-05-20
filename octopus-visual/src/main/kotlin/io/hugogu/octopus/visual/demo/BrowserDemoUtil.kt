package io.hugogu.octopus.visual.demo

import java.awt.Desktop
import java.io.File
import java.io.FileWriter

object BrowserDemoUtil {
    fun openInBrowser(html: String) {
        val file = createTemporaryFile()
        FileWriter(file).use {
            it.write(html)
        }
        openInBrowser(file)
    }

    private fun openInBrowser(file: File) {
        val desktop = Desktop.getDesktop()
        desktop.browse(file.toURI())
    }

    private fun createTemporaryFile(): File {
        val rootPath = "."
        println("Project root: $rootPath")
        val tmpDir = File(rootPath, "build/tmp")
        val file = File.createTempFile("index", ".html", tmpDir)
        println(file.canonicalFile)
        return file
    }
}