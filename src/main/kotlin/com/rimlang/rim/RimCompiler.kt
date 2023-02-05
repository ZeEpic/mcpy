package com.rimlang.rim

import com.rimlang.rim.errors.CodeFile
import com.rimlang.rim.lexer.Lexer
import com.rimlang.rim.syntax.SyntaxAnalyzer
import com.rimlang.rim.translation.JavaTranslator
import org.bukkit.plugin.java.JavaPlugin
import java.io.FileWriter

class RimCompiler : JavaPlugin() {

    override fun onEnable() {
        // Compiler phases
        val lexer = Lexer()
        val analyzer = SyntaxAnalyzer()
        val translator = JavaTranslator()

        // Compile
        val code = CodeFile("code.rim".asResource(), "code.rim")
        val tokens = lexer.lex(code)
        val nodes = analyzer.analyze(tokens, code)
        translator.translateGlobalScope(nodes)

        // Output result
        val output = FileWriter("ExamplePlugin.java", false)
        output.write(translator.complete("ExamplePlugin"))
        output.close()
    }

}

fun String.asResource() = object {}.javaClass.getResource("/$this")!!.readText()