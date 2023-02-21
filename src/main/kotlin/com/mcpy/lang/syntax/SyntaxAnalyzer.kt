package com.mcpy.lang.syntax

import com.mcpy.lang.compiler.CompilerPhase
import com.mcpy.lang.errors.CodeFile
import com.mcpy.lang.errors.error
import com.mcpy.lang.errors.require
import com.mcpy.lang.lexer.token.*
import com.mcpy.lang.syntax.node.SyntaxNode
import com.mcpy.lang.syntax.node.control.*
import com.mcpy.lang.syntax.node.expression.*
import com.mcpy.lang.syntax.node.global.*
import com.mcpy.lang.title

class SyntaxAnalyzer : CompilerPhase {

    fun analyze(code: List<Token>, file: CodeFile): List<SyntaxNode> {
        val result = mutableListOf<SyntaxNode>()
        val codeLine = mutableListOf<Token>()
        var beginningOfLine = 0
        for (i in code.indices) {
            val t = code[i]
            if (t.type !== TokenType.EOL) {
                codeLine.add(t)
            }
            if (t.type === TokenType.EOL || i == code.size - 1) {
                if (codeLine.isEmpty()) continue
                result += processLine(codeLine, code, beginningOfLine, file)
                codeLine.clear()
                beginningOfLine = i + 1
            }
        }
        return result
    }

    private fun processLine(
        codeLine: List<Token>,
        code: List<Token>,
        beginningOfLine: Int,
        file: CodeFile
    ): SyntaxNode {
        when (val first = codeLine[0].type) {
            TokenType.FUNCTION, TokenType.COMMAND, TokenType.GUI, TokenType.TIMER, TokenType.TRAIT -> {
                val lineType = first.name.title()
                require(codeLine.size >= 4, codeLine[0]) {
                    "$lineType definition must have at least a name, parentheses, and a code body"
                }
                require(codeLine[1].type == TokenType.ID, codeLine[1]) {
                    "$lineType definition must have a name"
                }
                val name = codeLine[1] as StringToken
                require(codeLine[2].type == TokenType.PARENTHESES, codeLine[2]) {
                    "$lineType definition must have parentheses, even if there are zero arguments"
                }
                val body = codeLine[codeLine.size - 1]
                val syntaxNodes = if (first != TokenType.TRAIT) {
                    require(body.type == TokenType.BRACE && body is GroupToken, body) {
                        "$lineType must have a code body"
                    }
                    analyze(getGroup(body), file)
                } else null

                require(codeLine[2] is GroupToken && codeLine[2].type == TokenType.PARENTHESES, codeLine[2]) {
                    "$lineType definition must have parentheses${if (first != TokenType.TIMER) ", even if there are zero arguments" else ""}"
                }
                if (first == TokenType.TIMER) {
                    val args = getGroup(codeLine[2])
                    require(args.size == 1 && args[0] is NumberToken, args[0]) {
                        "Timer definition must have a single argument, which is its length in seconds"
                    }
                    val seconds = (args[0] as NumberToken).value
                    return TimerDefinitionSyntaxNode(name, seconds, syntaxNodes!!, codeLine[0])
                }
                val args = ArgsExpression(getGroup(codeLine[2]), codeLine[2])
                if (first == TokenType.GUI) { // Gui doesn't have a return type
                    return handleGuiDefinitionSyntaxNode(syntaxNodes!!, codeLine, name, args)
                }
                if (first == TokenType.COMMAND || first == TokenType.TRAIT) { // Command doesn't have a return type either
                    val senderType = if (codeLine.size > 3) {
                        // They must want to use 'by type' syntax
                        // cmd foo() by player { }
                        // ^   ^  ^  ^  ^      ^
                        // 0   1  2  3? 4?     last
                        require(codeLine[3].type == TokenType.BY, codeLine[3]) {
                            "You're missing 'by' to specify the sender type"
                        }
                        require(codeLine.size == 6 && codeLine[4].type == TokenType.ID && codeLine[4] is StringToken, codeLine[4]) {
                            "You're missing a sender type after 'by'. It can either be player or console"
                        }
                        codeLine[4] as StringToken
                    } else null
                    if (first == TokenType.COMMAND) {
                        require(
                            senderType == null || senderType.value == "player" || senderType.value == "console",
                            senderType ?: codeLine[0]
                        ) {
                            "Sender type must be either player or console"
                        }
                        return CommandDefinitionSyntaxNode(name, args, senderType, syntaxNodes!!, codeLine[0])
                    }
                    // trait foo() by player
                    require(senderType != null, codeLine[0]) {
                        "Trait definition must have a targeted type"
                    }
                    return TraitDefinitionSyntaxNode(name, args, senderType, codeLine[0])
                }
                val returnType = if (codeLine.size > 3 ) {
                    // def foo(): bar
                    // ^   ^  ^ ^ ^
                    // 0   1  2 3 4
                    require(codeLine[3].type == TokenType.COLON, codeLine[3]) {
                        "$lineType has a return type, but no colon"
                    }
                    require(codeLine.size == 6 && codeLine[4].type == TokenType.ID && codeLine[4] is StringToken, codeLine[4]) {
                        "$lineType has a colon, but no return type"
                    }
                    codeLine[4] as StringToken
                } else null
                return FunctionDefinitionSyntaxNode(name, args, returnType, syntaxNodes!!, codeLine[0])
            }
            TokenType.ID -> {
                when (val second = codeLine[1].type) {
                    TokenType.ASSIGNMENT_OPERATOR -> {
                        return VariableDefinitionSyntaxNode(
                            codeLine[0] as StringToken,
                            GenericExpression(codeLine.subList(2, codeLine.size)),
                            codeLine[0]
                        )
                    }
                    TokenType.DOT, TokenType.PARENTHESES -> { // Accessing a property or calling a function
                        return ExpressionSyntaxNode(codeLine, codeLine[0])
                    }
                    else -> {
                        error(
                            "Unexpected token after identifier of type ${second.name}",
                            codeLine[1]
                        )
                    }
                }
            }
            TokenType.IF -> {
                val branches = mutableListOf<ConditionalBranch>()
                val split = codeLine.split(TokenType.BRACE, true) { it.type }
                for (i in split.indices) {
                    val branch = split[i]
                    if (branch.isEmpty()) continue
                    require(branch[0].type in listOf(TokenType.IF, TokenType.ELSE, TokenType.ELIF), branch[0]) {
                        "If statement can't have a branch starting with ${branch[0].type.name.lowercase()}."
                    }
                    require(i == split.lastIndex || branch[0].type != TokenType.ELSE, branch[0]) {
                        "Else statement must be the last branch of an if statement."
                    }
                    require(i == 0 || branch[0].type != TokenType.IF, branch[0]) {
                        "If statement can't be preceded by another branch."
                    }
                    branches += ConditionalBranch(
                        branch[0] as StringToken,
                        BooleanExpression(branch.drop(1).dropLast(1), branch[1]),
                        analyze(getGroup(branch.last()), file)
                    )
                }
                require(branches.isNotEmpty(), codeLine[0]) {
                    "If statement must have a code body"
                }
                return IfSyntaxNode(branches, codeLine[0])
            }
            TokenType.WHILE -> {
                val body = codeLine.last()
                require(body.type == TokenType.BRACE, body) {
                    "While statement must have code body"
                }
                return WhileSyntaxNode(
                    BooleanExpression(codeLine.subList(1, codeLine.size - 1), codeLine[1]),
                    analyze(getGroup(body), file),
                    codeLine[0]
                )
            }
            TokenType.MATCH -> {
                val bodyToken = codeLine.last()
                require(bodyToken.type == TokenType.BRACE, bodyToken) {
                    "Match statement must have code body"
                }
                val body = getGroup(bodyToken)
                val branches = HashMap<List<Token>, List<SyntaxNode>>()
                body.split(TokenType.EOL) { it.type }
                    .forEach {
                        val branchBodyToken = it.last()
                        require(branchBodyToken.type == TokenType.BRACE, branchBodyToken) {
                            "Match branch must have code body"
                        }
                        branches[it.subList(0, it.lastIndex)] =
                            analyze(getGroup(branchBodyToken), file)
                    }
                return MatchSyntaxNode(
                    GenericExpression(codeLine.subList(1, codeLine.size - 1)),
                    branches,
                    codeLine[0]
                )
            }
            TokenType.RETURN -> {
                return ReturnSyntaxNode(
                    GenericExpression(code.subList(beginningOfLine + 2, codeLine.size)),
                    code[beginningOfLine + 1]
                )
            }
            TokenType.FOR -> {
                val body = codeLine.last()
                require(body.type == TokenType.BRACE, body) {
                    "For loop must have code body"
                }
                val split = codeLine.split(TokenType.IN) { it.type }
                require(split.size == 2, codeLine[0]) {
                    "For loop must have the 'in' keyword"
                }
                val loopIdentifiers = split[0].split(TokenType.COMMA) { it.type }
                require(loopIdentifiers.all { it.size == 1 && it[0].type == TokenType.ID }, codeLine[1]) {
                    "The identifiers in the for loop must be a name like 'i'"
                }
                val loopIterator = split[1]
                return ForeachSyntaxNode(
                    loopIdentifiers.map { it.filterIsInstance<StringToken>().first() },
                    GenericExpression(loopIterator),
                    analyze(getGroup(body), file),
                    codeLine[0]
                )
            }
            TokenType.WHEN -> { // Events
                val body = codeLine.last()
                require(body.type == TokenType.BRACE && body is GroupToken, body) {
                    "Event must have code body in { }. Removing this line fixes the error too"
                }
                // when event.name { }
                // ^    ^          ^
                // 0    1+         last
                require(codeLine.size >= 3, codeLine[0]) {
                    "You must specify the name of the event"
                }
                val rawEvent = codeLine.drop(1).dropLast(1).filterIsInstance<StringToken>()
                require(rawEvent.size == codeLine.size - 2, rawEvent[0]) {
                    "You must specify the name of the event"
                }
                return EventDefinitionSyntaxNode(EventExpression(rawEvent, rawEvent[0]), analyze(getGroup(body), file), codeLine[0])
            }
            else -> {
                error("You can't start a line like that", codeLine[0])
            }
        }
    }

    private fun handleGuiDefinitionSyntaxNode(
        syntaxNodes: List<SyntaxNode>,
        codeLine: List<Token>,
        name: StringToken,
        args: ArgsExpression
    ): GuiDefinitionSyntaxNode {
        // A gui requires many variables to be defined
        // These are: title, pattern, legend, and optionally the action matcher
        val variableNodes = syntaxNodes.filterIsInstance<VariableDefinitionSyntaxNode>()
        val titleNode = variableNodes.firstOrNull { it.identifier.value == "title" }
        require(titleNode != null, codeLine[0]) {
            "Gui definition must have a title variable"
        }
        val title = titleNode.initialValue.firstToken as? StringToken
        require(
            title != null && titleNode.initialValue.tokens.countRecursive() == 1,
            titleNode.initialValue.firstToken
        ) {
            "Gui title must be a string"
        }
        val patternNode = variableNodes.firstOrNull { it.identifier.value == "pattern" }
        require(patternNode != null, codeLine[0]) {
            "Gui definition must have a pattern variable"
        }
        val legend = variableNodes.firstOrNull { it.identifier.value == "legend" }
        require(legend != null, codeLine[0]) {
            "Gui definition must have a legend variable"
        }
        val actionMatcherNode = syntaxNodes.filterIsInstance<MatchSyntaxNode>()
            .firstOrNull { it.genericExpression.firstToken.value == "action" && it.genericExpression.tokens.countRecursive() == 1 }
        return GuiDefinitionSyntaxNode(
            name,
            args,
            title.value,
            patternNode.initialValue,
            legend.initialValue,
            actionMatcherNode,
            syntaxNodes,
            codeLine[0]
        )
    }
}
