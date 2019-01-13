package com.github.grishberg.tests.common

import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException

/**
 * Abstraction of filesystem.
 */
interface BuildFileSystem {
    @Throws(IOException::class)
    fun cleanFolder(dir: File)
}

class BuildFileSystemImpl : BuildFileSystem {
    @Throws(IOException::class)
    override fun cleanFolder(dir: File) {
        FileUtils.deleteQuietly(dir)
        if (!dir.mkdirs()) {
            throw IOException("Cant create folder " + dir.absolutePath)
        }
    }
}