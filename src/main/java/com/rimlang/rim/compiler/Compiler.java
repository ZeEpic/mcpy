package com.rimlang.rim.compiler;

import javax.tools.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Compiler {
    public static void compile(String javaCode) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        String path = (System.getProperty("user.dir") + "/me/zeepic/example/Main.java");
        JavaFileObject file = new JavaSourceFromString(path, javaCode);

        Iterable<? extends JavaFileObject> compilationUnits = List.of(file);
        List<String> optionList = new ArrayList<>();
        optionList.add("-classpath");
        optionList.add("spigot-api.jar");
        JavaCompiler.CompilationTask task = compiler.getTask(null, null, diagnostics, optionList, null, compilationUnits);
        boolean success = task.call();
        for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
//            System.out.println(diagnostic.getCode());
//            System.out.println(diagnostic.getKind());
//            System.out.println(diagnostic.getPosition());
//            System.out.println(diagnostic.getStartPosition());
//            System.out.println(diagnostic.getEndPosition());
            System.out.println(diagnostic.getSource());
            System.out.println(diagnostic.getMessage(null));
        }
        System.out.println("Success: " + success);
    }
}
