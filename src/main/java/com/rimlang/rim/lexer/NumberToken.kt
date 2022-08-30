package com.rimlang.rim.lexer

class NumberToken(
    type: TokenType,
    override val value: Double,
    line: Int,
    character: Int
) : Token(type, line, character)