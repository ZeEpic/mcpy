package com.mcpy.lang

import com.mcpy.lang.errors.CodeFile
import com.mcpy.lang.lexer.Lexer
import com.mcpy.lang.syntax.SyntaxAnalyzer
import com.mcpy.lang.translation.JavaTranslator
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