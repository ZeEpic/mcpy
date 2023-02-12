package com.mcpy.lang.syntax

import com.mcpy.lang.compiler.CompilerPhase
import com.mcpy.lang.errors.CodeFile
import com.mcpy.lang.errors.error
import com.mcpy.lang.errors.require
import com.mcpy.lang.lexer.token.*
import com.mcpy.lang.syntax.node.SyntaxNode
import com.mcpy.lang.syntax.node.control.*
import com.mcpy.lang.syntax.node.expression.*
import com.mcpy.lang.syntax.node.global.CommandDefinitionSyntaxNode
import com.mcpy.lang.syntax.node.global.FunctionDefinitionSyntaxNode
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
                result += processLine(codeLine, code, codeLine[0].line, beginningOfLine, file)
                codeLine.clear()
                beginningOfLine = i + 1
            }
        }
        return result
    }

    private fun processLine(
        codeLine: List<Token>,
        code: List<Token>,
        line: Int,
        beginningOfLine: Int,
        file: CodeFile
    ): SyntaxNode {
        when (val first = codeLine[0].type) {
            TokenType.FUNCTION, TokenType.COMMAND -> {
                val lineType = first.name.title()
                require(codeLine[1].type === TokenType.ID, codeLine[1]) {
                    "$lineType definition must have a name"
                }
                val name = codeLine[1] as StringToken
                require(codeLine[2].type === TokenType.PARENTHESES, codeLine[2]) {
                    "$lineType definition must have parentheses, even if there are zero arguments"
                }
                val args = ArgsExpression(getGroup(codeLine[2]), codeLine[2])
                val body = codeLine[codeLine.size - 1]
                require(body.type === TokenType.BRACE, body) {
                    "$lineType must have a code body"
                }
                val syntaxNodes = analyze(getGroup(body), file)
                if (first === TokenType.COMMAND) { // Command doesn't have a return type
                    return CommandDefinitionSyntaxNode(name, args, syntaxNodes, codeLine[0])
                }
                val returnType = if (codeLine[3].type === TokenType.COLON) {
                    require(codeLine[4].type === TokenType.ID, codeLine[4]) {
                        "$lineType has a colon, but no return type"
                    }
                    codeLine[4] as StringToken
                } else {
                    StringToken(TokenType.ID, "void", line, -1, file)
                }
                return FunctionDefinitionSyntaxNode(name, args, returnType, syntaxNodes, codeLine[0])
            }
            TokenType.ID -> {
                when (val second = codeLine[1].type) {
                    TokenType.ASSIGNMENT_OPERATOR -> {
                        return VariableDefinitionSyntaxNode(
                            codeLine[0] as StringToken,
                            GenericExpression(sub(codeLine, 2, codeLine.size), codeLine[2]),
                            codeLine[0]
                        )
                    }
                    TokenType.DOT -> {
                        return ExpressionSyntaxNode(codeLine, codeLine[0])
                    }
                    TokenType.PARENTHESES -> {
                        val argTokens = getGroup(codeLine[1])
                        val args = argTokens.split(TokenType.COMMA).map { GenericExpression(it, it[0]) }
                        if (codeLine.size <= 2 || codeLine[2].type !== TokenType.BRACE) {
                            return ExpressionSyntaxNode(codeLine, codeLine[0])
                        }
                        val tokens = getGroup(codeLine[2])
                        return if (codeLine[0] is StringToken) {
                            val stringToken = codeLine[0] as StringToken
                            ComplexFunctionCallSyntaxNode(stringToken, args, analyze(tokens, file), codeLine[0])
                        } else {
                            error("Function call must have the function name.", codeLine[0])
                        }
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
                val split = codeLine.split(TokenType.BRACE, true)
                for (i in split.indices) {
                    val branch = split[i]
                    if (branch.isEmpty()) continue
                    require(
                        branch[0].type in listOf(TokenType.IF, TokenType.ELSE, TokenType.ELIF),
                        branch[0]
                    ) {
                        "If statement can't have a branch starting with ${branch[0].type.name.lowercase()}."
                    }
                    require(i == split.lastIndex || branch[0].type != TokenType.ELSE) {
                        "Else statement must be the last branch of an if statement."
                    }
                    require(i == 0 || branch[0].type != TokenType.IF) {
                        "If statement can't be preceded by another branch."
                    }
                    branches += ConditionalBranch(
                        branch[0] as StringToken,
                        BooleanExpression(branch.drop(1).dropLast(1), branch[1]),
                        analyze(getGroup(branch.last()), file)
                    )
                }
                require(branches.isNotEmpty()) {
                    "If statement must have a code body"
                }
                return IfSyntaxNode(branches, codeLine[0])
            }
            TokenType.WHILE -> {
                val body = codeLine.last()
                require(body.type == TokenType.BRACE) {
                    "While statement must have code body"
                }
                return WhileSyntaxNode(
                    BooleanExpression(sub(codeLine, 1, codeLine.size - 1), codeLine[1]),
                    analyze(getGroup(body), file),
                    codeLine[0]
                )
            }
            TokenType.MATCH -> {
                val bodyToken = codeLine.last()
                require(bodyToken.type === TokenType.BRACE) {
                    "Match statement must have code body"
                }
                val body = getGroup(bodyToken)
                val branches = HashMap<List<Token>, List<SyntaxNode>>()
                body.split(TokenType.EOL).forEach {
                    val branchBodyToken = it.last()
                    require(branchBodyToken.type === TokenType.BRACE) {
                        "Match branch must have code body"
                    }
                    branches[sub(it, 0, it.lastIndex)] =
                        analyze(getGroup(branchBodyToken), file)
                }
                return MatchSyntaxNode(
                    GenericExpression(sub(codeLine, 1, codeLine.size - 1), codeLine[1]),
                    branches,
                    codeLine[0]
                )
            }
            TokenType.RETURN -> {
                return ReturnSyntaxNode(
                    GenericExpression(sub(code, beginningOfLine + 2), code[beginningOfLine + 2]),
                    code[beginningOfLine + 1]
                )
            }
            TokenType.FOR -> {
                val body = codeLine.last()
                require(body.type === TokenType.BRACE) {
                    "For loop must have code body"
                }
                require(codeLine[2].type === TokenType.IN) {
                    "For loop must have the 'in' keyword"
                }
                return ForeachSyntaxNode(
                    codeLine[1] as StringToken,
                    GenericExpression(sub(codeLine, 3, codeLine.size - 1), codeLine[3]),
                    analyze(getGroup(body), file),
                    codeLine[1]
                )
            }
            else -> {
                error("You can't start a line like that", codeLine[0])
            }
        }
    }
}
