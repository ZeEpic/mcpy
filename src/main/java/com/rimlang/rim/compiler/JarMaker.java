package com.rimlang.rim.compiler;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarOutputStream;

public class JarMaker {
    public static void create() throws IOException {
        JarTool tool = new JarTool();
        tool.startManifest();

        JarOutputStream target = tool.openJar("plugin-build/ExamplePlugin.jar");
        tool.addFile(target, System.getProperty("user.dir") + "\\me\\zeepic\\example\\", System.getProperty("user.dir") + "\\me\\zeepic\\example\\ExamplePlugin.class");
//        tool.addFile(target, System.getProperty("user.dir"), System.getProperty("user.dir") + "src\\main\\resources\\plugin.yml");
        target.close();
        boolean success = new File(System.getProperty("user.dir") + "\\me\\zeepic\\example\\ExamplePlugin.class").delete();
        if (!success) {
            System.out.println("Could not delete ExamplePlugin.class");
        }
    }
}
