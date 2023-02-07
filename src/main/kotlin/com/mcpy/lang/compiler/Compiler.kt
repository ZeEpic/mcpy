package com.mcpy.lang.compiler

import javax.tools.DiagnosticCollector
import javax.tools.JavaFileObject
import javax.tools.ToolProvider

fun compileToByteCode(javaCode: String) {
    val compiler = ToolProvider.getSystemJavaCompiler()
    val diagnostics = DiagnosticCollector<JavaFileObject>()
    val path = System.getProperty("user.dir") + "/me/zeepic/example/Main.java"
    val file = JavaSourceFromString(path, javaCode)
    val compilationUnits = listOf<JavaFileObject>(file)
    val optionList = listOf("-classpath", "paper-api.jar")
    val task = compiler.getTask(null, null, diagnostics, optionList, null, compilationUnits)
    val success = task.call()
    for (diagnostic in diagnostics.diagnostics) {
//            println(diagnostic.getCode())
//            println(diagnostic.getKind())
//            println(diagnostic.getPosition())
//            println(diagnostic.getStartPosition())
//            println(diagnostic.getEndPosition())
        println(diagnostic.source)
        println(diagnostic.getMessage(null))
    }
    println("Success: $success")
}
