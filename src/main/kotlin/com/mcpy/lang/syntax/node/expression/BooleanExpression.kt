package com.mcpy.lang.syntax.node.expression

import com.mcpy.lang.abstractions.Type
import com.mcpy.lang.errors.error
import com.mcpy.lang.errors.require
import com.mcpy.lang.lexer.token.*
import com.mcpy.lang.translation.context.Context

data class BooleanExpression(val tokens: List<Token>, override val firstToken: Token) : Expression(firstToken) {

    private data class ComparisonExpression(
        val leftSide: MutableList<Token>,
        val rightSide: MutableList<Token>,
        val logicalOperator: StringToken,
        override val firstToken: Token
    ) : Expression(firstToken) {
        override fun translate(context: Context): String {
            require(leftSide.isNotEmpty() && rightSide.isNotEmpty(), firstToken) {
                "A comparison must be comparing 2 values"
            }
            val template = when (val type = logicalOperator.type) {
                TokenType.IN, TokenType.IS -> {
                    require(rightSide.none { it.type == TokenType.NOT }, firstToken) {
                        "The 'not' keyword can't be used after the '${type.name.lowercase()}' keyword"
                    }
                    if (type == TokenType.IS) {
                        val first = rightSide.first()
                        require(rightSide.countRecursive() == 1 && first is StringToken && Type.toJava(first.value) != null, firstToken) {
                            "The 'is' keyword can only be used to check if a value is of a certain type"
                        }
                    }
                    if (leftSide.last().type == TokenType.NOT) {
                        leftSide.removeLast()
                        if (type == TokenType.IS) {
                            "!($0 instanceof $1)"
                        } else {
                            "!$1.contains$0"
                        }
                    } else {
                        if (type == TokenType.IS) {
                            "$0 instanceof $1"
                        } else {
                            "$1.contains($0)"
                        }
                    }
                }
                TokenType.BOOLEAN_OPERATOR -> {
                    require(rightSide.none { it.type == TokenType.NOT }, logicalOperator) {
                        "The 'not' keyword cannot be used after '${logicalOperator.value}'"
                    }
                    if (leftSide.first().type == TokenType.NOT) {
                        "!($0 ${logicalOperator.value} $1)"
                    } else {
                        "$0 ${logicalOperator.value} $1"
                    }
                }
                else -> {
                    error("Invalid true/false comparison: ${logicalOperator.value}", logicalOperator)
                }
            }
            // formats:
            // x == y           -> x == y
            // not x == y       -> !(x == y)
            // x is not type    -> !(x instanceof type)
            // x is type        -> x instanceof type
            // x is not None    -> x != None
            // x in y           -> y.contains(x)
            // not x in y       -> !y.contains(x)
            // x not in y       -> !y.contains(x)
            val leftSideExpression = GenericExpression(leftSide, firstToken).translate(context)
            val rightSideExpression = GenericExpression(rightSide, firstToken).translate(context)
            return template.replace("$0", leftSideExpression).replace("$1", rightSideExpression)
        }
    }

    private data class BooleanValueExpression(
        val callChain: List<Token>,
        override val firstToken: Token,
    ) : Expression(firstToken) {
        override fun translate(context: Context): String {
            require(callChain.isNotEmpty(), firstToken) {
                "A true/false expression is required"
            }
            val expr = GenericExpression(callChain, firstToken)
            require(expr.resultType?.type == "boolean", firstToken) {
                "A true/false expression is required, but this does not return a boolean value"
            }
            return expr.translate(context)
        }
    }

    init {
        println(this)
    }

    override fun translate(context: Context): String {
        // example: a and b and c    ->    a and (b and c)
        require(tokens.isNotEmpty(), firstToken) {
            "A true/false expression is required"
        }
        val builder = StringBuilder()
        if (tokens.countRecursive() == 1) {
            return handleBooleanExpression(tokens, context, true)
        }
        val booleanExpression = mutableListOf<Token>()
        for (token in tokens) {
            if (token == tokens.last()) {
                booleanExpression += token
            }
            if (token.type == TokenType.AND || token.type == TokenType.OR || token == tokens.last()) {
                if (token.type == TokenType.AND) {
                    builder.append(" && ")
                } else if (token.type == TokenType.OR) {
                    builder.append(" || ")
                }
                builder.append(handleBooleanExpression(booleanExpression, context, true))
                booleanExpression.clear()
            } else {
                booleanExpression += token
            }
        }
        return builder.toString()
    }

    private fun handleBooleanExpression(booleanExpression: List<Token>, context: Context, requireBooleanValue: Boolean): String {
        val comparisonOperators = listOf(TokenType.BOOLEAN_OPERATOR, TokenType.IS, TokenType.IN)
        val operatorIndex = booleanExpression.indexOfFirst { tok -> tok.type in comparisonOperators }
        if (operatorIndex == -1) {
            if (!requireBooleanValue) {
                return GenericExpression(booleanExpression, booleanExpression.first()).translate(context)
            }
            return BooleanValueExpression( // this represents something that returns a boolean
                booleanExpression,
                booleanExpression.first()
            ).translate(context)
        }
        require(booleanExpression.count { it.type in comparisonOperators } == 1, booleanExpression.first()) {
            "A true/false expression can only have one comparison operator"
        }
        return ComparisonExpression( // this has to be something like x > y
            booleanExpression.take(operatorIndex).toMutableList(),
            booleanExpression.drop(operatorIndex + 1).toMutableList(),
            booleanExpression[operatorIndex] as StringToken,
            booleanExpression.first()
        ).translate(context)
    }
}