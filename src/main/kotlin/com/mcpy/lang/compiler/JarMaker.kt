package com.mcpy.lang.compiler

import java.io.File

class JarMaker : CompilerPhase, Runnable {

    override fun run() {
        val tool = JarTool()
        tool.startManifest()
        val target = tool.openJar("plugin-build/ExamplePlugin.jar")
        tool.addFile(
            target,
            System.getProperty("user.dir") + "\\me\\zeepic\\example\\",
            System.getProperty("user.dir") + "\\me\\zeepic\\example\\ExamplePlugin.class"
        )
        //        tool.addFile(target, System.getProperty("user.dir"), System.getProperty("user.dir") + "src\\main\\resources\\plugin.yml");
        target.close()
        val success = File(System.getProperty("user.dir") + "\\me\\zeepic\\example\\ExamplePlugin.class").delete()
        if (!success) {
            println("Could not delete ExamplePlugin.class")
        }
    }
}
