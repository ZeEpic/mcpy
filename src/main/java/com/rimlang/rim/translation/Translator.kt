package com.rimlang.rim.translation

import com.rimlang.rim.asResource
import com.rimlang.rim.lexer.NumberToken
import com.rimlang.rim.lexer.StringToken
import com.rimlang.rim.lexer.TokenType
import com.rimlang.rim.syntax.*
import com.rimlang.rim.util.camelCase
import com.rimlang.rim.util.classNameFromQualifiedName
import com.rimlang.rim.util.pascalCase
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
    val traits = mutableMapOf<String, MutableMap<String, String>>() // <class, <trait_name, type>>

    private val eventMap = HashMap<String, String>()


    private val globalContext = GlobalContext()

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
        nodes.filterIsInstance<FunctionDefinitionSyntaxNode>()
            .forEach {
                val id = it.identifier.value.camelCase()
                methods += "private ${toJavaType(it.returnType)} $id(${it.args.translate(globalContext)}) {"
                val variableArgs = it.args.args.map { arg ->
                    VariableIdentifier(arg.identifier.value, arg.type.value, "", arg.identifier.line)
                }
                methods += translate(it.body, FunctionContext(id,
                    (globalContext.identifiers + variableArgs).toMutableList()
                ))
                methods += "}\n"
                globalContext.identifiers += FunctionIdentifier(id,
                    it.args
                        .args
                        .map { arg -> arg.translate().split(" ") }
                        .associate { (a, b) -> a to b },
                    it.returnType.value,
                    it.identifier.line
                )
            }
        for (node in nodes) {
            when (node) {
                is ComplexFunctionCallSyntaxNode -> {
                    when (node.functionIdentifier.value) {
                        "on" -> {
                            val event = node.args[0].tokens
                                .filterIsInstance<StringToken>()
                                .joinToString("") { it.value }
                            if (event == "server.start") {
                                onEnable += translate(node.body, EventContext("onEnable", globalContext.identifiers))
                            } else {
                                val spigotEvent = eventMap[event] ?: ""
                                require(spigotEvent.isNotEmpty()) {
                                    "Unknown event: $event (at line ${node.args[0].tokens[0].line})."
                                }
                                eventCount[event] = if (event !in eventCount) 0
                                                    else eventCount[event]!! + 1
                                val count = eventCount[event]!!.toStringWithoutZero()
                                events += "@EventHandler"
                                events += "public void on$spigotEvent$count(${spigotEvent.classNameFromQualifiedName()} event) {"
                                events += translate(node.body, EventContext(spigotEvent, globalContext.identifiers))
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
                                    it.initialValue.translate(globalContext)
                                    traits[traitName]!![it.identifier.value] = it.initialValue.resultType ?: "String"
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
                            onEnable += translate(node.body, TimerContext(globalContext.identifiers))
                            onEnable += "\n});\n"
                        }
                    }
                }
                is CommandDefinitionSyntaxNode -> {
                    // TODO: Implement command arguments
                    onEnable += "getCommand(\"${node.identifier.value.camelCase()}\""
                    onEnable += ").setExecutor((sender, command, label, args) -> {\n"
                    onEnable += translate(node.body, CommandContext(globalContext.identifiers))
                    onEnable += "});\n"
                }
                is FunctionDefinitionSyntaxNode -> continue
                is VariableDefinitionSyntaxNode -> {
                    fields += "private var ${node.identifier.value.pascalCase()} = ${
                        node.initialValue.translate(VariableDefinitionContext(globalContext.identifiers))
                    };"
                    globalContext.identifiers += VariableIdentifier(
                        node.identifier.value,
                        node.initialValue.resultType ?: throw IllegalStateException("Variable must be initialized with a real value!"),
                        node.initialValue.translate(globalContext),
                        node.identifier.line
                    )
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

    private fun translate(nodes: List<SyntaxNode>, context: Context): String {
        var builder = ""
        for (node in nodes) {
            when (node) {
                is IfSyntaxNode -> {
                    node.branches.forEach {
                        val value = it.type.value.takeUnless { v -> v in TokenType.ELIF.values } ?: "else if"
                        builder += "$value ${if (value != "else") "(${it.booleanExpression.translate(context)})" else ""} {"
                        builder += translate(it.body, context)
                        builder += "} "
                    }
                }
                is ExpressionSyntaxNode -> {
                    println(node)
                }
                is ForeachSyntaxNode -> {
                    val loopType = node.genericExpression.resultType ?: "Object"
                    builder += "for ($loopType ${node.loopIdentifier.value.camelCase()} : ${node.genericExpression.translate(ForeachContext(loopType, globalContext.identifiers))}) {\n"
                    builder += translate(node.body, context)
                    builder += "}\n"
                }
                is WhileSyntaxNode -> {
                }
                is ReturnSyntaxNode -> {
                }
                is VariableDefinitionSyntaxNode -> {
                    builder += "var ${node.identifier.value.pascalCase()} = ${
                        node.initialValue.translate(VariableDefinitionContext(globalContext.identifiers))
                    };\n"
                    context.identifiers += VariableIdentifier(
                        node.identifier.value,
                        node.initialValue.resultType ?: throw IllegalStateException("Variable must be initialized with a real value!"),
                        node.initialValue.translate(globalContext),
                        node.identifier.line
                    )
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