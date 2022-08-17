package com.rimlang.rim.compiler;

import java.io.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class JarTool {
    private final Manifest manifest = new Manifest();

    public void startManifest() {
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
    }

    public JarOutputStream openJar(String jarFile) throws IOException {
        return new JarOutputStream(new FileOutputStream(jarFile), manifest);
    }

    public void addFile(JarOutputStream target, String rootPath, String source) throws IOException {
        String remaining;
        if (rootPath.endsWith(File.separator)) {
            remaining = source.substring(rootPath.length());
        } else {
            remaining = source.substring(rootPath.length() + 1);
        }
        String name = remaining.replace("\\","/");
        JarEntry entry = new JarEntry(name);
        entry.setTime(new File(source).lastModified());
        target.putNextEntry(entry);

        BufferedInputStream in = new BufferedInputStream(new FileInputStream(source));
        byte[] buffer = new byte[1024];
        while (true) {
            int count = in.read(buffer);
            if (count == -1) {
                break;
            }
            target.write(buffer, 0, count);
        }
        target.closeEntry();
        in.close();
    }

}
