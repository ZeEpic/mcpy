package com.rimlang.rim.syntax

import com.rimlang.rim.lexer.*
import com.rimlang.rim.translation.*
import com.rimlang.rim.util.split
import kotlin.math.floor

data class GenericExpression(val tokens: List<Token>) : Expression() {

    private var expressionChain = listOf<ChainLink>()

    override fun translate(context: Context): String {
        expressionChain = generateExpressionChain(context)
        return expressionChain.joinToString(".") {
            if (it.parametersInJava.isEmpty()) {
                it.idInJava
            } else {
                "${it.idInJava}(${it.parametersInJava.joinToString { p -> p.translate(context) }})"
            }
        }
    }

    private fun generateExpressionChain(context: Context): List<ChainLink> {
        val expressionChain = mutableListOf<ChainLink>()
        for (i in tokens.indices) {
            val token = tokens[i]
            when (token.type) {
                TokenType.ID -> {
                    if (i == tokens.lastIndex) {
                        expressionChain += ChainLink(token as StringToken, null, context, expressionChain, i)
                        continue
                    }
                    val nextToken = tokens[i + 1]
                    when (nextToken.type) {
                        TokenType.DOT -> {
                            expressionChain += ChainLink(token as StringToken, null, context, expressionChain, i)
                            continue
                        }
                        TokenType.PARENTHESES -> {
                            if (nextToken !is GroupToken) continue
                            expressionChain += ChainLink(token as StringToken, nextToken.value.split(TokenType.COMMA), context, expressionChain, i)
                        }
                        else -> {
                            throw RimSyntaxException(
                                "Unexpected token ${nextToken.type} after ID ${token.value}",
                                token.line
                            )
                        }
                    }
                }
                TokenType.STRING_LITERAL -> {
                    if (i != 0) {
                        throw RimSyntaxException("String literal must be the first token in an expression", token.line)
                    }
                    expressionChain += ChainLink(token as StringToken, null, context, expressionChain, i)
                }
                TokenType.NUMBER_LITERAL -> {
                    if (tokens.size > 1) {
                        throw RimSyntaxException("Number literal must be the only token in the expression", token.line)
                    }
                    val number = (token as NumberToken).value
                    val numberAsString = if (floor(number) == number) number.toInt().toString() else number.toString()
                    expressionChain += ChainLink(StringToken(TokenType.NUMBER_LITERAL, numberAsString, token.line, token.character), null, context, expressionChain, i)
                }
                TokenType.BOOLEAN_LITERAL -> {
                    if (tokens.size > 1) {
                        throw RimSyntaxException("Boolean literal must be the only token in the expression", token.line)
                    }
                    expressionChain += ChainLink(token as StringToken, null, context, expressionChain, i)
                }
                TokenType.DOT -> {
                    if (i == tokens.lastIndex) {
                        throw RimSyntaxException("Can't end expression with a dot", token.line)
                    }
                }
                else -> {
                    throw RimSyntaxException("Unexpected token ${token.type} in generic expression", token.line)
                }
            }
        }
        return expressionChain
    }

    val resultType: String?
        get() = expressionChain.lastOrNull()?.returnType

}
