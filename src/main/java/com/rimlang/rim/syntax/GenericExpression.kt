package com.rimlang.rim.syntax

import com.rimlang.rim.lexer.Token

data class GenericExpression(val tokens: List<Token>) : Expression() {

    init {
        println(this)
    }

    override fun translate(): String {
        TODO("not implemented")
    }

    val resultType: String
        get() = "null"
}