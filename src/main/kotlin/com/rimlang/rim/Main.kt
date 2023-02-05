package com.rimlang.rim

import com.rimlang.rim.lexer.Lexer
import com.rimlang.rim.syntax.analyze
import com.rimlang.rim.translation.Translator
import com.rimlang.rim.errors.CodeFile
import org.bukkit.plugin.java.JavaPlugin
import java.io.FileWriter

class Main : JavaPlugin() {

    override fun onEnable() {
        val code = CodeFile("code.rim".asResource(), "code.rim")
        val tokens = Lexer.lex(code)
        val nodes = analyze(tokens, code)
        Translator.translateGlobalScope(nodes)
        val output = FileWriter("ExamplePlugin.java", false)
        output.write(Translator.complete("ExamplePlugin"))
        output.close()
    }

}

fun String.asResource() = object {}.javaClass.getResource("/$this")!!.readText()