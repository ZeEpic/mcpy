package com.rimlang.rim.translation

import com.rimlang.rim.asResource
import com.rimlang.rim.lexer.NumberToken
import com.rimlang.rim.lexer.StringToken
import com.rimlang.rim.lexer.TokenType
import com.rimlang.rim.syntax.*
import com.rimlang.rim.util.title
import org.eclipse.jdt.core.JavaCore
import org.eclipse.jdt.core.ToolFactory
import org.eclipse.jdt.core.formatter.CodeFormatter
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants
import org.eclipse.jface.text.Document


private fun Int.toStringWithoutZero()
    = if (this == 0) ""
      else this.toString()

object Translator {

    private val imports = mutableListOf<String>()
    private val events = mutableListOf<String>()
    private val methods = mutableListOf<String>()
    private val fields = mutableListOf<String>()
    private var onEnable = ""
    private val traits = mutableMapOf<String, MutableMap<String, String>>() // <class, <trait_name, type>>

    private val eventMap = HashMap<String, String>()

    init {
        val eventMapString = "event_map.txt".asResource()

        // Create event map
        for (line in eventMapString.split("\n")) {
            val split = line.split(": ")
            if (split.size < 2) continue
            eventMap[split[1].trim()] = split[0]
        }
        println(eventMap)
    }

    fun translateGlobalScope(nodes: List<SyntaxNode>) {
        val eventCount = HashMap<String, Int>()
        var hasTimer = false
        for (node in nodes) {
            when (node) {
                is ComplexFunctionCallSyntaxNode -> {
                    when (node.functionIdentifier.value) {
                        "on" -> {
                            val event = node.args[0].tokens
                                .filterIsInstance<StringToken>()
                                .joinToString("") { it.value }
                            if (event == "server.start") {
                                onEnable += translate(node.body)
                            } else {
                                val spigotEvent = eventMap[event] ?: ""
                                require(spigotEvent.isNotEmpty()) {
                                    "Unknown event: $event (at line ${node.args[0].tokens[0].line})."
                                }
                                eventCount[event] = if (event !in eventCount) 0
                                                    else eventCount[event]!! + 1
                                val count = eventCount[event]!!.toStringWithoutZero()
                                events += "@EventHandler"
                                events += "public void on$spigotEvent$count($spigotEvent event) {"
                                events += translate(node.body)
                                events += "}"
                            }
                        }
                        "trait" -> {
                            val traitType = node.args[0].tokens[0] as StringToken
                            val traitName = traitType.value
                            if (traitName !in traits) {
                                traits[traitName] = mutableMapOf()
                            }
                            node.body.filterIsInstance<VariableDefinitionSyntaxNode>()
                                .forEach {
                                    traits[traitName]!![it.identifier.value] = it.initialValue.resultType
                                }
                        }
                        "timer" -> {
                            if (!hasTimer) {
                                hasTimer = true
                                methods += "    private void beginTimer(double seconds, Runnable runnable) {"
                                methods += "        Bukkit.getScheduler().runTaskTimer(this, runnable, 0, (long) (20 * seconds));"
                                methods += "    }"
                            }
                            val secondsToken = node.args[0].tokens[0]
                            require(secondsToken is NumberToken) {
                                "Timer argument must be a number (at line ${secondsToken.line})."
                            }
                            onEnable += "beginTimer(${secondsToken.value}, () -> {\n"
                            onEnable += translate(node.body)
                            onEnable += "\n});\n"
                        }
                    }
                }
                is CommandDefinitionSyntaxNode -> {
                    // TODO: Implement command arguments
                    onEnable += "getCommand(\"${camelCase(node.identifier.value)}\""
                    onEnable += ").setExecutor((sender, command, label, args) -> {\n"
                    onEnable += translate(node.body)
                    onEnable += "});\n"
                }
                is FunctionDefinitionSyntaxNode -> {
                    methods += "private ${toJavaType(node.returnType)} ${camelCase(node.identifier.value)}(${node.args.translate()}) {"
                    methods += translate(node.body)
                    methods += "}\n"
                }
                is VariableDefinitionSyntaxNode -> {
                    fields += "private var ${camelCase(node.identifier.value)} = ${node.initialValue.translate()};"
                }
                else -> {
                    throw IllegalStateException("Node type " + node::class.simpleName + " is not allowed in global scope.")
                }
            }
        }
    }

    fun toJavaType(typeToken: StringToken): String {
        val type = typeToken.value
        if (type.startsWith("list[") && type.endsWith("]")) {
            return "List<" + toJavaType(
                StringToken(
                    TokenType.TYPE,
                    type.substring(5, type.length - 1),
                    typeToken.line,
                    typeToken.character
                )
            ) + ">"
        }
        when (type) {
            "str" -> return "String"
            "num" -> return "double"
            "bool" -> return "boolean"
            "void" -> return "void"
            "player" -> return "Player"
        }
        throw ClassNotFoundException("Type " + type + " not found (on line " + typeToken.line + ").")
    }

    fun camelCase(value: String): String {
        val pascalCase = pascalCase(value)
        return pascalCase[0].lowercase() + pascalCase.drop(1)
    }

    private fun pascalCase(value: String)
        = value.split("_").joinToString("") { title(it) }

    private fun translate(nodes: List<SyntaxNode>): String {
        var builder = ""
        for (node in nodes) {
            when (node) {
                is IfSyntaxNode -> {
                    node.branches.forEach {
                        val value = it.type.value.takeUnless { v -> v == "elif" } ?: "else if"
                        builder += "$value (${it.booleanExpression.translate()}) {"
                        builder += translate(it.body)
                        builder += "} "
                    }
                }
                is ExpressionSyntaxNode -> {
                }
                is ForeachSyntaxNode -> {
                    builder += "for (${node.genericExpression.resultType} ${pascalCase(node.loopIdentifier.value)} : ${node.genericExpression.translate()}) {\n"
                    builder += translate(node.body)
                    builder += "}\n"
                }
                is WhileSyntaxNode -> {
                }
                is ReturnSyntaxNode -> {
                }
                is VariableDefinitionSyntaxNode -> {
                }
            }
        }
        return builder
    }

    fun complete(pluginName: String): String {
        val template = "Template.java".asResource()
        val complete = template.format(
            imports.joinToString(";\nimport "),
            pluginName,
            fields.joinToString("\n"),
            onEnable,
            events.joinToString("\n"),
            methods.joinToString("\n")
        )
        val options = DefaultCodeFormatterConstants.getEclipseDefaultSettings()

        options[JavaCore.COMPILER_COMPLIANCE] = JavaCore.VERSION_1_5
        options[JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM] = JavaCore.VERSION_1_5
        options[JavaCore.COMPILER_SOURCE] = JavaCore.VERSION_1_5

        // Can use: DefaultCodeFormatterConstants.SOMETHING
        val formatter = ToolFactory.createCodeFormatter(options)
        val edits = formatter.format(CodeFormatter.K_COMPILATION_UNIT, complete, 0, complete.length, 0, "\n")
        val document = Document(complete)
        edits.apply(document)
        return document.get()
    }
}