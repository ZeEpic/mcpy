package com.rimlang.rim.syntax

import com.rimlang.rim.lexer.StringToken
import com.rimlang.rim.lexer.Token
import com.rimlang.rim.lexer.TokenType
import com.rimlang.rim.util.*

@Throws(RimSyntaxException::class)
fun analyze(code: List<Token>): List<SyntaxNode> {
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
            result += processLine(codeLine, code, codeLine[0].line, beginningOfLine)
            codeLine.clear()
            beginningOfLine = i + 1
        }
    }
    return result
}

@Throws(RimSyntaxException::class)
private fun processLine(codeLine: List<Token>, code: List<Token>, line: Int, beginningOfLine: Int): SyntaxNode {
    when (val first = codeLine[0].type) {
        TokenType.FUNCTION, TokenType.COMMAND -> {
            val lineType = first.name.title()
            if (codeLine[1].type !== TokenType.ID) {
                throw RimSyntaxException("$lineType definition must have a name", line)
            }
            val name = codeLine[1] as StringToken
            if (codeLine[2].type !== TokenType.PARENTHESES) {
                throw RimSyntaxException(
                    "$lineType definition must have parentheses, even if there are zero arguments",
                    line
                )
            }
            val args = ArgsExpression(getGroup(codeLine[2]))
            val body = codeLine[codeLine.size - 1]
            if (body.type !== TokenType.BRACE) {
                throw RimSyntaxException("$lineType must have a code body", line)
            }
            val syntaxNodes = analyze(getGroup(body))
            if (first === TokenType.COMMAND) { // Command doesn't have a return type
                return CommandDefinitionSyntaxNode(name, args, syntaxNodes)
            }
            val returnType = if (codeLine[3].type === TokenType.COLON) {
                if (codeLine[4].type !== TokenType.TYPE) {
                    throw RimSyntaxException("$lineType has a colon, but no return type", line)
                }
                codeLine[4] as StringToken
            } else {
                StringToken(TokenType.TYPE, "void", line, -1)
            }
            return FunctionDefinitionSyntaxNode(name, args, returnType, syntaxNodes)
        }
        TokenType.ID -> {
            when (val second = codeLine[1].type) {
                TokenType.ASSIGNMENT_OPERATOR -> {
                    return VariableDefinitionSyntaxNode(
                        codeLine[0] as StringToken,
                        GenericExpression(sub(codeLine, 2, codeLine.size))
                    )
                }
                TokenType.DOT -> {
                    println(codeLine)
                    return ExpressionSyntaxNode(codeLine)
                }
                TokenType.PARENTHESES -> {
                    val argTokens = getGroup(codeLine[1])
                    val args = argTokens.split(TokenType.COMMA).map(::GenericExpression)
                    if (codeLine.size <= 2 || codeLine[2].type !== TokenType.BRACE) {
                        println(codeLine)
                        return ExpressionSyntaxNode(codeLine)
                    }
                    val tokens = getGroup(codeLine[2])
                    return if (codeLine[0] is StringToken) {
                        val stringToken = codeLine[0] as StringToken
                        ComplexFunctionCallSyntaxNode(stringToken, args, analyze(tokens))
                    } else {
                        throw RimSyntaxException("Function call must have the function name", line)
                    }
                }
                else -> {
                    throw RimSyntaxException("Unexpected token after identifier of type ${second.name}", line)
                }
            }
        }
        TokenType.IF -> {
            val branches = mutableListOf<ConditionalBranch>()
            val split = codeLine.split(TokenType.BRACE, true)
            for (i in split.indices) {
                val branch = split[i]
                if (branch.isEmpty()) continue
                if (branch[0].type !in listOf(TokenType.IF, TokenType.ELSE, TokenType.ELIF)) {
                    throw RimSyntaxException("If statement can't have a branch starting with ${branch[0].type.name.lowercase()}.", line)
                }
                if (i != split.lastIndex && branch[0].type == TokenType.ELSE) {
                    throw RimSyntaxException("Else statement can't be followed by another branch", line)
                }
                if (i != 0 && branch[0].type == TokenType.IF) {
                    throw RimSyntaxException("If statement can't be preceded by another branch", line)
                }
                branches += ConditionalBranch(
                    branch[0] as StringToken,
                    BooleanExpression(branch.drop(1).dropLast(1)),
                    analyze(getGroup(branch.last()))
                )
            }
            if (branches.isEmpty()) {
                throw RimSyntaxException("If statement must have a code body", line)
            }
            return IfSyntaxNode(branches)
        }
        TokenType.WHILE -> {
            val body = codeLine.last()
            if (body.type !== TokenType.BRACE) {
                throw RimSyntaxException("While statement must have code body", line)
            }
            return WhileSyntaxNode(
                BooleanExpression(sub(codeLine, 1, codeLine.size - 1)),
                analyze(getGroup(body))
            )
        }
        TokenType.MATCH -> {
            val bodyToken = codeLine.last()
            if (bodyToken.type !== TokenType.BRACE) {
                throw RimSyntaxException("Match statement must have code body", line)
            }
            val body = getGroup(bodyToken)
            val branches = HashMap<List<Token>, List<SyntaxNode>>()
            body.split(TokenType.EOL).forEach {
                val branchBodyToken = it.last()
                if (branchBodyToken.type !== TokenType.BRACE) {
                    throw RimSyntaxException("Match branch must have code body", branchBodyToken.line)
                }
                branches[sub(it, 0, it.lastIndex)] =
                    analyze(getGroup(branchBodyToken))
            }
            return MatchSyntaxNode(
                GenericExpression(sub(codeLine, 1, codeLine.size - 1)),
                branches
            )
        }
        TokenType.RETURN -> {
            return ReturnSyntaxNode(GenericExpression(sub(code, beginningOfLine + 2)))
        }
        TokenType.FOR -> {
            val body = codeLine[codeLine.size - 1]
            if (body.type !== TokenType.BRACE) {
                throw RimSyntaxException("For each statement must have code body", line)
            }
            if (codeLine[2].type !== TokenType.IN) {
                throw RimSyntaxException("For loop must have 'in' token", line)
            }
            return ForeachSyntaxNode(
                codeLine[1] as StringToken,
                GenericExpression(sub(codeLine, 3, codeLine.size - 1)),
                analyze(getGroup(body))
            )
        }
        else -> {
            println(codeLine)
            throw RimSyntaxException("Illegal start to line", line)
        }
    }
}
