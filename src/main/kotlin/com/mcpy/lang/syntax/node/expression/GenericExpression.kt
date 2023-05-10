package com.mcpy.lang.syntax.node.expression

import com.mcpy.lang.abstractions.Type
import com.mcpy.lang.enumNameTitle
import com.mcpy.lang.errors.error
import com.mcpy.lang.errors.require
import com.mcpy.lang.lexer.token.*
import com.mcpy.lang.syntax.chain.*
import com.mcpy.lang.translation.context.Context
import kotlin.math.floor

data class GenericExpression(val tokens: List<Token>, override val firstToken: Token) : Expression(firstToken) {

    constructor(tokens: List<Token>) : this(tokens, tokens.firstOrNull() ?: error("How did this happen (tokens is empty for generic expression)?"))

    private var expressionChain = listOf<ChainLink>()

    override fun translate(context: Context): String {
        expressionChain = generateExpressionChain(context)
        return expressionChain.joinToString(".") {
            it.generate().first.translate(context)
        }
    }

    private fun generateExpressionChain(context: Context): List<ChainLink> {
        val expressionChain = mutableListOf<ChainLink>()
        for (i in tokens.indices) {
            val token = tokens[i]
            when (token.type) {
                TokenType.PARENTHESES -> {
                    expressionChain += ChainGroup((token as GroupToken).value, context, expressionChain,token, i)
                    continue
                }
                TokenType.ID -> {
                    if (i == tokens.lastIndex) {
                        expressionChain += ChainFunction(token as StringToken, null, context, expressionChain, token, i)
                        continue
                    }
                    val nextToken = tokens[i + 1]
                    when (nextToken.type) {
                        TokenType.DOT -> {
                            // Property
                            expressionChain += ChainFunction(token as StringToken, null, context, expressionChain,token, i)
                            continue
                        }
                        TokenType.PARENTHESES -> {
                            // Function call
                            val callArgs = (nextToken as GroupToken).value.split(TokenType.COMMA) { it.type }
                            expressionChain += ChainFunction(token as StringToken, callArgs, context, expressionChain, nextToken, i)
                            continue
                        }
                        else -> {
                            require(nextToken is GroupToken, nextToken) {
                                "Expected parentheses or a period, but found a ${nextToken.type.name.enumNameTitle()}."
                            }
                        }
                    }
                }
                TokenType.STRING_LITERAL -> {
                    // String
                    require(i == 0, token) {
                        "A string must be the first part of an expression"
                    }
                    expressionChain += ChainLiteral(token as StringToken, context, expressionChain, i)
                    continue
                }
                TokenType.NUMBER_LITERAL -> {
                    // Int, Long, Short, Double, or Float
                    require(tokens.count() == 1, token) {
                        "A number must be the only part of an expression"
                    }
                    val number = (token as NumberToken).value
                    val numberAsString = if (floor(number) == number) number.toInt().toString() else number.toString()
                    val stringToken = StringToken(TokenType.NUMBER_LITERAL, numberAsString, token.line, token.character, token.file)
                    expressionChain += ChainLiteral(stringToken, context, expressionChain, i)
                    continue
                }
                TokenType.BOOLEAN_LITERAL -> {
                    // Boolean
                    require(tokens.size <= 1, token) {
                        "A boolean must be the only part of an expression"
                    }
                    expressionChain += ChainLiteral(token as StringToken, context, expressionChain, i)
                    continue
                }
                TokenType.DOT -> {
                    require(i != tokens.lastIndex, token) {
                        "Can't end expression with a dot"
                    }
                }
                TokenType.BRACE -> {
                    // Dictionary
                    require(tokens.size <= 1, token) {
                        "A new dictionary must be the only part of an expression"
                    }
                    val items = (token as GroupToken).value.split(TokenType.COMMA) { it.type }
                        .map { it.split(TokenType.COLON) { tok -> tok.type } }
                        .map {
                            require(it.size == 2 && it.all(List<Token>::isNotEmpty), it.firstOrNull()?.firstOrNull() ?: token) {
                                "All dictionary items must be in the form of 'key: value'"
                            }
                            it[0] to it[1]
                        }
                        .associate { GenericExpression(it.first) to GenericExpression(it.second) }
                    val keySet = items.keys.map { it.translate(context) }.toSet()
                    require(keySet.size == items.size, token) {
                        "All dictionary keys must be unique"
                    }
                    expressionChain += ChainDictionary(items, context, expressionChain, token, i)
                    continue
                }
                TokenType.BRACKET -> {
                    // List
                    require(tokens.size <= 1, token) {
                        "A new list must be the only part of an expression"
                    }
                    val items = (token as GroupToken).value.split(TokenType.COMMA) { it.type }
                        .map { GenericExpression(it) }
                    items.firstOrNull { it.tokens.isEmpty() }?.let {
                        error("You can't have 2 commas with nothing between them", it.firstToken)
                    }
                    expressionChain += ChainList(items, context, expressionChain, token, i)
                    continue
                }
                else -> {
                    error("This '${token.type.name.enumNameTitle()}' was unexpected for an expression. You might have made a typo or forgot a parentheses somewhere", token)
                }
            }
        }
        return expressionChain
    }

    val resultType: Type?
        get() = expressionChain.lastOrNull()?.generate()?.second

}
