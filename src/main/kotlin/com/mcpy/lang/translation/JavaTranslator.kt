package com.mcpy.lang.translation

import com.mcpy.lang.abstractions.Name
import com.mcpy.lang.abstractions.Type
import com.mcpy.lang.asResource
import com.mcpy.lang.camelCase
import com.mcpy.lang.lexer.token.NumberToken
import com.mcpy.lang.lexer.token.StringToken
import com.mcpy.lang.lexer.token.TokenType
import com.mcpy.lang.pascalCase
import com.mcpy.lang.syntax.node.SyntaxNode
import com.mcpy.lang.syntax.node.control.ForeachSyntaxNode
import com.mcpy.lang.syntax.node.control.IfSyntaxNode
import com.mcpy.lang.syntax.node.control.ReturnSyntaxNode
import com.mcpy.lang.syntax.node.control.WhileSyntaxNode
import com.mcpy.lang.syntax.node.expression.ExpressionSyntaxNode
import com.mcpy.lang.syntax.node.expression.VariableDefinitionSyntaxNode
import com.mcpy.lang.syntax.node.global.CommandDefinitionSyntaxNode
import com.mcpy.lang.syntax.node.global.ComplexFunctionCallSyntaxNode
import com.mcpy.lang.syntax.node.global.FunctionDefinitionSyntaxNode
import com.mcpy.lang.translation.context.*
import com.mcpy.lang.translation.function.CustomFunction
import com.mcpy.lang.translation.function.EventFunction
import com.mcpy.lang.translation.function.TimerFunction
import com.mcpy.lang.translation.identifier.Function
import com.mcpy.lang.translation.identifier.VariableIdentifier
import org.eclipse.jdt.core.JavaCore
import org.eclipse.jdt.core.ToolFactory
import org.eclipse.jdt.core.formatter.CodeFormatter
import org.eclipse.jdt.core.formatter.DefaultCodeFormatterConstants
import org.eclipse.jface.text.Document

class JavaTranslator : Translator {

    private val imports = mutableListOf<String>()
    private val events = mutableListOf<EventFunction>()
    private val methods = mutableListOf<Function>()
    private val fields = mutableListOf<String>()
    private val onEnable = CustomFunction(mutableListOf("Override"), Type("void"), Name("onEnable", Name.NameType.FUNCTION), "", "")
    private val traits = mutableMapOf<Name, MutableMap<Name, Type>>() // <parent_class, <trait_name, data_type>>


    private val globalContext = GlobalContext()

    fun translateGlobalScope(nodes: List<SyntaxNode>) {
        var hasTimer = false
        nodes.filterIsInstance<FunctionDefinitionSyntaxNode>()
            .forEach {
                val id = it.identifier.value
                val variableArgs = it.args.args.map { arg ->
                    VariableIdentifier(Name(arg.identifier.value, Name.NameType.VARIABLE), Type(arg.type.value), "", arg.identifier.line)
                }
                val returnType = Type.toJava(it.returnType).toType()
                val func = CustomFunction(
                    mutableListOf(),
                    returnType,
                    Name(id, Name.NameType.FUNCTION),
                    it.args.translate(globalContext),
                    translate(it.body, FunctionContext(
                        returnType,
                        (globalContext.identifiers + variableArgs).toMutableList()
                    ))
                )
                methods += func
                globalContext.identifiers += func
            }
        for (node in nodes) {
            when (node) {
                is ComplexFunctionCallSyntaxNode -> {
                    when (ComplexFunctionCallSyntaxNode.Type.valueOf(node.functionIdentifier.value)) {
                        ComplexFunctionCallSyntaxNode.Type.EVENT -> {
                            val event = node.args[0].tokens
                                .filterIsInstance<StringToken>()
                                .joinToString("") { it.value }
                            // TODO: event definition changed to be `event <event_name>(event.stuff) {}`
                            if (event == "server.start") {
                                onEnable.body += "do {" + translate(node.body, EventContext(Name("onEnable", Name.NameType.EVENT), globalContext.identifiers)) + "}\n"
                            } else {
                                val spigotEvent = Events.spigotEvent(Name(event, Name.NameType.EVENT))
                                if (spigotEvent == null) {
                                    com.mcpy.lang.errors.error("Unknown event $event", node.args[0].tokens[0])
                                } else {
                                    // TODO: fix event name because of new event syntax
                                    val eventName = Name("incomplete", Name.NameType.FUNCTION)
                                    events += EventFunction(
                                        eventName,
                                        Type(spigotEvent),
                                        translate(node.body, EventContext(eventName, globalContext.identifiers))
                                    )
                                }
                            }
                        }
                        ComplexFunctionCallSyntaxNode.Type.METADATA -> {
                            val traitType = node.args[0].tokens[0] as StringToken
                            val traitName = traitType.value
                            if (traitName !in traits.mapKeys { it.key.value }) {
                                traits[Name(traitName, Name.NameType.CLASS)] = mutableMapOf()
                            }
                            node.body.filterIsInstance<VariableDefinitionSyntaxNode>()
                                .forEach {
                                    it.initialValue.translate(globalContext)
                                    val traitMap = traits.filterKeys { t -> t.value == traitName }.values.first()
                                    // TODO: redo this for the new syntax
//                                    traitMap[it.identifier.value] = it.initialValue.resultType ?: Type(String::class)
                                }
                        }
                        ComplexFunctionCallSyntaxNode.Type.TIMER -> {
                            if (!hasTimer) {
                                hasTimer = true
                                methods += TimerFunction()
                            }
                            val secondsToken = node.args[0].tokens[0]
                            com.mcpy.lang.errors.require(secondsToken is NumberToken, secondsToken) {
                                "Timer argument must be a number (at line ${secondsToken.line})."
                            }
                            onEnable.body += "beginTimer(${secondsToken.value}, () -> {\n"
                            onEnable.body += translate(node.body, TimerContext(globalContext.identifiers))
                            onEnable.body += "\n});\n"
                        }
                    }
                }
                is CommandDefinitionSyntaxNode -> {
                    // TODO: Implement command arguments
                    onEnable.body += "getCommand(\"${node.identifier.value.camelCase()}\""
                    onEnable.body += ").setExecutor((sender, command, label, args) -> {\n"
                    onEnable.body += translate(node.body, CommandContext(globalContext.identifiers))
                    onEnable.body += "});\n"
                }
                is FunctionDefinitionSyntaxNode -> continue
                is VariableDefinitionSyntaxNode -> {
                    fields += "private var ${node.identifier.value.pascalCase()} = ${
                        node.initialValue.translate(VariableDefinitionContext(globalContext.identifiers))
                    };"
                    generateVariableIdentifier(node)
                }
                else -> {
                    com.mcpy.lang.errors.error(
                        "Node type " + node::class.simpleName + " is not allowed in global scope.",
                        node.firstToken
                    )
                }
            }
        }
    }

    private fun generateVariableIdentifier(node: VariableDefinitionSyntaxNode) {
        val resultType = node.initialValue.resultType
        com.mcpy.lang.errors.require(resultType != null, node.identifier) {
            "Variable must be initialized with a real value!"
        }
        globalContext.identifiers += VariableIdentifier(
            Name(node.identifier.value, Name.NameType.VARIABLE),
            resultType,
            node.initialValue.translate(globalContext),
            node.identifier.line
        )
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
                    val loopType = node.genericExpression.resultType ?: Type(Object::class)
                    builder += "for ($loopType ${node.loopIdentifier.value.camelCase()} : ${node.genericExpression.translate(
                        ForeachContext(loopType, globalContext.identifiers)
                    )}) {\n"
                    builder += translate(node.body, context)
                    builder += "}\n"
                }
                is WhileSyntaxNode -> {
                    // TODO: unfinished syntax
                }
                is ReturnSyntaxNode -> {
                    // TODO: unfinished syntax
                }
                is VariableDefinitionSyntaxNode -> {
                    builder += "var ${node.identifier.value.pascalCase()} = ${
                        node.initialValue.translate(VariableDefinitionContext(globalContext.identifiers))
                    };\n"
                    generateVariableIdentifier(node)
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
            onEnable.build(),
            events.joinToString("\n", transform = Function::build),
            methods.joinToString("\n", transform = Function::build)
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