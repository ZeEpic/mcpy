package com.rimlang.rim.lexer

class StringToken(
    type: TokenType,
    override val value: String,
    line: Int,
    character: Int
) : Token(type, line, character)