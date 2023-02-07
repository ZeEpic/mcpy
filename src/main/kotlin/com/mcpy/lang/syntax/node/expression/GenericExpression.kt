package com.mcpy.lang.syntax.node.expression

import com.mcpy.lang.abstractions.Type
import com.mcpy.lang.errors.error
import com.mcpy.lang.errors.require
import com.mcpy.lang.lexer.token.*
import com.mcpy.lang.syntax.ChainLink
import com.mcpy.lang.translation.context.Context
import kotlin.math.floor

data class GenericExpression(val tokens: List<Token>, override val firstToken: Token) : Expression(firstToken) {

    private var expressionChain = listOf<ChainLink>()

    override fun translate(context: Context): String {
        expressionChain = generateExpressionChain(context)
        return expressionChain.joinToString(".") {
            if (it.parametersInJava.isEmpty()) {
                it.idInJava.value
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
                            error("Unexpected token ${nextToken.type} after ID ${token.value}", token)
                        }
                    }
                }
                TokenType.STRING_LITERAL -> {
                    require(i == 0, token) {
                        "String literal must be the first token in an expression"
                    }
                    expressionChain += ChainLink(token as StringToken, null, context, expressionChain, i)
                }
                TokenType.NUMBER_LITERAL -> {
                    require(tokens.size <= 1) {
                        "Number literal must be the only token in the expression"
                    }
                    val number = (token as NumberToken).value
                    val numberAsString = if (floor(number) == number) number.toInt().toString() else number.toString()
                    expressionChain += ChainLink(StringToken(TokenType.NUMBER_LITERAL, numberAsString, token.line, token.character, token.file), null, context, expressionChain, i)
                }
                TokenType.BOOLEAN_LITERAL -> {
                    require(tokens.size <= 1) {
                        "Boolean literal must be the only token in the expression"
                    }
                    expressionChain += ChainLink(token as StringToken, null, context, expressionChain, i)
                }
                TokenType.DOT -> {
                    require(i != tokens.lastIndex, token) {
                        "Can't end expression with a dot"
                    }
                }
                else -> {
                    error("Unexpected token ${token.type} in generic expression", token)
                }
            }
        }
        return expressionChain
    }

    val resultType: Type?
        get() = expressionChain.lastOrNull()?.returnType

}
