package com.rimlang.rim.syntax

import com.rimlang.rim.lexer.Token

data class BooleanExpression(val tokens: List<Token>) : Expression() {
    init {
        println(this)
    }
    private fun parse(tokens: List<Token>): BooleanExpression {
        // example: a and b and c -> a and (b and c)
        TODO("Not yet implemented")
    }

    override fun translate(): String {
        return "true" //tokens.joinToString(" ") { it.value().toString() };
    }
}