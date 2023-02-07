package com.mcpy.lang.compiler

import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.jar.Attributes
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import java.util.jar.Manifest

class JarTool {
    private val manifest = Manifest()
    fun startManifest() {
        manifest.mainAttributes[Attributes.Name.MANIFEST_VERSION] = "1.0"
    }

    fun openJar(jarFile: String): JarOutputStream {
        return JarOutputStream(FileOutputStream(jarFile), manifest)
    }

    fun addFile(target: JarOutputStream, rootPath: String, source: String) {
        var remaining = source.drop(rootPath.length)
        if (!rootPath.endsWith(File.separator)) {
            remaining = remaining.drop(1)
        }
        val name = remaining.replace("\\", "/")
        val entry = JarEntry(name)
        entry.time = File(source).lastModified()
        target.putNextEntry(entry)
        val stream = BufferedInputStream(FileInputStream(source))
        val buffer = ByteArray(1024)
        while (true) {
            val count = stream.read(buffer)
            if (count == -1) break
            target.write(buffer, 0, count)
        }
        target.closeEntry()
        stream.close()
    }
}