package com.rimlang.rim.syntax.node.expression

import com.rimlang.rim.lexer.token.Token
import com.rimlang.rim.translation.context.Context

data class BooleanExpression(val tokens: List<Token>, override val firstToken: Token) : Expression(firstToken) {
    init {
        println(this)
    }
    private fun parse(tokens: List<Token>): BooleanExpression {
        // example: a and b and c    ->    a and (b and c)
        TODO("Not yet implemented")
    }

    override fun translate(context: Context): String {
        return "true"
    }
}