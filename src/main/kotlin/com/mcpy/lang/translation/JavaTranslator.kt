package com.mcpy.lang.translation

import com.mcpy.lang.abstractions.Name
import com.mcpy.lang.abstractions.Type
import com.mcpy.lang.asResource
import com.mcpy.lang.errors.require
import com.mcpy.lang.lexer.token.StringToken
import com.mcpy.lang.lexer.token.TokenType
import com.mcpy.lang.lexer.token.join
import com.mcpy.lang.lexer.token.split
import com.mcpy.lang.pascalCase
import com.mcpy.lang.startsWithAny
import com.mcpy.lang.syntax.chain.validateSetter
import com.mcpy.lang.syntax.node.SyntaxNode
import com.mcpy.lang.syntax.node.control.*
import com.mcpy.lang.syntax.node.expression.ExpressionSyntaxNode
import com.mcpy.lang.syntax.node.expression.GenericExpression
import com.mcpy.lang.syntax.node.expression.PropertyAssignmentSyntaxNode
import com.mcpy.lang.syntax.node.expression.VariableDefinitionSyntaxNode
import com.mcpy.lang.syntax.node.global.*
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

// TODO For later when coding GUI:
/*
        GUI creator function

        public Inventory createGui(List<String> patternList, String title, Map<Character, ItemStack> legend) {
            Inventory inventory = Bukkit.createInventory(null, pattern.length() / 9, title);
            for (int i = 0; i < pattern.length(); i++) {
                char c = pattern.charAt(i);
                if (c == ' ') continue;
                if (!legend.containsKey(c)) continue;
                inventory.setItem(i, legend.get(c));
            }
            return inventory;
        }

        InventoryClickEvent

        String pattern = "";
        switch (pattern.charAt(event.getSlot())) {
            case 'put user specified char here' -> {
                // do something
            }
        }
 */

class JavaTranslator : Translator {

    private val imports = mutableListOf<String>()
    private val events = mutableListOf<EventFunction>()
    private val methods = mutableListOf<Function>()
    private val fields = mutableListOf<String>()
    private val onEnable = CustomFunction(mutableListOf("Override"), Type.VOID, Name("onEnable", Name.NameType.FUNCTION), "", "")
    private val traits = mutableMapOf<Name, TraitDefinitionSyntaxNode>()
    private val guiPatterns = mutableMapOf<String, String>() // <gui_name, pattern>


    private val globalContext = GlobalContext()

    fun translateGlobalScope(nodes: List<SyntaxNode>) {
        println("nodes1: $nodes")
        nodes.filterIsInstance<FunctionDefinitionSyntaxNode>()
            .forEach {
                val id = it.identifier.value
                val variableArgs = it.args.args.map { arg ->
                    VariableIdentifier(Name(arg.identifier.value, Name.NameType.VARIABLE), Type(arg.type.value), "")
                }
                val returnType = it.returnType?.let { t -> Type.toJava(t.value)?.toType() } ?: Type.VOID
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
        println("nodes2: $nodes")
        for (node in nodes) {
            println("node: $node")
            when (node) {
                is EventDefinitionSyntaxNode -> {
                    val event = Type(node.event.translate(globalContext))
                    // TODO: This causes a problem if more than one of the same event is defined
                    val name = Name("on" + event.type, Name.NameType.FUNCTION)
                    events += EventFunction(
                        name,
                        event,
                        translate(node.body, EventContext(event, globalContext.identifiers))
                    )
                }
                is TimerDefinitionSyntaxNode -> {
                    if (methods.none { it is TimerFunction }) {
                        methods += TimerFunction()
                    }
                    onEnable.body += "\ngetServer().getScheduler().runTaskTimer(this, () -> {\n" +
                            translate(node.body, TimerContext(globalContext.identifiers)) +
                    "}, 0, ${(node.seconds * 20.0).toLong()}L);\n"
                }
                is GuiDefinitionSyntaxNode -> {

                }
                is TraitDefinitionSyntaxNode -> {
                    traits[Name(node.identifier.value, Name.NameType.VARIABLE)] = node
                }
                is CommandDefinitionSyntaxNode -> {
                    // TODO: Implement command arguments
                    onEnable.body += "getCommand(\"${node.identifier.value}\""
                    onEnable.body += ").setExecutor((sender, command, label, args) -> {\n"
                    println("hi" + node.body)
                    onEnable.body += translate(node.body, CommandContext(globalContext.identifiers))
                    onEnable.body += "});\n"
                }
                is FunctionDefinitionSyntaxNode -> continue
                is VariableDefinitionSyntaxNode -> {
                    fields += "private" + generateVariableIdentifier(node, globalContext)
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

    private fun generateVariableIdentifier(node: VariableDefinitionSyntaxNode, context: Context): String {
        val resultType = node.initialValue.resultType
        require(resultType != null, node.identifier) {
            "Variable must be initialized with a real value!"
        }
        val name = Name(node.identifier.value, Name.NameType.VARIABLE)
        val translate = node.initialValue.translate(VariableDefinitionContext(context.identifiers))
        context.identifiers += VariableIdentifier(
            name,
            resultType,
            translate
        )
        val type = resultType.type
        val varType = if (context.identifiers.filterIsInstance<VariableIdentifier>().any { it.name.value == name.value }) {
            ""
        } else if (type == "Object" || type == "java.lang.Object") "var "
          else "$type "
        return "$varType${node.identifier.value.pascalCase()} = $translate;"
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
                    val loopType = node.loopIterator.resultType
                    require(loopType?.type?.startsWithAny("HashMap", "List") == true, node.loopIterator.firstToken) {
                        "A for each loop can only iterate over a list or dictionary"
                    }
                    val loopTypeValue = loopType?.type!!
                    val loopIdentifierTypes = loopTypeValue.split("<", ">")[1].split(",")
                    val loopIdentifiers = node.loopIdentifiers.map { it.value }.zip(loopIdentifierTypes).map { (name, type) ->
                        VariableIdentifier(Name(name, Name.NameType.VARIABLE), Type.toJava(type)?.value?.let { Type(it) } ?: Type.VOID, "")
                    }
                    if (loopTypeValue.startsWith("HashMap")) {
                        require(loopIdentifiers.size == 2, node.loopIdentifiers.lastOrNull() ?: node.firstToken) {
                            "A for each loop over a dictionary must have two variables, one for the keys and one for the values"
                        }
                        val loopIterator = node.loopIterator.translate(
                            ForeachContext(loopType,
                                (globalContext.identifiers + loopIdentifiers ).toMutableList()
                            )
                        )
                        builder += "$loopIterator.forEach((${loopIdentifiers.joinToString { it.name.value }}) -> {\n"
                    } else {
                        require(loopIdentifiers.size == 1, node.loopIdentifiers.lastOrNull() ?: node.firstToken) {
                            "A for each loop over a list must have one variable for each value"
                        }
                        val id = loopIdentifiers.first()
                        builder += "for (${Type.toJava(id.type.type)} ${id.name.converted} : ${node.loopIterator.translate(
                            ForeachContext(loopType,
                                (globalContext.identifiers + id).toMutableList()
                            )
                        )}) {\n"
                    }
                    builder += translate(node.body, context)
                    builder += "}\n"
                }
                is WhileSyntaxNode -> {
                    // TODO: unfinished syntax
                }
                is ReturnSyntaxNode -> {
                    // TODO: unfinished syntax
                }
                is MatchSyntaxNode -> {

                }
                is VariableDefinitionSyntaxNode -> {
                    builder += generateVariableIdentifier(node, context) + "\n"
                }
                is PropertyAssignmentSyntaxNode -> {
                    println(nodes)
                    println(node)
                    println(node.left)
                    println(node.left.toMutableList())
                    val left = node.left.split(TokenType.DOT) { it.type }
                    val setter = left.last()
                    require(setter.size == 1 && setter.firstOrNull()?.type == TokenType.ID, node.left.lastOrNull() ?: left.first().first()) {
                        "You can't set a property like that. Follow this format instead: object.property = value"
                    }
                    val obj = left.dropLast(1).join { StringToken(TokenType.DOT, ".", it.line, it.character, it.file) }
                    val leftType = GenericExpression(obj).resultType
                    require(leftType != null, node.left.first()) {
                        "You can't set a property on a null value"
                    }
                    validateSetter(leftType, Name((obj.first() as StringToken).value, Name.NameType.FUNCTION), node.right, node.right.first())
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
            fields.joinToString("\n") + guiPatterns.map { (name, pattern) -> "String gui${name.pascalCase()} = \"$pattern\";" },
            onEnable.build(),
            events.joinToString("\n", transform = Function::build),
            methods.joinToString("\n", transform = Function::build)
        )

        // ZeEpic note: I have no idea why this formatting thing works, or why we have to use java version 1.5 formatting
        val options = DefaultCodeFormatterConstants.getEclipseDefaultSettings()

        options[JavaCore.COMPILER_COMPLIANCE] = JavaCore.VERSION_1_7
        options[JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM] = JavaCore.VERSION_1_7
        options[JavaCore.COMPILER_SOURCE] = JavaCore.VERSION_1_7

        val formatter = ToolFactory.createCodeFormatter(options)
        val edits = formatter.format(CodeFormatter.K_COMPILATION_UNIT, complete, 0, complete.length, 0, "\n")
        val document = Document(complete)
        edits.apply(document)
        return document.get()
    }
}